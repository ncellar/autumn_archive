package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.slot.Slot;

/**
 * A slot corresponding to an indexed child of a parsing expression.
 */
public class ChildSlot implements Slot<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A read-only child slot ({@link #set} is a no-op).
     */
    public static final class ReadOnly extends ChildSlot
    {
        public ReadOnly(ParsingExpression pe, int index)
        {
            super(pe, index);
        }

        @Override
        public Slot<ParsingExpression> set(ParsingExpression child)
        {
            return this;
        }
    }

    public ChildSlot(ParsingExpression pe, int index)
    {
        this.pe = pe;
        this.index = index;
    }

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        pe.setChild(index, child);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;
    public final int index;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression get()
    {
        return pe.children()[index];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "child [" + index + "] of " + pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
