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

[java-example]: https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/test/parsing/JavaGrammarTest.java

[parse-state]:
https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/parsing/state/ParseState.java

[parsing-exp]: https://github.com/norswap/autumn/blob/master/src/com/norswap/autumn/parsing/ParsingExpression.java

## Building

    make build

... will output the class files under the `out/dev` directory.
