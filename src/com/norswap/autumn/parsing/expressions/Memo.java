package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class Memo extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (state.isMemoizationForbidden())
        {
            operand.parse(parser, state);
            return;
        }

        OutputChanges changes = parser.memoizationStrategy.get(this, state);

        if (changes != null)
        {
            changes.mergeInto(state);
            return;
        }

        operand.parse(parser, state);
        parser.memoizationStrategy.memoize(operand, state, new OutputChanges(state));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
