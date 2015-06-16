package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class DropPrecedence extends UnaryParsingExpression
{
    @Override
    public void parse(Parser parser, ParseState state)
    {
        int minPrecedence = parser.minPrecedence();
        parser.setMinPrecedence(0);
        operand.parse(parser, state);
        parser.setMinPrecedence(minPrecedence);
    }
}
