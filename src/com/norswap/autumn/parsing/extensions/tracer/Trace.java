package com.norswap.autumn.parsing.extensions.tracer;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.InstrumentedExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Strings;

public final class Trace extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Trace(ParsingExpression pe)
    {
        this.operand = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        TraceState tstate = (TraceState) state.customStates[TracerExtension.INDEX];

        System.err.println(
            Strings.times(tstate.level, "-|")
            + operand.toStringOneLine()
            + " at " + parser.source.position(state.start));

        ++ tstate.level;
        operand.parse(parser, state);
        -- tstate.level;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
