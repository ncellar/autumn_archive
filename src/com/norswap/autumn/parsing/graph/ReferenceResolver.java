package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resolves all resolvable references underneath the visited expression graph. Resolvable references
 * are those for which a target with the specified name exist within the expression graph.
 * <p>
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 * <p>
 * If there are unresolved references, an exception is thrown (but if caught, the above still
 * applies).
 * <p>
 * This is the preferred way to resolve references. If performance is critical, look into {@link
 * ParsingExpressionFactory#recursive$}.
 */
public class ReferenceResolver extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Max allowed number of chained references. Used to detect reference loops (not be confused
     * with loops in the grammar -- reference loops involve only references and no actual
     * parsing expressions).
     */
    public static int REFERENCE_CHAIN_LIMIT = 10000;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maps names (e.g. rule names) to the expression they designate.
     */
    public Map<String, ParsingExpression> named = new HashMap<>();

    // ---------------------------------------------------------------------------------------------

    /**
     * All the regular TODO
     */
    public Set<Slot<ParsingExpression>> references = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ReferenceResolver()
    {
        super(Walks.inPlace);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        String name = pe.name();

        if (name != null)
        {
            named.put(name, pe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(ParsingExpression pe, Slot<ParsingExpression> slot, NodeState state)
    {
        if (slot.get() instanceof Reference)
        {
            references.add(slot);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<ParsingExpression> slot, NodeState state)
    {
        if (slot.get() instanceof Reference)
        {
            references.add(slot);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        HashSet<String> unresolved = new HashSet<>();

        for (Slot<ParsingExpression> slot: references)
        {
            ParsingExpression target = slot.get();
            String name;
            int i = 0;

            // References can be chained!

            do {
                name = ((Reference)target).target;
                target = named.get(name);

                if (++i > REFERENCE_CHAIN_LIMIT)
                {
                    panic();
                }
            }
            while (target != null && target instanceof Reference);

            if (target == null)
            {
                unresolved.add(name);
            }
            else
            {
                slot.set(target);
            }
        }

        if (!unresolved.isEmpty())
        {
            throw new RuntimeException(
                "There were unresolved references in the grammar: " + unresolved);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void panic()
    {
        throw new RuntimeException(
            "It is likely that you have a rule which is a reference to itself. "
            + "If it is not the case and you use more than " + REFERENCE_CHAIN_LIMIT
            + " chained references, increase the value of "
            + "ReferenceResolver.REFERENCE_CHAIN_LIMIT.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
