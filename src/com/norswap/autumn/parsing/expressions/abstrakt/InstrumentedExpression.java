package com.norswap.autumn.parsing.expressions.abstrakt;

import com.norswap.autumn.parsing.Parser;

import static com.norswap.autumn.parsing.ParsingExpressionFlags.PEF_UNARY_INVISIBLE;

public abstract class InstrumentedExpression extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    {
        flags |= PEF_UNARY_INVISIBLE;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
