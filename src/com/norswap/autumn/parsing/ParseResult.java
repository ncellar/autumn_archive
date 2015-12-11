package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.errors.ErrorReport;
import com.norswap.util.Array;

/**
 * [Immutable] The user-facing result of a parse.
 */
public final class ParseResult
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Whether the parse succeeded matching the whole input.
     */
    public final boolean matched;

    /**
     * Whether the parse succeeded matching a prefix (or all) of the input.
     */
    public final boolean succeeded;

    /**
     * The input position where the match ends. Equal to the input size if {@code matched},
     * undefined if {@code !succeeded}.
     */
    public final int endPosition;

    /**
     * The generated parse tree, or null if {@code !succeeded}. If you do not specify any captures
     * in the grammar, this tree is empty when the parse succeeds.
     */
    public final ParseTree tree;

    /**
     * If {@code !matched}, holds error information and diagnostic about the parse.
     * Undefined otherwise.
     */
    public final ErrorReport error;

    /**
     * Changes that were applied to the custom state during the parse.
     */
    public final Array<Object> customChanges;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseResult(
        boolean matched,
        boolean succeeded,
        int endPosition,
        ParseTree tree,
        Array<Object> customChanges,
        ErrorReport error)
    {
        this.matched = matched;
        this.succeeded = succeeded;
        this.endPosition = endPosition;
        this.tree = tree;
        this.error = error;
        this.customChanges = customChanges;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
