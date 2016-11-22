# The Autumn Parsing Library

Soon, this repository will hold the new (third) version of the Autum parsing
library. The new version should run considerably faster than the previous
version.

Autumn is developed as part of the [Whimsy] compiler framework.
The newest developments will land in the Whimsy repository first.

[whimsy]: https://github.com/norswap/whimsy

## Presentation

Autumn is a parsing library written in the [Kotlin] programming language.

One of its specificities is its ability to parse context-sensitive languages
through the introduction of *parse state*, which may be mutated during the parse.

[Kotlin]: https://kotlinlang.org/

## History

- **v1** - https://github.com/ncellar/autumn_v1

The initial version of Autumn is an extensible parser-combinator library built
upon the PEG formalism. It was the first general PEG parsing library to support
left recursion with both left- and right-associative interpretations. It also
includes support for precedence and associativity.

It was the object
of [a paper (Parsing Expression Grammars Made Practical)][SLE2015] at the SLE
(Software Language Engineering) in 2015.

[SLE2015]: http://norswap.com/pubs/sle2015.pdf

- **v2** - https://github.com/ncellar/autumn_v2

v2 is a considerably simplified rewrite of v1 in the Kotlin programming
language.

It was the object of
[a paper (Taming Context-Sensitive Languages with Principled Stateful Parsing)][SLE2016] at
the SLE (Software Language Engineering) in 2016.

[SLE2016]: http://norswap.com/pubs/sle2016.pdf

- **v3** - soon
