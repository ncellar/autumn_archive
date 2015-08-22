package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.MultiMap;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
 * all its dependants. Note a child can be become resolved by visiting it, or because it was
 * itself re-reduced because one of its own child became resolved.
 *
 * After running the calculator, the remaining unresolved nullabilities come from infinite
 * recursion (e.g., "X = X"). Since our handling of left-recursion will ensure that these rules
 * always fail, we don't need to consider them nullable.
 */
public class NullabilityCalculator extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, Nullability> nullabilities = new HashMap<>();

    private MultiMap<ParsingExpression, ParsingExpression> dependants = new MultiMap<>();

    private Grammar grammar;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NullabilityCalculator(Grammar grammar)
    {
        super(Walks.readOnly);
        this.grammar = grammar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicate whether the supplied parsing expression is nullable.
     * Call only after the calculator has run.
     */
    public boolean isNullable(ParsingExpression pe)
    {
        return nullabilities.get(pe).yes();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        nullabilities.put(pe, pe.nullability(grammar));
    }

    // -----------------------------------------------------------------------------------------

    @Override
    public void after(ParsingExpression pe, List<Slot<ParsingExpression>> children, NodeState state)
    {
        Nullability n = nullabilities.get(pe);

        if (n.resolved) {
            return;
        }

        reduce(n);
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
        n = n.reduce(nullabilities(n.toReduce));

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

    // ---------------------------------------------------------------------------------------------

    private Nullability[] nullabilities(ParsingExpression[] exprs)
    {
        return Arrays.stream(exprs)
            .map(nullabilities::get)
            .toArray(Nullability[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
