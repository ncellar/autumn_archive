# Autumn: Extensible Parsing Combinators

## Documentation

Soon: a user manual. You can send me angry emails if I procrastinate for too
long.

In the meantime, dig around [the Autumn class][autumn] for documented entry
points into the parser. There's also [a good example][java-example] that uses
the bundled Java grammar.

For the internals, [the ParseState class][parse-state] class has a long
explanation of the concepts underlying parse state handling. For writing custom
parsing expressions, look at [the ParsingExpression class][parsing-exp].

[autumn]: https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/Autumn.java

[java-example]:
https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/test/languages/JavaTest.java

[parse-state]:
https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/parsing/state/ParseState.java

[parsing-exp]:
https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/parsing/ParsingExpression.java

## Building

    make fetchpom
    rm -rf deps/jar
    mv deps/fetched/jar deps/jar
    make build

... will output the class files under the `out/dev` directory.

## Running Tests

    make run t=MAIN_CLASS

e.g.,

    make run t=com.norswap.autumn.test.languages.JavaTest
    make run t=com.norswap.autumn.test.languages.PythonTest
    
Note that running these will require to download some source code for the test to run over and to
edit the test classes to set the proper path to the sources.

We used [Guava][guava] to test Java and [Django][django] to test Python.

[guava]: https://github.com/google/guava/releases/tag/v18.0-rc2
[django]: https://github.com/django/django/releases/tag/1.9
