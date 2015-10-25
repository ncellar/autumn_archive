package com.norswap.autumn.parsing.expressions.abstrakt;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

import java.util.function.Predicate;

/**
 * Base implementation for parsing expression with a single operand.
 */
public abstract class UnaryParsingExpression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] children()
    {
        return operand != null
            ? new ParsingExpression[]{operand}
            : new ParsingExpression[0];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        this.operand = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return operand != null
            ? Nullability.single(this, operand)
            : Nullability.no(this);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return operand != null
            ? new ParsingExpression[]{operand}
            : new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
