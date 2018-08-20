CPU_COUNT=$(shell nproc)
FIND_PATHS='(' -path ./inventory -o -path './host_vars/*' -o -type f -name '*.yml' ')'

help:
	@echo "Please use \`make <target>' where <target> is one of:"
	@echo "  help               to show this message"
	@echo "  lint               to run all linters"
	@echo "  lint-ansible-lint  to run ansible-lint"
	@echo "  lint-pylint        to run pylint"
	@echo "  lint-syntax-check  to run ansible-playbook --syntax-check"
	@echo "  lint-yamllint      to run yamllint"

lint: lint-syntax-check lint-ansible-lint lint-pylint lint-yamllint

# ANSIBLE0017: become_user requires become to work as expected
#
# ansible-lint doesn't know how to deal with the case where `become_user: ...`
# is applied directly to a task and `become: true` is applied to a task via a
# block statement.
lint-ansible-lint:
	ansible-lint -x ANSIBLE0017 site.yml

lint-pylint:
	find . -type f -name '*.py' -print0 | xargs -0 pylint -j $(CPU_COUNT)

lint-syntax-check:
	ansible-playbook site.yml --syntax-check

lint-yamllint:
	yamllint --strict .

.PHONY: help lint lint-ansible-lint lint-pylint lint-syntax-check lint-yamllint
