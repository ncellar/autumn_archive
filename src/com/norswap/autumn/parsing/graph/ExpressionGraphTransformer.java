package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An expression graph walker that transforms an expression as it walks it.
 *
 * After each child has been walked, it is replaced by the result of calling {@link #transform}
 * with it as the parameter.
 *
 * If {@link #unique} is set, {@link #transform} will only be called once on every expression,
 * and the result will be cached, to be reused whenever the expression is encountered again. Note
 * that if you intend to modify the named alternates of an expression cluster, you *must* use
 * this option, otherwise you won't work in filters anymore.
 *
 * If you want to pass the transformation function as a lambda, see {@link FunctionalTransformer}.
 */
public abstract class ExpressionGraphTransformer extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final boolean unique;

    private Map<ParsingExpression, ParsingExpression> transformations;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExpressionGraphTransformer(boolean unique)
    {
        this.unique = unique;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract ParsingExpression transform(ParsingExpression pe);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Apply the transformation to all expressions reachable through {@code pe}, and returns the
     * transformation of {@code pe}.
     */
    protected ParsingExpression apply(ParsingExpression pe)
    {
        if (unique) {
            transformations = new HashMap<>();
        }

        walk(pe);
        ParsingExpression out = transform(pe);
        transformations = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Apply the transformation to all expressions reachable through expressions in {@code exprs}.
     * Replaces elements of {@code exprs} by their transformation, and return {@code exprs}.
     */
    protected ParsingExpression[] apply(ParsingExpression[] exprs)
    {
        if (unique) {
            transformations = new HashMap<>();
        }

        walk(exprs);

        for (int i = 0; i < exprs.length; ++i)
        {
            exprs[i] = transform(exprs[i]);
        }

        transformations = null;
        return exprs;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Apply the transformation to all expressions reachable through expressions in {@code exprs}.
     * Returns a collection of the transformation of the elements of {@code exprs}, preserving
     * the iteration order.
     *
     */
    protected Collection<ParsingExpression> apply(Iterable<ParsingExpression> exprs)
    {
        if (unique) {
            transformations = new HashMap<>();
        }

        ArrayList<ParsingExpression> array = new ArrayList<>();

        walk(exprs);

        for (ParsingExpression expr: exprs)
        {
            array.add(transform(expr));
        }

        transformations = null;
        return array;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void afterChild(ParsingExpression pe, ParsingExpression child, int index, State state)
    {
        pe.setChild(index,
            unique
                ? transformations.computeIfAbsent(child, this::transform)
                : transform(child));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
