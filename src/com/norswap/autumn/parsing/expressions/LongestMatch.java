package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.expressions.abstrakt.NaryParsingExpression;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

import java.util.function.Predicate;

/**
 * Invokes all its operands at its initial input position.
 *
 * Succeeds if at least one of its operands succeeds.
 *
 * On success, its end position is the largest amongst the end positions of its
 * successful operands.
 */
public final class LongestMatch extends NaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseChanges farthestChanges = ParseChanges.failure();

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.end > farthestChanges.end)
            {
                farthestChanges = state.extract();
            }

            state.discard();
        }

        state.merge(farthestChanges);

        if (state.failed())
        {
            state.fail(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        int farthestPosition = -1;

        for (ParsingExpression operand: operands)
        {
            int result = operand.parseDumb(parser, position);

            if (result > farthestPosition)
            {
                farthestPosition = result;
            }
        }

        return farthestPosition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.any(this, operands);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return operands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
