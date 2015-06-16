package com.norswap.autumn.parsing.graph.nullability;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.Arrays;

/**
 * A nullable with a reduction strategy indicating that an expression is nullable if all its
 * children are nullable.
 */
public final class AllNullable extends Nullability
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public AllNullable(ParsingExpression pe, ParsingExpression[] toReduce)
    {
        super(pe, false, false, toReduce);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability reduce(Nullability[] nullabilities)
    {
        boolean allYes = true;

        for (Nullability n : nullabilities)
        {
            if (n.resolved)
            {
                if (!n.nullable)
                {
                    return no(pe);
                }
            }
            else if (allYes)
            {
                allYes = false;
            }
        }

        if (allYes)
        {
            return yes(pe);
        }

        return new AllNullable(pe, Arrays.stream(nullabilities)
            .filter(n -> !n.resolved)
            .map(n -> n.pe)
            .toArray(ParsingExpression[]::new));
    }

    // -----------------------------------------------------------------------------------------

    @Override
    public Nullability update(Nullability n)
    {
        return n.no()
            ? no(pe)
            : null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
