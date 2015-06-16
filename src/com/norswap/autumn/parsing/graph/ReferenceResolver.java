package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.IncrementalReferenceResolver;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.MultiMap;

import java.util.HashMap;

/**
 * Resolves all resolvable references within a number of (possibly mutually referencing) parsing
 * expressions. Resolvable references are those for which a target with the specified name exist
 * within the expression graph.
 *
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 *
 * This is the preferred way to resolve references in a expression that was constructed
 * automatically. If you use factory methods, the method {@link ParsingExpressionFactory#recursive$}
 * which uses a {@link IncrementalReferenceResolver} is an alternative.
 *
 * Implementation-wise, this is an expression graph transformer. The walk records named
 * expressions as they are encountered. The transformation replaces a reference with its target, if
 * it has already been encountered. Otherwise the reference stays, but the its
 * location (a {@link Slot}) is recorded as needing to be assigned whenever the target is
 * encountered.
 *
 * A reference's target might be another reference. We handle these cases by recursively
 * resolving until we encounter a non-reference target, or a missing target.
 */
public final class ReferenceResolver extends ExpressionGraphTransformer
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int REFERENCE_CHAIN_LIMIT = 10000;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maps names (e.g. rule names) to the expression they designate.
     */
    private HashMap<String, ParsingExpression> named;

    /**
     * Map target names that can't be resolved to a slot.
     */
    private MultiMap<String, Slot> unresolved;

    /**
     * If {@link #transform} encounters a missing target, it will record its name here for the
     * benefit of calling functions, who can associate a location to it.
     */
    private String unresolvedTarget;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ReferenceResolver()
    {
        super(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression resolve(ParsingExpression expr)
    {
        return new ReferenceResolver().run(expr);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Resolves all references within the expressions reachable through {@code exrprs}. If {@code
     * exprs} contains references, it is modified in place. It is also returned.
     *
     * Throws an exception if there are unresolvable expressions.
     */
    public static ParsingExpression[] resolve(ParsingExpression[] exprs)
    {
        return new ReferenceResolver().run(exprs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression run(ParsingExpression expr)
    {
        named = new HashMap<>();
        unresolved = new MultiMap<>();

        walk(expr);

        if (!unresolved.isEmpty())
        {
            throw new RuntimeException(
                "There were unresolved references in the grammar: " + unresolved.keySet());
        }

        named = null;
        unresolved = null;
        return expr;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Resolves all references within the expressions reachable through {@code exrprs}. If {@code
     * exprs} contains references, it is modified in place. It is also returned.
     *
     * Throws an exception if there are unresolvable expressions.
     */
    public ParsingExpression[] run(ParsingExpression[] exprs)
    {
        named = new HashMap<>();
        unresolved = new MultiMap<>();

        walk(exprs);

        // Replace the references inside the array by their targets.

        for (int i = 0; i < exprs.length; ++i)
        {
            exprs[i] = transform(exprs[i]);

            if (unresolvedTarget != null)
            {
                // Trigger the exception below.
                unresolved.add(unresolvedTarget, null);
                unresolvedTarget = null;
            }
        }

        if (!unresolved.isEmpty())
        {
            throw new RuntimeException(
                "There were unresolved references in the grammar: " + unresolved.keySet());
        }

        named = null;
        unresolved = null;
        return exprs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression transform(ParsingExpression pe)
    {
        String targetName;

        int i = 0;
        while (pe instanceof Reference)
        {
            targetName = ((Reference) pe).target;
            ParsingExpression target = named.get(targetName);

            if (i > REFERENCE_CHAIN_LIMIT)
            {
                throw new RuntimeException(
                    "It is likely that you have a rule which is a reference to itself. "
                    + "If it is not the case and you use more than " + REFERENCE_CHAIN_LIMIT
                    + " chained references, increase the value of "
                    + "ReferenceResolver.REFERENCE_CHAIN_LIMIT.");
            }

            if (target == null)
            {
                unresolvedTarget = targetName;
                break;
            }

            pe = target;
        }

        return pe;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void before(ParsingExpression pe)
    {
        String name = pe.name();

        if (name == null) {
            return;
        }

        named.put(name, pe);
        updateSlotsResolution(name);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterChild(ParsingExpression pe, ParsingExpression child, int index, State state)
    {
        super.afterChild(pe, child, index, state);
        updateSlotResolution(new Slot(pe, index));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateSlotsResolution(String name)
    {
        for (Slot slot: unresolved.remove(name))
        {
            slot.set(transform(slot.get()));
            updateSlotResolution(slot);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void updateSlotResolution(Slot slot)
    {
        if (unresolvedTarget != null)
        {
            unresolved.add(unresolvedTarget, slot);
            unresolvedTarget = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
