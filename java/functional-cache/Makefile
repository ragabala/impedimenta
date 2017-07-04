# Author: Jeremy Audet
#
# The `lib` directory must either contain or link to:
#
# * junit.jar, from org.junit
# * hamcrest-core.jar, from org.hamcrest
CLASSPATH:="lib/*:$$CLASSPATH"

help:
	@echo "Please use \`make <target>' where <target> is one of:"
	@echo "  help"
	@echo "    to show this message"
	@echo "  compile"
	@echo "    to compile the application"
	@echo "  docs"
	@echo "    to compile API documentation"
	@echo "  test"
	@echo "    to execute unit tests"

compile: edu/vtc/cis3720/jxa03200/Cache.class

docs:
	CLASSPATH=$(CLASSPATH) javadoc edu.vtc.cis3720.jxa03200 -d docs

test: edu/vtc/cis3720/jxa03200/TestCache.class
	CLASSPATH=$(CLASSPATH) java org.junit.runner.JUnitCore edu.vtc.cis3720.jxa03200.TestCache

edu/vtc/cis3720/jxa03200/Cache.class: edu/vtc/cis3720/jxa03200/Cache.java

edu/vtc/cis3720/jxa03200/TestCache.class: edu/vtc/cis3720/jxa03200/TestCache.java \
	edu/vtc/cis3720/jxa03200/Cache.class

.PHONY: help compile test

.SUFFIXES: .java .class
.java.class:
	CLASSPATH=$(CLASSPATH) javac -Xlint $(@:.class=.java)
