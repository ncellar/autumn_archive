package com.norswap.autumn.parsing.extensions.cluster;

import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.extensions.cluster.expressions.Filter;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.util.Array;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.filter;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.reference;

/**
 * Describes the syntactic extension for cluster expression filters (see {@link Filter}).
 * <p>
 * Examples:
 * <pre>{@code
 * myRule1 = `filter { myClusterRule; allowed: myArrow1, myArrow2; }
 * myRule2 = `filter { myClusterRule; forbidden: myArrow1, myArrow2; }
 * }</pre>
 */
public final class SyntaxExtensionFilter extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SyntaxExtensionFilter()
    {
        super(Type.EXPRESSION, "filter", ClusterSyntax.filter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object compile(
        GrammarCompiler compiler,
        ParseTree filter)
    {
        String ref = filter.value("ref");

        Array<ParseTree> allowed   = filter.group("allowed");
        Array<ParseTree> forbidden = filter.group("forbidden");

        return filter(
            reference(ref),
            allowed   .mapToArray(t -> t.value, String[]::new),
            forbidden .mapToArray(t -> t.value, String[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
