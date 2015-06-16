package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This detects left-recursive cycles in a parsing expression graphs. For each cycle, it selects
 * a parsing expression that must be marked as left recursive to break the cycle and puts into
 * {@link #leftRecursives}.
 *
 * The detector is aware of pre-resolved left-recursion (via {@link LeftRecursive} nodes and does
 * not record nodes in {@link #leftRecursives} for that cycle, unless they belong to another cycle.
 *
 * The basic idea of the algorithm is to mark an expression as left-recursive whenever {@link
 * #afterChild} reports {@link State#VISITING}. Effectively, this means that the expression
 * selected to break a cycle is the one encountered first when walking the rules in a top-down,
 * left-to-right (w.r.t. the order of {@link ParsingExpression#firsts}) manner. Cycles reachable
 * from multiple rules will be detected in the first rule that reaches it.
 *
 * However, this doesn't account for pre-existent {@link LeftRecursive} nodes. To account for
 * them, we map each expression to the recursion depth at which it occurs. We also record the
 * recursion depth of each encountered LeftRecursive node. When detecting recursion; if the
 * recursive node occurs at a lower stack depth than the last encountered LeftRecursive node, it
 * means that the cycle goes through the LeftRecursive node and is thus already broken; so we do
 * not record it.
 *
 * This class only detects cycles and record the expressions at which theses cycles should be
 * broken. To actually break them, see {@link LeftRecursionBreaker}.
 */
public final class LeftRecursionDetector extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Set<ParsingExpression> leftRecursives;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int stackDepth = 0;
    private HashMap<ParsingExpression, Integer> stackPositions = new HashMap<>();
    private Array<Integer> leftRecursiveStackPositions = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs the detector then returns {@link #leftRecursives}.
     */
    public static Set<ParsingExpression> detect(ParsingExpression[] rules)
    {
        return new LeftRecursionDetector().run(rules);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs the detector then returns {@link #leftRecursives}.
     */
    public Set<ParsingExpression> run(ParsingExpression[] rules)
    {
        leftRecursives = new HashSet<>();
        stackDepth = 0;
        stackPositions = new HashMap<>();
        leftRecursiveStackPositions = new Array<>();

        walk(rules);

        stackPositions = null;
        leftRecursiveStackPositions = null;
        return leftRecursives;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void before(ParsingExpression pe)
    {
        if (pe instanceof LeftRecursive)
        {
            leftRecursiveStackPositions.push(stackDepth);
        }

        stackPositions.put(pe, stackDepth++);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterChild(ParsingExpression pe, ParsingExpression child, int index, State state)
    {
        Integer leftPos = leftRecursiveStackPositions.peekOrNull();

        if (state == State.VISITING
            && stackPositions.get(child) > (leftPos != null ? leftPos : -1))
        {
            leftRecursives.add(child);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterAll(ParsingExpression pe)
    {
        if (pe instanceof LeftRecursive)
        {
            leftRecursiveStackPositions.pop();
        }

        stackPositions.remove(pe);
        --stackDepth;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected ParsingExpression[] children(ParsingExpression pe)
    {
        return pe.firsts();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
