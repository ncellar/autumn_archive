package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph.NodeState;
import com.norswap.util.graph.Slot;

import java.util.HashMap;
import java.util.function.Function;

/**
 *
 */
public final class Transformer extends ParsingExpressionVisitor
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Function<ParsingExpression, ParsingExpression> transform;

    private HashMap<ParsingExpression, ParsingExpression> transformations = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Transformer(Function<ParsingExpression, ParsingExpression> transform)
    {
        this.transform = transform;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void afterChild(Slot<ParsingExpression> parent, Slot<ParsingExpression> child, NodeState state)
    {
        child.assigned = transformations.computeIfAbsent(child.initial, key -> transform.apply(key));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<ParsingExpression> slot, NodeState state)
    {
        afterChild(null, slot, state);

    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void applyChanges(Array<Slot<ParsingExpression>> modified)
    {
        for (Slot<ParsingExpression> slot: modified)
        {
            if (slot.parent != null)
            {
                slot.parent.setChild(slot.index, slot.assigned);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        super.conclude();
        this.transformations = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
