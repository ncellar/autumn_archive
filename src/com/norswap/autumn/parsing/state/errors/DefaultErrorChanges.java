package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.util.Array;

/**
 * Changes to the error state returned by {@link DefaultErrorState#changes}.
 */
public final class DefaultErrorChanges implements ErrorChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final Array<ParsingExpression> expressions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DefaultErrorChanges(int position, Array<ParsingExpression> expressions)
    {
        this.position = position;
        this.expressions = expressions;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ErrorReport report(Source source)
    {
        return new DefaultErrorReport(source.position(position), expressions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
