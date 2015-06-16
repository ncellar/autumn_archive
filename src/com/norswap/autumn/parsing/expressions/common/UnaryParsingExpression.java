package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.graph.nullability.Nullability;

import java.util.stream.Stream;

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
        return new ParsingExpression[]{operand};
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        this.operand = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     *
     * Here, implemented by outputting the class name with an initial lower-case, then the
     * operand between parens.
     */
    @Override
    public void appendTo(StringBuilder builder)
    {
        String name = this.getClass().getSimpleName();
        name = name.substring(0,1).toLowerCase() + name.substring(1);

        builder.append(name);
        builder.append("(");
        operand.toString(builder);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public UnaryParsingExpression deepCopy()
    {
        UnaryParsingExpression copy = (UnaryParsingExpression) super.deepCopy();
        copy.operand = operand.deepCopy();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        if (operand == null)
        {
            System.err.println("NULL OPERAND: " + this);
        }

        return Nullability.single(this, operand);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts()
    {
        return new ParsingExpression[]{operand};
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
