o?=dev
LIBS?=
OUTDIR?=out/$o
MVN_OUTPUT?=deps/fetched

ifeq ($(OS),Windows_NT)
	SEP=;
else
	SEP=:
endif

ifeq ($o,debug)
	DEBUG=-g
else ifeq ($o,opti)
	DEBUG=-g:none
endif

define MANUAL_TEXT
build

    Compiles all java files in 'src', as well as all java files in the
    directories listed in the LIBS variable.

    The generated class files are put in the output directory. This
    directory can be set via the OUTDIR variable and default to 'out/$o'.

    'o' is the build type variable. Setting this to 'debug' will cause local
    variables to be included in the class files debug information. Using
    'opti' will disable *all* debugging information. Defaults to 'dev'.

clean

    Deletes OUTDIR. Recall OUTDIR default to 'out/$o', and 'o' is the build
    type variable.

run

    Runs the class indicate in variable 't' (e.g. 'my.pkg.Main'), passing
    the variable 'a' as argument (optional).

trace

    Same as 'run', but uses hprof to benchmark the CPU use of the program.
    The results are stored in the file 'java.hprof.txt'.

    For more info:
    https://docs.oracle.com/javase/8/docs/technotes/guides/
        troubleshoot/tooldescr008.html

fetch

    Fetches the maven artifact (e.g. jar file) specified in the 'a' variable, in
    Gradle format. It does not fetch the dependencies of the artifact
    transitively.

    The downloaded files are put in the directory indicated by the
    MVN_OUTPUT variable, defaulting to 'deps/fetched'.

fetchpom

    Fetches all the jar dependencies listed in 'deps.xml' and fetches their
	dependencies transitively.

    The downloaded files are put in '$MVN_OUTPUT/jar', where MVN_OUTPUT is a
	variable defaulting to 'deps/fetched'.

fetchdev

	Fetches the sources and javadoc artifacts corresponding to the jars
	listed in 'deps.xml' and their transitive dependencies.

	The downloaded files are put in '$MVN_OUTPUT/src' and '$MVN_OUTPUT/jdoc',
	where MVN_OUTPUT is a variable defaulting to 'deps/fetched'.

endef

# NOTE(norswap): no idea why this voodoo dance works, but naive attempts were
# unsuccessful. Kudos http://stackoverflow.com/questions/7281395

export MANUAL=$(MANUAL_TEXT)

help:
	@echo "$$MANUAL"

# NOTE(norswap): The quotes around "deps/*" are necessary to avoid shell
# wildcard expansion. The wildcard must be processed by javac.

build:
	mkdir -p $(OUTDIR)
	cp -R resources/* $(OUTDIR)
	javac -Xlint:unchecked $(DEBUG) -d $(OUTDIR) -cp "deps/*" `find src $(LIBS) -name *.java`

clean:
	rm -rf $(OUTDIR)

run:
	java -cp "$(OUTDIR)$(SEP)deps/*$(SEP)$(OUTDIR)/resources" $t $a

trace:
	java -cp "$(OUTDIR)$(SEP)deps/*$(SEP)$(OUTDIR)/resources" -agentlib:hprof=cpu=samples $t $a

fetch:
	mkdir -p $(MVN_OUTPUT)
	mvn org.apache.maven.plugins:maven-dependency-plugin:2.10:get \
		-DrepoUrl=http://repo1.maven.org/maven2/ \
		-Ddest=$(MVN_OUTPUT)
		-Dartifact=$a

fetchpom:
	mvn dependency:copy-dependencies -f deps.xml \
		-DoutputDirectory=$(MVN_OUTPUT)/jar

fetchdev:
	mvn dependency:copy-dependencies -f deps.xml \
		-DoutputDirectory=$(MVN_OUTPUT)/jdoc -Dclassifier=sources
	mvn dependency:copy-dependencies -f deps.xml \
		-DoutputDirectory=$(MVN_OUTPUT)/src -Dclassifier=javadoc

.PHONY: \
	help \
	build \
	clean \
	run \
	trace \
	fetch \
	fetchpom \
	fetchdev
