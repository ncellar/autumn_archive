# The Autumn Parsing Library

## Autumn has moved to https://github.com/norswap/autumn

## Legacy Versions

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

- **v3** - https://github.com/ncellar/whimsy

v3  is a rewrite of v2, still in Kotlin. It was part of the Whimsy compiler framework project, whose
other component (a reactive middle-end compiler library) never fuly materialized.

v3 features two significant changes: first the context-sensitive parsing mechanism now uses a log of
undoable changes instead of relying on state snapshots. Second, the framework uses Kotlin's `inline`
keyword pervasively in order to avoid megamorphic call sites overheads, and enable additional
optimization. This works well in practice, and performance are greatly improved.