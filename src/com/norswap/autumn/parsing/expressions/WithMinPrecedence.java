package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class WithMinPrecedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        int oldMinPrecedence = parser.minPrecedence();
        parser.setMinPrecedence(this.minPrecedence);
        operand.parse(parser, state);
        parser.setMinPrecedence(oldMinPrecedence);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownPrintableData()
    {
        return "minPrecedence: " + minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
