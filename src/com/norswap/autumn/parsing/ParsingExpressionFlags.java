package com.norswap.autumn.parsing;

import com.norswap.util.FlagFactory;

/**
 * Defines standard flags to be used in the {@link ParsingExpression#flags} field and allows users
 * to register flags for their own uses.
 * <p>
 * This is not synchronized, so take care if concurrent access is required.
 */
public final class ParsingExpressionFlags
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final FlagFactory factory = new FlagFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * (For {@code Capture} only) Indicates a capture should be performed.
     */
    public static final int PEF_CAPTURE
        = factory.next();

    /**
     * (For {@code Capture} only) Indicates the matched text should be captured.
     */
    public static final int PEF_CAPTURE_TEXT
        = factory.next();

    /**
     * (For {@code Capture} only) Indicates that captures should be added to a group corresponding
     * to their accessor.
     */
    public static final int PEF_CAPTURE_GROUPED
        = factory.next();

    /**
     * Indicates that the parsing expression shouldn't be printed. The parsing expression
     * sporting this flag should only have a single child expression.
     */
    public static final int PEF_UNARY_INVISIBLE
        = factory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a flag mask (a 1 shifted by a certain offset) that will be reserved to the caller.
     */
    int register()
    {
        return factory.next();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
