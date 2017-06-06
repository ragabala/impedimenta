# Author: Jeremy Audet
#
# The `lib` directory must either contain or link to the jar for
# net.sf.jopt-simple. Tested with version 5.0.3. See:
# http://search.maven.org/#search|gav|1|g%3A%22net.sf.jopt-simple%22
CLASSPATH:="lib/*:$$CLASSPATH"

help:
	@echo "Please use \`make <target>' where <target> is one of:"
	@echo "  help"
	@echo "    to show this message"
	@echo "  docs"
	@echo "    to compile API documentation"
	@echo "  compile"
	@echo "    to compile the application"
	@echo "  run-no-opts"
	@echo "    to run the application with no options"
	@echo "  run-help"
	@echo "    to run the application with the --help option"
	@echo "  run-class"
	@echo "    to run the application with a class"
	@echo "  run-bad-class"
	@echo "    to run the application with a non-existent class"
	@echo "  run-all"
	@echo "    to run the application with all available options"

compile: edu/vtc/cis3720/jxa03200/Main.class

docs:
	CLASSPATH=$(CLASSPATH) javadoc edu.vtc.cis3720.jxa03200 -d docs

run-no-opts: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Main

run-help: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Main --help

run-class: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Main edu.vtc.cis3720.jxa03200.Main

run-bad-class: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Main foo

run-all: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Main --issue REQUIREMENTS edu.vtc.cis3720.jxa03200.Main

edu/vtc/cis3720/jxa03200/Issue.class: edu/vtc/cis3720/jxa03200/Issue.java

edu/vtc/cis3720/jxa03200/Main.class: edu/vtc/cis3720/jxa03200/Main.java \
		edu/vtc/cis3720/jxa03200/Review.class \
		edu/vtc/cis3720/jxa03200/ReviewRequired.class

edu/vtc/cis3720/jxa03200/Review.class: edu/vtc/cis3720/jxa03200/Review.java \
		edu/vtc/cis3720/jxa03200/Reviews.class

edu/vtc/cis3720/jxa03200/ReviewRequired.class: edu/vtc/cis3720/jxa03200/ReviewRequired.java \
		edu/vtc/cis3720/jxa03200/ReviewsRequired.class \
		edu/vtc/cis3720/jxa03200/Issue.class

edu/vtc/cis3720/jxa03200/Reviews.class: edu/vtc/cis3720/jxa03200/Reviews.java

edu/vtc/cis3720/jxa03200/ReviewsRequired.class: edu/vtc/cis3720/jxa03200/ReviewsRequired.java

.PHONY: help compile run-no-opts run-help run-class run-bad-class run-all

.SUFFIXES: .java .class
.java.class:
	CLASSPATH=$(CLASSPATH) javac -Xlint $(@:.class=.java)
