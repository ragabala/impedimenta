#!/usr/bin/env bash
# coding=utf-8
#
# Clone and customize several libvirt domains.
#
# Requirements:
#
# * The user must belong to the libvirt group. This is needed for passwordless
#   access to libvirt.
# * The user must be root or have passwordless sudo privileges. This is needed
#   to alter disk image permissions. If the user has password-protected sudo
#   privileges, this script will still work, but a human may need to babysit.
# * virsh, virt-clone and virt-sysprep must be available.
set -euo pipefail

# ------------------------------------------------------------------------------
# Argument parsing and core business logic
# ------------------------------------------------------------------------------

# Parse arguments and execute business logic.
main() {
    # Transform $@. ($temp is needed. If omitted, non-zero exit codes are
    # ignored.)
    check_getopt
    local temp
    temp=$(getopt \
        --options '' \
        --longoptions help,public-key: \
        --name clone-customize-domains \
        -- "$@")
    eval set -- "$temp"
    unset temp

    # Read args. (getopt sets $1 to -- even when no args are passed.)
    if [ "${#@}" -eq 1 ]; then show_help; exit 1; fi
    local public_key bases suffixes
    while true; do
        case "$1" in
            --public-key) public_key="$(realpath --canonicalize-existing "$2")"; shift 2;;
            --help) show_help; return;;
            --) shift; break;;
            *) echo >&2 "Internal error! Encountered unexpected argument: $1"; exit 1;;
        esac
    done
    if [ -z "${1:-}" ]; then echo >&2 'Missing parameter <bases>.'; exit 1; fi
    if [ -z "${2:-}" ]; then echo >&2 'Missing parameter <suffixes>.'; exit 1; fi
    mapfile -t -d ',' bases < <(echo -n "$1")
    mapfile -t -d ',' suffixes < <(echo -n "$2")
    shift 2

    # Set non-argument variables.
    readonly local uid=$(id -u)

    # Execute business logic.
    #
    # Local variables are visible to child functions. Scope creep could be
    # combatted by moving each child function into a separate script or by using
    # a safer programming language.
    stop_bases
    delete_clones
    create_clones
    customize_clones
    start_clones
}

show_help() {
fmt <<EOF
Usage: clone-customize-domains [options] <bases> <suffixes>

Stop base domains, delete conflicting clones, and create, customize and start
new clones.

<bases> is a comma-separated list of domains. They are used as source material
when creating new domains. <suffixes> is a comma-separated list of strings used
to name new domains. For each base in <bases>, and for each suffix in
<suffixes>, a new domain is created. For example, if this script is called like
so:

    clone-customize-domains -- foo,bar -biz,-baz

...then domains named "foo-biz", "foo-baz", "bar-biz" and "bar-baz" are created.
The exact steps are as follows:

EOF
fmt --crown-margin <<EOF
1. Shut down base
   domains.
EOF
fmt --crown-margin <<EOF
2. Delete all conflicting clones. (e.g. delete "foo-biz," "foo-baz," "bar-biz"
   and "bar-baz.")
EOF
fmt --crown-margin <<EOF
3. Create
   clones.
EOF
fmt --crown-margin <<EOF
4. Customize the clones. (e.g. Inject a public SSH
   key.)
EOF
fmt --crown-margin <<EOF
5. Start the
   clones.
EOF
fmt <<EOF

Options:
    --help
        Show this message.
    --public-key <path>
        Copy the named public SSH key into each domain as the root user's
        ~/.ssh/authorized_keys file.
EOF
}

# Verify that getopt(1) supports modern option parsing.
check_getopt() {
    if [ "$(getopt --test || true)" != '' ]; then
        fmt 1>&2 <<EOF
An old version of getopt is installed. Its limitations include being unable to
cope with whitespace and other shell-specific special characters. Please upgrade
getopt, or execute this script on a more up-to-date system.

Execute 'getopt --test' to see if getopt is new enough. A modern getopt will
print no output and return 4. A traditional getopt will print '--' and return 0.
For more information, see getopt(1).

This script will now exit, so as to avoid possibly causing damage.
EOF
        exit 1
    fi
}

# ------------------------------------------------------------------------------
# Auxiliary functions
# ------------------------------------------------------------------------------

# Shut down base domains.
#
# Wait until all stopped domains are fully shut down before returning. If
# domains take more than five minutes to shut down, stop waiting and return 1.
#
# Arguments: bases
stop_bases() {
    local base domains running_bases shutoff_bases start_time elapsed
    local -r time_limit=300

    # Issue shut down commands.
    _stop_bases_begin_msg
    for base in "${bases[@]}"; do
        virsh --quiet shutdown "${base}" &
    done
    wait

    # Wait for domains to shut down.
    start_time="$(date +%s)"
    while true; do
        domains="$(virsh --quiet list --state-shutoff)"
        current_time="$(date +%s)"
        elapsed="$(( current_time - start_time ))"
        running_bases=()
        shutoff_bases=()
        for base in "${bases[@]}"; do
            if echo "$domains" | grep "$base" >/dev/null; then
                shutoff_bases+=("$base")
            else
                running_bases+=("$base")
            fi
        done
        if [ "${#shutoff_bases[@]}" -eq "${#bases[@]}" ]; then
            _stop_bases_end_msg
            break
        fi
        _stop_bases_update_msg
        if [ "$elapsed" -gt "$time_limit" ]; then
            _stop_bases_timeout_msg
            return 1
        fi
        sleep 5
    done
}

_stop_bases_begin_msg() {
echo "Shutting down ${#bases[@]} domains: ${bases[*]}"
}

