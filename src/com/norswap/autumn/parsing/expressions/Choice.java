package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.expressions.common.NaryParsingExpression;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.nullability.Nullability;

/**
 * Invokes all its operands at its initial input position, until one succeeds.
 *
 * Succeeds iff one operand succeeds.
 *
 * On success, its end position is that of the operand that succeeded.
 */
public final class Choice extends NaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.succeeded())
            {
                return;
            }
            else
            {
                state.resetOutput();
            }
        }

        parser.fail(this, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        for (ParsingExpression operand : operands)
        {
            int result = operand.parseDumb(parser, position);

            if (result != - 1)
            {
                return result;
            }
        }

        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] firsts()
    {
        return operands;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Nullability nullability()
    {
        return Nullability.any(this, operands);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
