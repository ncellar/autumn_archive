package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

/**
 * Repeatedly invokes its operand over the input, until it fails. Each invocation occurs at
 * the end position of the previous one.
 *
 * Always succeeds.
 *
 * On success, its end position is the end position of the last successful invocation of its
 * operand.
 */
public final class ZeroMore extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        final ParseState down = new ParseState(state);

        while (true)
        {
            operand.parse(parser, down);

            if (down.failed())
            {
                down.resetOutput();
                break;
            }

            down.advance();
        }

        state.merge(down);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        int result;

        while ((result = operand.parseDumb(parser, position)) != -1)
        {
            position = result;
        }

        return position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
