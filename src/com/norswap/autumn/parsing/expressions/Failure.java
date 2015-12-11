package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * A parsing expression that always fails.
 */
public class Failure extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        state.fail(this);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
