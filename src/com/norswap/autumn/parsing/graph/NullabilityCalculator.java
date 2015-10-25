package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.MultiMap;
import com.norswap.util.graph.Slot;

import java.util.HashMap;
import java.util.function.Predicate;

/**
 * Determines which rules in a parsing expression graph are nullable. Nullable parsing expressions
 * are those that can succeed while matching no input. The nullability of an expression can be
 * determined after the visitor has run by calling the {@link #test} method.
 * <p>
 * To help determine this, each type of parsing expression implements the {@link
 * ParsingExpression#nullability} method which returns a {@link Nullability} object.
 * <p>
 * To understand what follows, read the Javadoc of {@link Nullability}.
 * <p>
 * The calculator reduces the nullability of each expression after visiting its children. Because of
 * recursion, this does not suffice to immediately resolve all the nullabilities.
 * <p>
 * To resolve this issue, whenever a nullability cannot be resolved right away, we register the
 * expression as a "dependant" of its children. Whenever a child becomes resolved, we re-reduce all
 * its dependants. Note a child can be become resolved by visiting it, or because it was itself
 * re-reduced because one of its own child became resolved.
 * <p>
 * After running the calculator, the remaining unresolved nullabilities come from infinite recursion
 * (e.g., "X = X"). Since our handling of left-recursion will ensure that these rules always fail,
 * we don't need to consider them nullable.
 */
public final class NullabilityCalculator
    extends ParsingExpressionVisitor
    implements Predicate<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, Nullability> nullabilities = new HashMap<>();

    private MultiMap<ParsingExpression, ParsingExpression> dependants = new MultiMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicate whether the supplied parsing expression is nullable.
     * Call only after the calculator has run.
     */
    @Override
    public boolean test(ParsingExpression pe)
    {
        return nullabilities.get(pe).yes();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(Slot<ParsingExpression> pe)
    {
        nullabilities.put(pe.initial, pe.initial.nullability());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(Slot<ParsingExpression> slot, Array<Slot<ParsingExpression>> children)
    {
        Nullability n = nullabilities.get(slot.initial);

        if (n.resolved) {
            return;
        }

        reduce(n);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        super.conclude();
        dependants = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Nullability update(Nullability n)
    {
        Nullability updated = n.update(n);

        if (updated == null)
        {
            return reduce(n);
        }
        else
        {
            nullabilities.put(updated.pe, updated);
            propagateResolution(updated);
            return updated;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private Nullability reduce(Nullability n)
    {
        n = n.reduce(JArrays.map(n.toReduce, Nullability[]::new, nullabilities::get));

        nullabilities.put(n.pe, n);

        if (n.resolved)
        {
            propagateResolution(n);
        }
        else
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

            update(n);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}



