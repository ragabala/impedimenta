# If any prerequisites are newer than the target, make rebuilds the target. In
# this case, the behaviour is problematic, as the zip file will always be newer
# than its contents. Use an order-only prerequisite to work around this. See:
# https://www.gnu.org/software/make/manual/make.html#Prerequisite-Types
data/ml-latest-small: | data/ml-latest-small.zip
	unzip -d data data/ml-latest-small.zip

data/ml-latest-small.zip:
	scripts/get-ml-latest-small-zip.sh $@
