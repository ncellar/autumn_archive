package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;

import java.util.function.Supplier;

public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int end;
    public int blackEnd;
    public ParseTree tree;
    public Array<String> cuts;
    public HandleMap ext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static OutputChanges failure()
    {
        OutputChanges changes = new OutputChanges();
        changes.end = -1;
        changes.blackEnd = -1;
        return changes;
    }

    // ---------------------------------------------------------------------------------------------

    private OutputChanges()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges(ParseState state)
    {
        this.end = state.end;
        this.blackEnd = state.blackEnd;
        this.tree = new ParseTree();
        this.cuts = new Array<>();

        Supplier<String> x = "x"::toString;

        for (int i = state.treeChildrenCount; i < state.tree.childrenCount(); ++i)
        {
            this.tree.add(state.tree.children.get(i));
        }

        for (int i = state.cutsCount; i < state.cuts.size(); ++i)
        {
            this.cuts.add(state.cuts.get(i));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void mergeInto(ParseState state)
    {
        state.end = end;
        state.blackEnd = blackEnd;

        if (tree != null)
        {
            state.tree.add(tree);
        }

        if (cuts != null)
        {
            state.cuts.addAll(cuts);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
