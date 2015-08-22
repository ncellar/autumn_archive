package com.norswap.autumn.parsing.expressions.instrument;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.InstrumentedExpression;
import com.norswap.util.Strings;

import static com.norswap.autumn.parsing.Registry.PH_DEPTH;

public final class Trace extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        Integer depth = parser.ext.get(PH_DEPTH);

        if (depth == null)
        {
            parser.ext.set(PH_DEPTH, depth = 0);
        }

        System.err.println(Strings.times(depth, "-|") + operand);

        parser.ext.set(PH_DEPTH, depth + 1);
        operand.parse(parser, state);
        parser.ext.set(PH_DEPTH, depth);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
