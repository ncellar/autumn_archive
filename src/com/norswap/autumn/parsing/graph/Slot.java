package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * Represents an assignable parsing expression slot (a child) in another parsing expression.
 */
public final class Slot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;
    public final int index;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Slot(ParsingExpression pe, int index)
    {
        this.pe = pe;
        this.index = index;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression get()
    {
        return pe.children()[index];
    }

    // ---------------------------------------------------------------------------------------------

    public void set(ParsingExpression child)
    {
        pe.setChild(index, child);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
