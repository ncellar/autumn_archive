package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.List;

/**
 * This detects left-recursive cycles in a parsing expression graphs. For each cycle, it selects a
 * node that must be marked as left recursive (by wrapping it inside a {@link LeftRecursive} node)
 * to break the cycle. The selected node will be mapped to a new {@link LeftRecursive} inside {@link
 * #leftRecursives}.
 * <p>
 * The node selected to break a cycle is the first node pertaining to the cycle encountered in a
 * top-down left-to-right walk of the graph.
 * <p>
 * The visitor is aware of pre-existent {@link LeftRecursive} nodes and does not detect already
 * cycles anew.
 * <p>
 * To handle these nodes, we map each expression to the recursion depth at which it occurs. We also
 * record the recursion depth of each encountered LeftRecursive node. When detecting recursion; if
 * the recursive node occurs at a lower stack depth than the last encountered LeftRecursive node, it
 * means that the cycle goes through the LeftRecursive node and is thus already broken; so we do not
 * record it.
 */
public class LeftRecursionDetector extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LeftRecursionDetector(Grammar grammar)
    {
        super(Walks.inPlaceFirsts(grammar));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int stackDepth = 0;

    private HashMap<ParsingExpression, Integer> stackPositions = new HashMap<>();

    private Array<Integer> leftRecursiveStackPositions = new Array<>();

    public HashMap<ParsingExpression, ParsingExpression> leftRecursives = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        if (pe instanceof LeftRecursive)
        {
            leftRecursiveStackPositions.push(stackDepth);
        }

        stackPositions.put(pe, stackDepth);
        ++stackDepth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(ParsingExpression pe, List<Slot<ParsingExpression>> children, NodeState state)
    {
        if (pe instanceof LeftRecursive)
        {
            leftRecursiveStackPositions.pop();
        }

        stackPositions.remove(pe);
        --stackDepth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(ParsingExpression pe, Slot<ParsingExpression> slot, NodeState state)
    {
        if (state == NodeState.CUTOFF)
        {
            ParsingExpression child;
            Integer leftPos = leftRecursiveStackPositions.peekOr(-1);

            if (stackPositions.get(child = slot.get()) > leftPos)
            {
                LeftRecursive lr = ParsingExpressionFactory.leftRecursive(child);
                leftRecursives.put(child, ParsingExpressionFactory.leftRecursive(child));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        stackPositions = null;
        leftRecursiveStackPositions = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}