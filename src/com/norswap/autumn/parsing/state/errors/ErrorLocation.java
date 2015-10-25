package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;

/**
 * Indicates the location where an error occurred, defined by the input position, the parsing
 * expression that failed, and the repetition index. If the repetition index is n >= 0, i indicates
 * that the error location corresponds to the (n+1)th error encountered with the given (position,
 * expression) pair. If the index is negative, all occurrences with the given pair should be
 * considered.
 * <p>
 * If customizing the error state, users can subclass this class in order to supply more
 * information.
 */
public class ErrorLocation
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;

    public final int repetition;

    public final ParsingExpression pe;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ErrorLocation(int position, ParsingExpression pe)
    {
        this.position = position;
        this.repetition = -1;
        this.pe = pe;
    }

    // ---------------------------------------------------------------------------------------------

    public ErrorLocation(int position, int repetition, ParsingExpression pe)
    {
        this.position = position;
        this.repetition = repetition;
        this.pe = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
