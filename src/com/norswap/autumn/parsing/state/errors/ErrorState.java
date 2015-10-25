package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.ParseState;

import java.util.Collection;

/**
 * See {@link ParseState}, section "Error Handling".
 * <p>
 * The default strategy is implemented by {@link DefaultErrorState}.
 */
public interface ErrorState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void requestErrorRecordPoint();

    void dismissErrorRecordPoint();

    ErrorChanges changes();

    void merge(ErrorChanges changes);

    void handleError(ParsingExpression pe, ParseState state);

    ErrorReport report(Source source);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
