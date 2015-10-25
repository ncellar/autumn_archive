package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseStateSnapshot;
import com.norswap.autumn.parsing.expressions.abstrakt.NaryParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.Array;

import java.util.function.Predicate;

/**
 * Invokes all its operands sequentially over the input, until one fails. Each operand is
 * invoked at the end position of the previous one.
 *
 * Succeeds iff all operands succeed.
 *
 * On success, its end position is that of its last operand.
 */
public final class Sequence extends NaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseStateSnapshot snapshot = state.snapshot();

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.succeeded())
            {
                state.commit();
            }
            else
            {
                state.restore(snapshot);
                state.fail(this);
                return;
            }
        }

        state.uncommit(snapshot);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        for (ParsingExpression operand: operands)
        {
            position = operand.parseDumb(parser, position);

            if (position == -1)
            {
                break;
            }
        }

        return position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.all(this, operands);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        ParsingExpression pe;
        int i = 0;
        Array<ParsingExpression> array = new Array<>();

        do {
            pe = operands[i++];
            array.add(pe);
        }
        while (i < operands.length && nullability.test(pe));

        return array.toArray(ParsingExpression[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}