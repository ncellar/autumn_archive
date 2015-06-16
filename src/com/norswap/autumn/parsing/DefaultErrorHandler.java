package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Token;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.Registry.PEF_ERROR_RECORDING;
import static com.norswap.autumn.parsing.Registry.PSH_STACK_TRACE;

/**
 * The default error handling strategy consist of reporting the error(s) occuring at the farthest
 * error positions, under the assumption that the parse that makes the most progress is the
 * "most correct".
 *
 * This strategy only considers errors that result from a failure to match a parsing expression
 * marked as "error-recording" or as a token.
 */
public final class DefaultErrorHandler implements ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int farthestErrorPosition = -1;

    private Array<ParsingExpression> farthestExpressions = new Array<>(1);

    private Array<Array<ParsingExpression>> stackTraces = new Array<>(1);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handle(ParsingExpression pe, ParseState state)
    {
        if (!pe.hasFlagsSet(PEF_ERROR_RECORDING) && !(pe instanceof Token))
        {
            return;
        }

        if (state.start > farthestErrorPosition)
        {
            farthestErrorPosition = state.start;
            farthestExpressions = new Array<>();
            stackTraces = new Array<>();
        }

        if (state.start == farthestErrorPosition)
        {
            farthestExpressions.add(pe);

            Array<ParsingExpression> stackTrace = state.ext.get(PSH_STACK_TRACE);
            stackTraces.add(stackTrace != null ? stackTrace.clone() : null);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void reportErrors(Parser parser)
    {
        System.err.println(
            "The parser failed to match any of the following expressions at position "
            + parser.source().position(farthestErrorPosition) + ":");

        for (int i = 0; i < farthestExpressions.size(); ++i)
        {
            System.err.println(farthestExpressions.get(i));

            Array<ParsingExpression> stackTrace = stackTraces.get(i);

            if (stackTrace != null)
            {
                System.err.println("stack trace: ");

                for (ParsingExpression pe: stackTrace.reverseIterable())
                {
                    System.err.println(pe);
                }

                System.err.println("");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
