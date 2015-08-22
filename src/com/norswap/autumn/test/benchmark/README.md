# Setting Up The Benchmarks

## Dependencies

Add the following jar files to your classpath:

- mouse-1.6.1.jar
- parboiled-core-1.1.7.jar
- parboiled-java-1.1.7.jar
- asm-all-5.0.4.jar (parboiled dependency)
- xtc.jar (2.4.0)
- antlr-4.5-complete.jar

which you get from:

- http://sourceforge.net/projects/mousepeg/files/latest/download
- http://central.maven.org/maven2/org/parboiled/parboiled-java/1.1.7/parboiled-java-1.1.7.jar
- http://central.maven.org/maven2/org/parboiled/parboiled-core/1.1.7/parboiled-core-1.1.7.jar
- http://central.maven.org/maven2/org/ow2/asm/asm-all/5.0.4/asm-all-5.0.4.jar
- http://cs.nyu.edu/rgrimm/xtc/xtc.jar
- http://www.antlr.org/download/antlr-4.5-complete.jar

Now all the benchmark should be able to run.

# How the benchmarks were created

## Mouse

By generating a parser using Mouse's own Java 8 grammar:

    java -cp Mouse-1.6.1.jar mouse.Generate -G Java.1.8.peg -P MouseJava8Parser

Assuming the jar file and the grammar are in the working directory.

Grammar file: http://www.romanredz.se/papers/Java.1.8.peg

Note that a newer (1.7) version of Mouse was released shortly after the paper was accepted.
I did test it, and it performs worse than the 1.6.1 version.

## Parboiled

By extracting the Java 6 parser example from the parboiled repo:
https://github.com/sirthias/parboiled/tree/master/examples-java/src/main/java/org/parboiled/examples/java

## Rats (xtc)

By generating a parser using Rat's own Java 7 grammar:

    export JAVA_DEV_ROOT=.
    java -cp xtc.jar xtc.parser.Rats -in src src/xtc/lang/JavaSeven.rats

Assuming the working directory is the xtc source distribution
(http://cs.nyu.edu/rgrimm/xtc/xtc-core-2.4.0.zip) and that xtc.jar has been added to that directory.

## ANTLR4

By generating a parser using ANTLR's own Java 8 grammar:

    java -cp antlr-4.5-complete.jar org.antlr.v4.Tool -o java8 Java8.g4

Assuming the jar file and the grammar are in the working directory.

Grammar file: https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4

Since the Java8 grammar is documented to be slow (on account of being close to the Java spec), I
also generated a parser for Java 7:

    java -cp antlr-4.5-complete.jar org.antlr.v4.Tool -o java7 Java7.g4

Grammar file: https://github.com/antlr/grammars-v4/blob/master/java/Java.g4
