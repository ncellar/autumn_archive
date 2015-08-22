package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.NaryParsingExpression;
import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

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
        OutputChanges farthestChanges = OutputChanges.failure();

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.end > farthestChanges.end)
            {
                farthestChanges = new OutputChanges(state);
            }

            state.resetAllOutput();
        }

        farthestChanges.mergeInto(state);

        if (state.failed())
        {
            parser.fail(this, state);
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
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.any(this, operands);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        return operands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
