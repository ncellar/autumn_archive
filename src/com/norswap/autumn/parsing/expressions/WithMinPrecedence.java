package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.PrecedenceEntry;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

public final class WithMinPrecedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        PrecedenceEntry entry = state.minPrecedence.peek();
        int oldMinPrecedence = entry.minPrecedence;
        entry.minPrecedence = this.minPrecedence;
        operand.parse(parser, state);
        entry.minPrecedence = oldMinPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return "minPrecedence: " + minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
