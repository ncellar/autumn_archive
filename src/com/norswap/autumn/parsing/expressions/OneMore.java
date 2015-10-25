package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.state.ParseStateSnapshot;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

/**
 * Repeatedly invokes its operand over the input, until it fails.
 *
 * Succeeds if its operand succeeded at least once.
 *
 * On success, its end position is the end position of the last successful
 * invocation of its operand.
 */
public final class OneMore extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseStateSnapshot snapshot = state.snapshot();

        operand.parse(parser, state);

        if (state.failed())
        {
            state.restore(snapshot);
            state.fail(this);
            return;
        }
        else
        {
            state.commit();
        }

        while (true)
        {
            operand.parse(parser, state);

            if (state.failed())
            {
                state.discard();
                break;
            }

            state.commit();
        }

        state.uncommit(snapshot);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        position = operand.parseDumb(parser, position);

        if (position == -1)
        {
            return -1;
        }

        int result;

        while ((result = operand.parseDumb(parser, position)) != -1)
        {
            position = result;
        }

        return position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
