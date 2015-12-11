package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * A parsing expression that succeeds without consuming any input.
 */
public class Success extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
