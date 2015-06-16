package com.norswap.autumn.parsing.graph.nullability;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * A nullable with a reduction strategy indicating that an expression is nullable if its only
 * child is nullable.
 */
public final class SingleNullable extends Nullability
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    SingleNullable(ParsingExpression pe, ParsingExpression op)
    {
        super(pe, false, false, new ParsingExpression[]{op});
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability reduce(Nullability[] nullabilities)
    {
        Nullability n = nullabilities[0];

        if (n.resolved)
        {
            return n.nullable
                ? yes(pe)
                : no(pe);
        }

        return this;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Nullability update(Nullability n)
    {
        if (n.resolved)
        {
            return n.nullable
                ? yes(pe)
                : no(pe);
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
