package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.Counter;
import com.norswap.util.slot.Slot;

import java.util.Arrays;

/**
 * This class is a repository of walks over graphs along with modification modes (in-place, copy,
 * read-only).
 */
public class Walks
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Walks the whole graph, modification are done in-place.
     */
    public static GraphWalker<ParsingExpression> inPlace = (pe, visitor) ->
    {
        Counter c = new Counter();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot(pe, c.i++)).toArray();

        return Array.<Slot<ParsingExpression>>fromUnsafe(slots);
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks the whole graph, modifications are applied to a clone of the original parsing
     * expression. Beware this means that the original graph and the new graph will share
     * sub-expressions that are not modified!
     */
    public static GraphWalker<ParsingExpression> copy = (pe, visitor) ->
    {
        Counter c = new Counter();
        ParsingExpression pec = pe.clone();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot(pec, c.i++)).toArray();

        return Array.<Slot<ParsingExpression>>fromUnsafe(slots);
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks the whole graph. Attempts to set the value of a slot result in an exception.
     */
    public static GraphWalker<ParsingExpression> readOnly = (pe, visitor) ->
    {
        Counter c = new Counter();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot.ReadOnly(pe, c.i++)).toArray();

        return Array.<Slot<ParsingExpression>>fromUnsafe(slots);
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks a graph through the children that are part of the FIRST set of the parent parsing
     * expression. A parsing expression's FIRST set contains all descendant parsing expression that
     * can be invoked at the same input position as the parent, because no input has been consumed
     * yet.
     */
    public static GraphWalker<ParsingExpression> inPlaceFirsts(Grammar grammar)
    {
        return (pe, visitor) ->
        {
            Counter c = new Counter();
            Object[] slots = Arrays.stream(pe.firsts(grammar)).map(x -> new ChildSlot(pe, c.i++)).toArray();

            return Array.<Slot<ParsingExpression>>fromUnsafe(slots);
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
