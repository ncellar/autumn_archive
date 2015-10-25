package com.norswap.util.graph;

import com.norswap.util.annotations.Nullable;

/**
 * A slot is an assignable location for a graph node. See {@link GraphVisitor}.
 */
public class Slot<Node>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The original value of this slot.
     */
    public final Node initial;

    // ---------------------------------------------------------------------------------------------

    /**
     * The parent of this node, or null if it's a root.
     */
    public final @Nullable Node parent;

    // ---------------------------------------------------------------------------------------------

    /**
     * The index of this node within its parent, or 0 if it's a root.
     */
    public final int index;

    // ---------------------------------------------------------------------------------------------

    /**
     * The value assigned to this slot (or null).
     */
    public @Nullable Node assigned;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Slot(Node initial)
    {
        this(initial, null, 0);
    }

    // ---------------------------------------------------------------------------------------------

    public Slot(Node initial, Node parent, int index)
    {
        this.initial = initial;
        this.parent = parent;
        this.index = index;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return the last assigned value to this slot, or the initial value if no values were assigned,
     * or the assigned value was cleared.
     */
    public Node latest()
    {
        return assigned != null ? assigned : initial;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
