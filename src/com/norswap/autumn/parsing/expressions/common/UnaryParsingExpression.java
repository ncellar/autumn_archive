package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.graph.Nullability;

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
    public void appendContentTo(StringBuilder builder)
    {
        String name = this.getClass().getSimpleName();
        name = name.substring(0,1).toLowerCase() + name.substring(1);

        builder.append(name);
        builder.append("(");
        operand.appendTo(builder);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public UnaryParsingExpression clone()
    {
        UnaryParsingExpression clone = (UnaryParsingExpression) super.clone();
        clone.operand = operand.clone();
        return clone;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public UnaryParsingExpression deepCopy()
    {
        UnaryParsingExpression copy = (UnaryParsingExpression) super.deepCopy();
        copy.operand = operand.deepCopy();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        if (operand == null)
        {
            System.err.println("NULL OPERAND: " + this);
        }

        return Nullability.single(this, operand);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        return new ParsingExpression[]{operand};
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
