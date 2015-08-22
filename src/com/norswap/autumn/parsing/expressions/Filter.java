package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.util.Array;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Filter extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Those must be non-null.

    public ParsingExpression[] allowed;
    public ParsingExpression[] forbidden;

    // NOTE: The operand should be an ExpressionCluster (or some wrapper thereof).

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            return;
        }

        boolean success = allowed.length == 0;
        ParsingExpression clusterAlternate = parser.clusterAlternate;

        for (ParsingExpression pe : allowed)
        {
            if (pe == clusterAlternate)
            {
                success = true;
                break;
            }
        }

        for (ParsingExpression pe : forbidden)
        {
            if (pe == clusterAlternate)
            {
                success = false;
                break;
            }
        }

        if (!success)
        {
            state.resetOutput();
            parser.fail(this, state);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        builder.append("filter(");
        builder.append(Array.fromArray(allowed));
        builder.append(",");
        builder.append(Array.fromArray(forbidden));
        builder.append(",");
        operand.appendTo(builder);
        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownPrintableData()
    {
        return "allowed: " + allowed.length + ", forbidden: " + forbidden.length;
    }


    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return Stream.concat(
            Stream.of(operand),
            Stream.concat(Arrays.stream(allowed), Arrays.stream(forbidden)))
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        if (position == 0)
        {
            this.operand = pe;
        }

        else if (position <= allowed.length)
        {
            allowed[position - 1] = pe;
        }
        else
        {
            forbidden[position - 1 - allowed.length] = pe;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
