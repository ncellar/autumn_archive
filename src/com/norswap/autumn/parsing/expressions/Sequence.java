package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.NaryParsingExpression;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.Array;

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
        ParseState down = new ParseState(state);

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, down);

            if (down.succeeded())
            {
                down.advance();
            }
            else
            {
                state.resetOutput();
                parser.fail(this, state);
                return;
            }
        }

        state.merge(down);
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
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.all(this, operands);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        ParsingExpression pe;
        int i = 0;
        Array<ParsingExpression> array = new Array<>();

        do {
            pe = operands[i++];
            array.add(pe);
        }
        while (i < operands.length && grammar.isNullable(pe));

        return array.toArray(ParsingExpression[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}