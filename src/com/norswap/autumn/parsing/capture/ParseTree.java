package com.norswap.autumn.parsing.capture;

import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.Strings;

import com.norswap.util.annotations.NonNull;
import java.util.Arrays;
import java.util.Set;

import static com.norswap.util.JObjects.hash;
import static com.norswap.util.JObjects.same;

/**
 * A parse tree node, as seen by the parser's user.
 */
public final class ParseTree
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;
    public String value;
    public String kind;
    Set<String> kinds;
    @NonNull ParseTree[] children;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree(
        String accessor,
        String value,
        String kind,
        Set<String> kinds,
        @NonNull ParseTree[] children)
    {
        this.accessor = accessor;
        this.value = value;
        this.kind = kind;
        this.kinds = kinds;
        this.children = children;
    }

    // ---------------------------------------------------------------------------------------------

    ParseTree(String value, @NonNull ParseTree[] children)
    {
        this.value = value;
        this.children = children;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // KINDS

    /**
     * Does this node have the given kind?
     */
    public boolean hasKind(String kind)
    {
        return kinds != null && kinds.contains(kind);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: ACCESSORS

    /**
     * Does the tree have a child with the given accessor?
     */
    public boolean has(String accessor)
    {
        return get(accessor) != null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the child with the given accessor, or null.
     */
    public ParseTree get(String accessor)
    {
        return JArrays.first(children, x -> x.accessor.equals(accessor));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the value of the child with the given accessor, or null.
     */
    public String value(String accessor)
    {
        ParseTree child = get(accessor);
        return child == null ? null : child.value;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the group with the given accessor.
     */
    public Array<ParseTree> group(String accessor)
    {
        return JArrays.filter(children, x -> x.accessor.equals(accessor));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: ALL

    /**
     * Returns all the children.
     */
    public ParseTree[] children()
    {
        return children;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: POSITIONAL

    /**
     * Return the first child, or null.
     */
    public ParseTree child()
    {
        return children.length > 0 ? children[0] : null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the child at the given index, or null.
     */
    public ParseTree child(int i)
    {
        return i >= 0 && i < children.length ? children[i] : null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: KINDS

    /**
     * Return all nodes that have the given kind.
     */
    public Array<ParseTree> allWithKind(String kind)
    {
        return JArrays.filter(children, x -> x.hasKind(kind));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TO STRING

    public String nodeToString()
    {
        StringBuilder builder = new StringBuilder();
        nodeToString(builder);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void nodeToString(StringBuilder builder)
    {
        if (accessor == null && kinds == null && value == null)
        {
            builder.append("--");
            return;
        }

        if (accessor != null)
        {
            builder.append(accessor);

            if (kinds == null && value != null)
            {
                builder.append(" - ");
            }
            else if (kinds != null)
            {
                builder.append(" ");
            }
        }

        if (kinds != null)
        {
            builder.append(kinds);

            if (value != null)
            {
                builder.append(" - ");
            }
        }

        if (value != null)
        {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        toString(builder, 0);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void toString(StringBuilder builder, int depth)
    {
        builder.append(Strings.times(depth, "-|"));
        nodeToString(builder);
        builder.append("\n");

        for (ParseTree child: children)
        {
            child.toString(builder, depth + 1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ParseTree)) return false;

        ParseTree that = (ParseTree) o;

        return
           same(accessor,  that.accessor)
        && same(value,     that.value)
        && same(kind,      that.kind)
        && same(kinds,     that.kinds)
        && Arrays.equals(children, that.children);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = hash(accessor);
        result = 31 * result + hash(value);
        result = 31 * result + hash(kind);
        result = 31 * result + hash(kinds);
        result = 31 * result + hash(children);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
