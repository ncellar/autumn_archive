package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

public final class Memo extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseChanges changes = state.memo.get(this, state);

        if (changes != null)
        {
            state.merge(changes);
            return;
        }

        operand.parse(parser, state);
        state.memo.memoize(operand, state, state.extract());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
