package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

public interface ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that the given expression failed with the given state.
     */
    void handle(ParsingExpression pe, ParseState state);

    // ---------------------------------------------------------------------------------------------

    void reportErrors(Parser parser);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
