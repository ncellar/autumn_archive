package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.annotations.NonNull;

import java.util.Arrays;

public final class Filter extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public @NonNull ParsingExpression[] allowed;
    public @NonNull ParsingExpression[] forbidden;

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
        ParsingExpression clusterAlternate = state.clusterAlternate;

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
            state.discard();
            state.fail(this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        Array<Object> allowedIDs = new Array<>(Arrays.stream(allowed)
            .map(x -> x.name != null ? x.name : x.hashCode())
            .toArray());

        Array<Object> forbiddenIDs = new Array<>(Arrays.stream(forbidden)
            .map(x -> x.name != null ? x.name : x.hashCode())
            .toArray());

        return "allowed: " + allowedIDs + ", forbidden: " + forbiddenIDs;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return JArrays.concat(new ParsingExpression[]{operand}, allowed, forbidden);
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
