# If any prerequisites are newer than the target, make rebuilds the target. This
# is problematic when unpacking an archive, as an archive file will always be
# newer than its contents. We use order-only prerequisites to work around this.
# See: https://www.gnu.org/software/make/manual/make.html#Prerequisite-Types

data: data/ml-latest-small data/hadoop-3.1.1

data/ml-latest-small:
	scripts/install-ml-latest-small.sh

data/hadoop-3.1.1:
	scripts/install-hadoop.sh
