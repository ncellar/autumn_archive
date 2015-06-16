package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

/**
 * This class exposes the default whitespace expression ({@link #whitespace}) as well as a few of
 * its sub-expressions that can be useful when building custom whitespace expressions.
 */
public final class Whitespace
{
    public static final ParsingExpression

    EOF = named$("EOF", not(any())),

    EOL = named$("EOL", choice(literal("\n"), EOF)),

    lineComment =
        named$("lineComment", sequence(
            literal("//"),
            zeroMore(
                not(EOL),
                any()))),

    blockComment =
        named$("blockComment", sequence(
            literal("/*"),
            zeroMore(choice(
                reference("blockComment"),
                sequence(
                    not(literal("*/")),
                    any()))),
            literal("*/"))),

    whitespaceChars =
        named$("whitespaceChars", charSet("  \n\t")),

    whitespace =
        named$("whitespace", zeroMore(choice(
            whitespaceChars,
            lineComment,
            blockComment)))

    ;
}
