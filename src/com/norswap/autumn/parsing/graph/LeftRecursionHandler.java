package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.util.Array;
import com.norswap.util.graph.GraphVisitor;
import com.norswap.util.graph.NodeState;
import com.norswap.util.graph.Slot;

import java.util.HashMap;
import java.util.function.Predicate;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.leftRecursive;

/**
 * This detects left-recursive cycles in a parsing expression graphs. For each cycle, it selects a
 * node that must be marked as left recursive (by wrapping it inside a {@link LeftRecursive} node)
 * to break the cycle. The selected node will be mapped to a new {@link LeftRecursive} inside {@link
 * #leftRecursives}.
 * <p>
 * Optionally (if the {@code replace} parameter of the constructor is true), this will also replace
 * all detected nodes by their LeftRecursive replacement, in-place. Note that currently if the
 * replaced node is named, the name is not transferred to the LeftRecursive replacement. But since
 * the original node is a child of the replacement, the name still appears in the graph.
 * <p>
 * The node selected to break a cycle is the first node pertaining to the cycle encountered in a
 * top-down left-to-right walk of the graph.
 * <p>
 * The visitor is aware of pre-existent {@link LeftRecursive} an {@link ExpressionCluster} nodes and
 * does not detect already cycles anew.
 * <p>
 * To handle these nodes, we map each expression to the recursion depth at which it occurs. We also
 * record the recursion depth of each encountered LeftRecursive / ExpressionCluster node. When
 * detecting recursion; if the recursive node occurs at a lower stack depth than the last
 * encountered LeftRecursive node, it means that the cycle goes through the LeftRecursive node and
 * is thus already broken; so we do not record it.
 */
public final class LeftRecursionHandler extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean replace;
    private Predicate<ParsingExpression> nullability;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LeftRecursionHandler(boolean replace, Predicate<ParsingExpression> nullability)
    {
        this.replace = replace;
        this.nullability = nullability;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int stackDepth = 0;

    private HashMap<ParsingExpression, Integer> stackPositions = new HashMap<>();

    private Array<Integer> leftRecursiveStackPositions = Array.fromItem(-1);

    public HashMap<ParsingExpression, ParsingExpression> leftRecursives = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(Slot<ParsingExpression> slot)
    {
        ParsingExpression pe = slot.initial;

        if (pe instanceof LeftRecursive || pe instanceof ExpressionCluster)
        {
            leftRecursiveStackPositions.push(stackDepth);
        }

        stackPositions.put(pe, stackDepth);
        ++stackDepth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(Slot<ParsingExpression> slot, Array<Slot<ParsingExpression>> children)
    {
        ParsingExpression pe = slot.initial;

        if (pe instanceof LeftRecursive)
        {
            leftRecursiveStackPositions.pop();
        }

        stackPositions.remove(pe);
        --stackDepth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(Slot<ParsingExpression> parent, Slot<ParsingExpression> child, NodeState state)
    {
        if (state == NodeState.CUTOFF)
        {
            ParsingExpression pe = child.initial;

            if (stackPositions.get(pe) > leftRecursiveStackPositions.peek())
            {
                child.assigned = leftRecursive(pe);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        super.conclude();
        stackPositions = null;
        leftRecursiveStackPositions = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected Iterable<ParsingExpression> children(ParsingExpression pe)
    {
        return Array.fromArray(pe.firsts(nullability));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void applyChanges(Array<Slot<ParsingExpression>> modified)
    {
        for (Slot<ParsingExpression> slot: modified)
        {
            leftRecursives.put(slot.initial, slot.assigned);

            if (replace && slot.parent != null)
            {
                slot.parent.setChild(slot.index, slot.assigned);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
