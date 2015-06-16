package com.norswap.autumn.parsing.graph.nullability;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.ExpressionGraphWalker;
import com.norswap.autumn.util.MultiMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Determines which rules in a parsing expression graph are nullable.
 *
 * Nullable parsing expressions are those that can succeed while matching no input.
 *
 * To help determine this, each type of parsing expression implements the {@link
 * ParsingExpression#nullability} method which returns a {@link Nullability} object.
 *
 * To understand what follows, read the Javadoc of {@link Nullability}.
 *
 * The calculator reduces the nullability of each expression after visiting its children. Because
 * of recursion, this does not suffice to immediately resolve all the nullabilities.
 *
 * To resolve this issue, whenever a nullability cannot be resolved right away, we register the
 * expression as a "dependant" of its children. Whenever a child becomes resolved, we re-reduce
 * all its dependants. Note a child can be become resolved through our walk, or because it was
 * itself re-reduced because one of its own child became resolved.
 *
 * After running the calculator, the remaining unresolved nullabilities come from infinite
 * recursion (e.g., "X = X"). Since our handling of left-recursion will ensure that these rules
 * always fail, we don't need to consider them nullable.
 */
public class NullabilityCalculator extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, Nullability> nullabilities = new HashMap<>();

    private MultiMap<ParsingExpression, ParsingExpression> dependants = new MultiMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a stream of nullable parsing expression. Call only after the calculator has run.
     */
    public Stream<ParsingExpression> nullables()
    {
        return nullabilities
            .entrySet()
            .stream()
            .filter(e -> e.getValue().yes())
            .map(e -> e.getKey());
    }

    // -----------------------------------------------------------------------------------------

    /**
     * Indicate whether the supplied parsing expression is nullable.
     * Call only after the calculator has run.
     */
    public boolean isNullable(ParsingExpression pe)
    {
        return nullabilities.get(pe).yes();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes and returns a stream of nullable expressions ({@link #nullables})reachable through
     * the given rules.
     */
    public static Stream<ParsingExpression> compute(ParsingExpression[] rules)
    {
        return new NullabilityCalculator().run(rules);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes and returns a stream of nullable expressions ({@link #nullables})reachable through
     * the given rules.
     */
    public Stream<ParsingExpression> run(ParsingExpression[] rules)
    {
        walk(rules);
        dependants = null;
        return nullables();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void before(ParsingExpression pe)
    {
        nullabilities.put(pe, pe.nullability());
    }

    // -----------------------------------------------------------------------------------------

    @Override
    protected void afterAll(ParsingExpression pe)
    {
        Nullability n = nullabilities.get(pe);

        if (n.resolved) {
            return;
        }

        n = reduce(n);
        nullabilities.put(pe, n);

        if (n.resolved) {
            propagateResolution(n);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Nullability reduce(Nullability n)
    {
        n = n.reduce(nullabilities(n.toReduce));

        if (!n.resolved)
        {
            for (ParsingExpression pe: n.toReduce)
            {
                dependants.add(pe, n.pe);
            }
        }

        return n;
    }

    // ---------------------------------------------------------------------------------------------

    private void propagateResolution(Nullability n)
    {
        for (ParsingExpression expr: dependants.get(n.pe))
        {
            n = nullabilities.get(expr);

            if (n.resolved) {
                continue;
            }

            Nullability tmp;

            n = (tmp = n.update(n)) != null
                ? tmp
                : reduce(n);

            nullabilities.put(expr, n);

            if (n.resolved) {
                propagateResolution(n);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private Nullability[] nullabilities(ParsingExpression[] exprs)
    {
        return Arrays.stream(exprs)
            .map(nullabilities::get)
            .toArray(Nullability[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
