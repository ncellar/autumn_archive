package com.norswap.autumn.parsing.expressions.instrument;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.InstrumentedExpression;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.Registry.PSH_STACK_TRACE;

public class StackTrace extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        Array<ParsingExpression> stackTrace = state.ext.get(PSH_STACK_TRACE);

        if (stackTrace == null)
        {
            stackTrace = new Array<>();
            state.ext.set(PSH_STACK_TRACE, stackTrace);
        }

        stackTrace.push(this);

        try {
            operand.parse(parser, state);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage() + " at: ");
            for (ParsingExpression pe: stackTrace.reverseIterable())
            {
                System.err.println(pe);
            }

            throw new Error(e);
        }
        stackTrace.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
