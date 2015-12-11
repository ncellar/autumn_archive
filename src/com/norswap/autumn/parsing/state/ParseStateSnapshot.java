package com.norswap.autumn.parsing.state;

/**
 * See {@link ParseState}, "Snapshots" section.
 */
public final class ParseStateSnapshot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int start;
    public final int blackStart;
    public final int end;
    public final int blackEnd;
    public final int treeChildrenCount;
    public final Object[] customSnapshots;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseStateSnapshot(
        int start,
        int blackStart,
        int end,
        int blackEnd,
        int treeChildrenCount,
        Object[] customSnapshots)
    {
        this.start = start;
        this.blackStart = blackStart;
        this.end = end;
        this.blackEnd = blackEnd;
        this.treeChildrenCount = treeChildrenCount;
        this.customSnapshots = customSnapshots;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
