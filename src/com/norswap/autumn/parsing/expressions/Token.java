package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class Token extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            parser.fail(this, state);
            return;
        }

        int pos = parser.whitespace.parseDumb(parser, state.end);

        if (pos > 0)
        {
            state.end = pos;
        }
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

        int pos = parser.whitespace.parseDumb(parser, position);

        return pos > 0 ? pos : position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
