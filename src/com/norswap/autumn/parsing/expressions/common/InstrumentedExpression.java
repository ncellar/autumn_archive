package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.Parser;

public abstract class InstrumentedExpression extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        operand.toString(builder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
