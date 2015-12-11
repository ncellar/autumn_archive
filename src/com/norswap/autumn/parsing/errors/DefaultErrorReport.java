package com.norswap.autumn.parsing.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.TextPosition;
import com.norswap.util.Array;

import java.util.Set;

/**
 * Default implementation of {@link ErrorReport}, used by {@link DefaultErrorState}.
 * <p>
 * Reports the tokens which we expected at the farthest error position, but failed to match.
 * <p>
 * Currently, this is often unhelpful in practice.
 */
public final class DefaultErrorReport implements ErrorReport
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final String message;

    private final Array<ErrorLocation> errorLocations;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DefaultErrorReport(
        TextPosition farthestErrorPosition,
        Set<ParsingExpression> farthestExpressions)
    {
        this.errorLocations = new Array<>();
        StringBuilder b = new StringBuilder();

        b.append("The parser failed to match any of the following expressions at position ");
        b.append(farthestErrorPosition);
        b.append(":\n");

        // NOTE(norswap): As stated, the strategy is often unhelpful.
        // So here's some comment hackery to quickly disable it (and report nothing).
        //*
        for (ParsingExpression farthestExpression: farthestExpressions)
        {
            b.append(farthestExpression);
            b.append("\n");
            errorLocations.add(new ErrorLocation(farthestErrorPosition.offset, farthestExpression));
        }
        //*/

        this.message = b.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String message()
    {
        return message;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Array<ErrorLocation> locations()
    {
        return errorLocations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
