package com.norswap.autumn.parsing.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Token;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

import java.util.Collection;
import java.util.HashSet;

import static com.norswap.util.Caster.cast;

/**
 * See {@link ParseState}, section "Error Handling".
 */
public final class DefaultErrorState implements ErrorState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // The two first fields are conceptually the top of the stack implemented by the two other
    // arrays.

    private int farthestErrorPosition = -1;
    private HashSet<ParsingExpression> farthestExpressions = new HashSet<>();

    private Array<Integer> positions = new Array<>();

    @SuppressWarnings("unchecked")
    private Array<HashSet<ParsingExpression>> expressions = new Array<>(new HashSet<>());

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void requestErrorRecordPoint()
    {
        positions.push(farthestErrorPosition);
        expressions.push(farthestExpressions);
        farthestErrorPosition = -1;
        farthestExpressions = new HashSet<>();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void dismissErrorRecordPoint()
    {
        int prevPointPos = positions.pop();
        HashSet<ParsingExpression> prevPointExprs = expressions.pop();

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
        return new DefaultErrorChanges(farthestErrorPosition, cast(farthestExpressions.clone()));
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
            farthestExpressions = cast(c.expressions.clone());
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(Collection<ErrorLocation> errors)
    {
        for (ErrorLocation c: errors)
        {
            if (c.position == farthestErrorPosition)
            {
                farthestExpressions.add(c.pe);
            }
            else if (c.position > farthestErrorPosition)
            {
                farthestErrorPosition = c.position;
                farthestExpressions = new HashSet<>();
                farthestExpressions.add(c.pe);
            }
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
