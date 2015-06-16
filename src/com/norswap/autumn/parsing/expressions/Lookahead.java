package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.autumn.parsing.graph.nullability.Nullability;

/**
 * Invokes its operand on the input, then resets the input to its initial position.
 *
 * Succeeds iff its operand succeeds.
 *
 * On success, its end position is its start position.
 */
public final class Lookahead extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.succeeded())
        {
            state.resetOutput();
        }
        else
        {
            parser.fail(this, state);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position) == -1
            ? -1
            : position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
