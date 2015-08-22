package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

/**
 * This class exposes a few handy whitespace parsing expression; foremost amongst which is the
 * default whitespace expression ({@link #DEFAULT()}).
 * <p>
 * The parsing expression returned by the methods of this class are unique (i.e. they can be
 * freely modified) but they contain unresolved references!
 */
public final class Whitespace
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final ParsingExpression

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The default whitespace. This is roughly similar to the whitespace accepted by C/Java/C# etc.
     * It does however allow for nested block comments, and forbids weird whitespace characters
     * such as the vertical tab.
     */
    public static ParsingExpression DEFAULT()
    {
        return whitespace.deepCopy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * An expression that matches the end of the input (EOF = End Of File).
     */
    public static ParsingExpression EOF()
    {
        return EOF.deepCopy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * An expression that matches the end of a line (newline or EOF) (EOL = End Of Line).
     */
    public static ParsingExpression EOL()
    {
        return EOL.deepCopy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * An expression that matches a line (// ...) comment.
     */
    public static ParsingExpression lineComment()
    {
        return lineComment.deepCopy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * An expression that matches a block (/** ... <span>*</span>/) comment.
     * Block comments can be nested.
     */
    public static ParsingExpression blockComment()
    {
        return blockComment.deepCopy();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * An expression that matches whitespace characters (space, tabs & newlines).
     */
    public static ParsingExpression whitespaceChars()
    {
        return whitespaceChars.deepCopy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
