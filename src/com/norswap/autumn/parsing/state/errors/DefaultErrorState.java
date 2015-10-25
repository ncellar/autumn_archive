package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Token;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 * See {@link ParseState}, section "Error Handling".
 */
public final class DefaultErrorState implements ErrorState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // The two first fields are conceptually the top of the stack implemented by the two other
    // arrays.

    private int farthestErrorPosition = -1;
    private Array<ParsingExpression> farthestExpressions = new Array<>();
    private Array<Integer> positions = new Array<>();
    @SuppressWarnings("unchecked")
    private Array<Array<ParsingExpression>> expressions = new Array<>(new Array<>());

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void requestErrorRecordPoint()
    {
        positions.push(farthestErrorPosition);
        expressions.push(farthestExpressions);
        farthestErrorPosition = -1;
        farthestExpressions = new Array<>();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void dismissErrorRecordPoint()
    {
        int prevPointPos = positions.pop();
        Array<ParsingExpression> prevPointExprs = expressions.pop();

        if (farthestErrorPosition == prevPointPos)
        {
            farthestExpressions.addAll(prevPointExprs);
        }
        else if (farthestErrorPosition < prevPointPos)
        {
            farthestErrorPosition = prevPointPos;
            farthestExpressions = prevPointExprs;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public DefaultErrorChanges changes()
    {
        return new DefaultErrorChanges(farthestErrorPosition, farthestExpressions.clone());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(ErrorChanges changes)
    {
        DefaultErrorChanges c = (DefaultErrorChanges) changes;

        if (c.position == farthestErrorPosition)
        {
            farthestExpressions.addAll(c.expressions);
        }
        else if (c.position > farthestErrorPosition)
        {
            farthestErrorPosition = c.position;
            farthestExpressions = c.expressions.clone();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void handleError(ParsingExpression pe, ParseState state)
    {
        // only record errors for tokens
        if (!(pe instanceof Token))
        {
            return;
        }

        if (state.start == farthestErrorPosition)
        {
            farthestExpressions.add(pe);
        }
        if (state.start > farthestErrorPosition)
        {
            farthestErrorPosition = state.start;
            farthestExpressions.clear();
            farthestExpressions.add(pe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ErrorReport report(Source source)
    {
        return new DefaultErrorReport(source.position(farthestErrorPosition), farthestExpressions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
