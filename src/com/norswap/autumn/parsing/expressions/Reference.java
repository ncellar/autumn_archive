package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.Array;


/**
 * A reference to another expression. A reference is a temporary operator that is meant to be
 * pruned from the expression graph via a resolution process.
 *
 * A reference holds the name of its target, which is the an expression with that name ({@link
 * ParsingExpression#name()}).
 */
public final class Reference extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String target;

    /** See {@link com.norswap.autumn.parsing.IncrementalReferenceResolver}. */
    public Array<ParsingExpression> nestedReferences;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        throw new UnsupportedOperationException(
            "Trying to parse an unresolved reference to: " + target);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        builder.append(target);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownPrintableData()
    {
        return target;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        throw new UnsupportedOperationException(
            "Trying to get the nullability of: " + target);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        throw new UnsupportedOperationException(
            "Trying to get the FIRST set of: " + target);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
}