_stop_bases_update_msg() {
paste --serial --delimiters=' ' - <<EOF
(${elapsed}s/${time_limit}s) Waiting for ${#running_bases[@]} domains to shut
down: ${running_bases[*]}
EOF
}

_stop_bases_timeout_msg() {
paste --serial --delimiters=' ' - >&2 <<EOF
(${elapsed}s/${time_limit}s) Still waiting for ${#running_bases[@]} domains
to shut down. Aborting. Problematic domains: ${running_bases[*]}
EOF
}

_stop_bases_end_msg() {
echo "Shut down ${#bases[@]} domains: ${bases[*]}"
}

# Destroy and undefine domains, if possible.
#
# Arguments: bases, suffixes
delete_clones() {
    local base suffix
    local clones=()
    for base in "${bases[@]}"; do
        for suffix in "${suffixes[@]}"; do
            clones+=("${base}${suffix}")
        done
    done
    _delete_clones_begin_msg
    for clone in "${clones[@]}"; do
        _delete_clone "$clone" &
    done
    wait
    _delete_clones_end_msg
}

# Destroy and undefine a domain, if possible.
#
# All arguments should be passed as positional arguments. Doing so prevents
# concurrency issues. (What happens if this child function references a variable
# in the parent function, and the variable changes?)
#
# Arguments: domain (positional)
_delete_clone() {
    # We could use `virsh list --all` to check if the targeted domains exist
    # before nuking them. However, that approach is subject to race conditions.
    # Better to ask for forgiveness than permission.
    virsh --quiet destroy "$1" || true
    virsh --quiet undefine "$1" --remove-all-storage || true
}

_delete_clones_begin_msg() {
echo "Deleting ${#clones[@]} domains: ${clones[*]}"
}

_delete_clones_end_msg() {
echo "Deleted ${#clones[@]} domains: ${clones[*]}"
}

# Clone domains.
#
# Arguments: bases, suffixes
create_clones() {
    # We avoid parallelization to avoid naming conflicts. `virt-clone
    # --auto-clone` auto-generates disk image paths (among other things), and
    # when done in parallel, identical paths can be auto-generated.
    #
    # That said, empirical testing on a system with a cheap SSD shows that
    # cloning two domains in parallel reduces exection time from 13 seconds to
    # 12 or 11 seconds. This isn't much, and different tests might produce
    # different results. (What would happen with traditional hard drives?)
    # Cloning in parallel also raises concerns about file fragmentation - which
    # *does* affect SSDs - but bear in mind that qcow2 disks are sparsely
    # allocated, making some fragmentation inevitable.
    local base suffix
    for base in "${bases[@]}"; do
        for suffix in "${suffixes[@]}"; do
            virt-clone \
                --original "$base" \
                --name "${base}${suffix}" \
                --auto-clone
        done
    done
}

# Make permissions of disk images for cloned domains more lax.
#
# Specifically, set g+rw and a group of $(id --group for each cloned domain. By
# default, libvirt changes disk image permissions at various times, such as when
# a domain starts. Thus, the effects of this function will be overwritten.
#
# By default, system-wide disk images have restrictive permissions, like
# "-rw------- root root". virt-sysprep needs root privileges to touch such
# files. A better solution is to run virt-sysprep as a normal user and make the
# disk image permissions more lax.
#
# Arguments: bases, suffixes, uid
_set_disk_permissions() {
    local cmd path paths base suffix
    for base in "${bases[@]}"; do
        for suffix in "${suffixes[@]}"; do
            # Sample `virsh --quiet domblklist` output:
            #
            #     vda        /var/lib/libvirt/images/fedora-23.qcow2
            #     hda        -
            #
            # I've never seen a device name (traditional or udev) containing
            # whitespace, and libvirt doesn't allow whitespace in disk image
            # paths, so it's probably OK to split input on whitespace.
            mapfile -t paths < <(virsh --quiet domblklist "${base}${suffix}" | awk '{print $2}')
            for path in "${paths[@]}"; do
                if [ "$path" == '-' ]; then
                    continue
                fi
                echo "Setting permissions on $path"
                # shellcheck disable=SC2016
                {
                cmd=''
                if [ "$uid" != '0' ]; then cmd+='sudo '; fi
                cmd+='chown :"$(id --group)" "$path" '
                eval "$cmd"

                cmd=''
                if [ "$uid" != '0' ]; then cmd+='sudo '; fi
                cmd+='chmod g+rw "$path" '
                eval "$cmd"
                }
            done
        done
    done
}

# Customize the cloned domains. Set a hostname, etc.
#
# A cloned RHEL 6 VM will "remember" its original eth0 network interface and
# name its new one eth1. This breaks network connectivity. virt-sysprep wipes
# that memory, unlike virt-customize. But beware that virt-sysprep also wipes
# subscription-manager information.
#
# Arguments: bases, suffixes, public_key
customize_clones() {
    if virsh uri | grep system >/dev/null; then
        _set_disk_permissions
    fi
    local base suffix cmd
    for base in "${bases[@]}"; do
        # shellcheck disable=SC2016
        for suffix in "${suffixes[@]}"; do
            cmd='virt-sysprep '
            cmd+='--domain "${base}${suffix}" '
            cmd+='--hostname "${base}${suffix}" '
            if [ -n "${public_key:-}" ]; then
                cmd+='--ssh-inject root:file:"$public_key" '
                cmd+='--selinux-relabel '
            fi
            cmd+='&'
            eval "$cmd"
        done
    done
    wait
}

# Start cloned domains.
#
# Arguments: bases, suffixes
start_clones() {
    local base suffix
    for base in "${bases[@]}"; do
        for suffix in "${suffixes[@]}"; do
            # `virsh start` breaks if parallelized.
            virsh start "${base}${suffix}"
        done
    done
}

main "$@"
