package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;


/**
 * Triggers a cut operation: signify that the remaining alternatives of the choice indicated by
 * the name (a node of type {@link Cuttable}) must not be tried.
 *
 * Always succeeds.
 *
 * On success, its end position is its start position.
 */
public final class Cut extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        state.cuts.add(name);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        builder.append("cut(\"");
        builder.append(name);
        builder.append("\")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownPrintableData()
    {
        return "name: " + name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
