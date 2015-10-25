package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

/**
 * Invokes {@link Parser#whitespace} at its start position.
 *
 * Always succeeds.
 *
 * On success, its end position is the end position of the whitespace expression.
 */
public final class Whitespace extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        int end = parser.whitespace.parseDumb(parser, state.end);

        if (end > 0)
        {
            state.end = end;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
