package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.util.Array;
import com.norswap.util.graph.NodeState;
import com.norswap.util.graph.Slot;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Resolves all resolvable references underneath the visited expression graph. Resolvable references
 * are those for which a target with the specified name exist within the expression graph.
 * <p>
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph. ALl unresolved reference targets are  put into {@link
 * #unresolved}.
 */
public final class ReferenceResolver extends ParsingExpressionVisitor
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Max allowed number of chained references. Used to detect reference loops (not be confused
     * with loops in the grammar -- reference loops involve only references and no actual
     * parsing expressions).
     */
    public static int REFERENCE_CHAIN_LIMIT = 10000;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<String, ParsingExpression> named = new HashMap<>();

    private HashSet<Slot<ParsingExpression>> references = new HashSet<>();

    public final HashSet<String> unresolved = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(Slot<ParsingExpression> node)
    {
        ParsingExpression initial = node.initial;

        if (initial.name != null)
        {
            named.put(initial.name, initial);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<ParsingExpression> root, NodeState state)
    {
        ParsingExpression initial = root.initial;

        if (initial instanceof Reference)
        {
            references.add(root);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(Slot<ParsingExpression> parent, Slot<ParsingExpression> child, NodeState state)
    {
        ParsingExpression initial = child.initial;

        if (initial instanceof Reference)
        {
            references.add(child);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        super.conclude();
        named = null;
        references = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void applyChanges(Array<Slot<ParsingExpression>> modified)
    {
        for (Slot<ParsingExpression> slot: references)
        {
            ParsingExpression target = slot.initial;
            String name;
            int i = 0;

            // References can be chained!

            do {
                name = ((Reference) target).target;
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
            else if (slot.parent == null)
            {
                slot.assigned = target;
                target.name = name;
            }
            else
            {
                slot.parent.setChild(slot.index, target);
                target.name = name;
            }
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
