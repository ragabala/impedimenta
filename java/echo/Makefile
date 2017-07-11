# The 'lib' directory must either contain or link to net.sf.jopt-simple. Tested
# with version 5.0.3. See: http://search.maven.org/
CLASSPATH:="lib/*:$$CLASSPATH"

help:
	@echo "Please use \`make <target>' where <target> is one of:"
	@echo "  help"
	@echo "    to show this message"
	@echo "  docs"
	@echo "    to compile API documentation"
	@echo "  compile"
	@echo "    to compile the application"
	@echo "  run-help"
	@echo "    to run the application with the --help argument"
	@echo "  run-args"
	@echo "    to run the application several times, with varying arguments"
	@echo "  run-no-args"
	@echo "    to run the application with no arguments"
	@echo "  run-bad-arg"
	@echo "    to run the application with the (invalid) --bad argument"

compile: edu/vtc/cis3720/jxa03200/Echo.class

docs:
	CLASSPATH=$(CLASSPATH) javadoc *.java -d docs

run-help: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo --help

run-args: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo foo bar
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo 'foo bar'
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo --upper-case foo
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo foo --upper-case
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo --help foo
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo -- --help
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo --upper-case -- --help

run-no-args: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo

run-bad-arg: compile
	CLASSPATH=$(CLASSPATH) java edu.vtc.cis3720.jxa03200.Echo --bad

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac -Xlint $<

.PHONY: help docs compile run-help run-args run-no-args run-bad-arg
