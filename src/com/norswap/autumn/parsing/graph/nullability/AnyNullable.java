package com.norswap.autumn.parsing.graph.nullability;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.Arrays;

/**
 * A nullable with a reduction strategy indicating that an expression is nullable of any of its
 * children is nullable.
 */
public final class AnyNullable extends Nullability
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public AnyNullable(ParsingExpression pe, ParsingExpression[] toReduce)
    {
        super(pe, false, false, toReduce);
    }

    // -----------------------------------------------------------------------------------------

    @Override
    public Nullability reduce(Nullability[] nullabilities)
    {
        boolean allNo = true;

        for (Nullability n : nullabilities)
        {
            if (n.resolved)
            {
                if (n.nullable)
                {
                    return yes(pe);
                }
            }
            else if (allNo)
            {
                allNo = false;
            }
        }

        if (allNo)
        {
            return no(pe);
        }

        return new AnyNullable(pe, Arrays.stream(nullabilities)
            .filter(n -> !n.resolved)
            .map(n -> n.pe)
            .toArray(ParsingExpression[]::new));
    }

    // -----------------------------------------------------------------------------------------

    @Override
    public Nullability update(Nullability n)
    {
        return n.yes()
            ? yes(pe)
            : null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
