# If any prerequisites are newer than the target, make rebuilds the target. This
# is problematic when unpacking an archive, as an archive file will always be
# newer than its contents. We use order-only prerequisites to work around this.
# See: https://www.gnu.org/software/make/manual/make.html#Prerequisite-Types

data: data/ml-latest-small data/hadoop-3.1.1

data/ml-latest-small: | data/ml-latest-small.zip
	unzip -d data data/ml-latest-small.zip

data/ml-latest-small.zip:
	scripts/install-ml-latest-small-zip.sh

data/hadoop-3.1.1: | data/hadoop-3.1.1.tar.gz
	tar -xz --directory=data -f data/hadoop-3.1.1.tar.gz

data/hadoop-3.1.1.tar.gz:
	scripts/install-hadoop-tar-gz.sh
