package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.TextPosition;
import com.norswap.util.Array;

/**
 * Default implementation of {@link ErrorReport}, used by {@link DefaultErrorState}.
 * <p>
 * Reports the tokens which we expected at the farthest error position, but failed to match.
 */
public final class DefaultErrorReport implements ErrorReport
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final String message;

    private final Array<ErrorLocation> errorLocations;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DefaultErrorReport(TextPosition farthestErrorPosition, Array<ParsingExpression> farthestExpressions)
    {
        this.errorLocations = new Array<>();
        StringBuilder b = new StringBuilder();

        b.append("The parser failed to match any of the following expressions at position ");
        b.append(farthestErrorPosition);
        b.append(":\n");

        for (ParsingExpression farthestExpression: farthestExpressions)
        {
            b.append(farthestExpression);
            b.append("\n");
            errorLocations.add(new ErrorLocation(farthestErrorPosition.position, farthestExpression));
        }

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
