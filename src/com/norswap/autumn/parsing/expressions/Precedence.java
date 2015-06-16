package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class Precedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NONE = 0;
    public static final int ESCAPE_PRECEDENCE = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int precedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (precedence > 0 && precedence < state.precedence)
        {
            // We bypass error handling: it is not expected that the input matches this expression.

            state.fail();
        }
        else
        {
            int oldFlags = state.flags;
            int oldPrecedence = state.precedence;
            state.precedence = precedence;

            if (precedence > 0)
            {
                // If a precedence level is set, calling a sub-expression at the same position with
                // another precedence might yield a different result, so don't memoize.

                state.forbidMemoization();
            }

            operand.parse(parser, state);

            state.precedence = oldPrecedence;
            state.flags = oldFlags;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        if (precedence == NONE)
        {
            builder.append("noPrecedence(");
        }
        else
        {
            builder.append("precedence(");
            builder.append(precedence);
            builder.append(", ");
        }

        operand.toString(builder);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}