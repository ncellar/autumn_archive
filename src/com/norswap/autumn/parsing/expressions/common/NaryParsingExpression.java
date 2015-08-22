package com.norswap.autumn.parsing.expressions.common;

import com.norswap.util.DeepCopy;

/**
 *  Base implementation for parsing expression with an array of operands.
 */
public abstract class NaryParsingExpression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        String name = this.getClass().getSimpleName();
        name = name.substring(0,1).toLowerCase() + name.substring(1);

        builder.append(name);
        builder.append("(");

        if (operands.length > 0)
        {
            for (ParsingExpression operand: operands)
            {
                operand.appendTo(builder);
                builder.append(", ");
            }

            builder.setLength(builder.length() - 2);
        }

        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return operands;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        operands[position] = pe;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public NaryParsingExpression clone()
    {
        NaryParsingExpression clone = (NaryParsingExpression) super.clone();
        clone.operands = operands.clone();
        return clone;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public NaryParsingExpression deepCopy()
    {
        NaryParsingExpression copy = (NaryParsingExpression) super.deepCopy();
        copy.operands = DeepCopy.of(operands, ParsingExpression[]::new);
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
