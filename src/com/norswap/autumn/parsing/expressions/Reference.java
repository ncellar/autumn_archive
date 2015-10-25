package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

import java.util.function.Predicate;

/**
 * A reference to another expression. A reference is a temporary operator that is meant to be pruned
 * from the expression graph via a resolution process.
 * <p>
 * A reference holds the name of its target, which is the an expression with that name ({@link
 * ParsingExpression#name}).
 */
public final class Reference extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String target;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        throw new UnsupportedOperationException(
            "Trying to parse an unresolved reference to: " + target);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return target;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        throw new UnsupportedOperationException(
            "Trying to get the nullability of: " + target);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        throw new UnsupportedOperationException(
            "Trying to get the FIRST set of: " + target);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
}
