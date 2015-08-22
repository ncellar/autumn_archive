package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.NaryParsingExpression;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

public final class Cuttable extends NaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.succeeded())
            {
                return;
            }
            else if (state.cuts.removeFromEnd(name))
            {
                break;
            }
            else
            {
                state.resetOutput();
            }
        }

        parser.fail(this, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        for (ParsingExpression operand : operands)
        {
            int result = operand.parseDumb(parser, position);

            if (result != - 1)
            {
                return result;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        builder.append("cuttable(");
        builder.append("\"");
        builder.append(name);
        builder.append("\", ");

        for (ParsingExpression operand: operands)
        {
            operand.appendTo(builder);
            builder.append(", ");
        }

        builder.setLength(builder.length() - 2);

        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownPrintableData()
    {
        return toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.any(this, operands);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        return operands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
