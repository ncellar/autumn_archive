package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.state.CustomState.Snapshot;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

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
    public final @Nullable Array<Seed> seeds;
    public final Snapshot[] customSnapshots;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseStateSnapshot(
        int start,
        int blackStart,
        int end,
        int blackEnd,
        int treeChildrenCount,
        Array<Seed> seeds,
        Snapshot[] customSnapshots)
    {
        this.start = start;
        this.blackStart = blackStart;
        this.end = end;
        this.blackEnd = blackEnd;
        this.treeChildrenCount = treeChildrenCount;
        this.seeds = seeds;
        this.customSnapshots = customSnapshots;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
