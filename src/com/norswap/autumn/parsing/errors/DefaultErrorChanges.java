package com.norswap.autumn.parsing.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.Source;

import java.util.HashSet;

/**
 * Changes to the error state returned by {@link DefaultErrorState#changes}.
 */
public final class DefaultErrorChanges implements ErrorChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final HashSet<ParsingExpression> expressions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DefaultErrorChanges(int position, HashSet<ParsingExpression> expressions)
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
