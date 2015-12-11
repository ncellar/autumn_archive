package com.norswap.autumn.parsing.capture;

import java.util.HashSet;

/**
 * Adds a new kind to the node. If the node has no kind yet, also sets its principal kind. This
 * means the principal kind will be the one occuring the lowest in the parse tree.
 */
public final class DecorateWithKind implements Decorate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String kind;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DecorateWithKind(String kind)
    {
        this.kind = kind;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void decorate(ParseTree tree)
    {
        if (tree.kinds == null)
        {
            tree.kind = kind;
            tree.kinds = new HashSet<>();
        }

        tree.kinds.add(kind);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return "kind(" + kind + ")";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
