package com.norswap.autumn.parsing.tree;

import com.norswap.util.Array;
import com.norswap.util.Strings;
import com.norswap.util.annotations.NonNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A node of the final parse tree, that can be queried by the user to build some other trees and
 * data structures.
 * <p>
 * Beware that most of the methods can return null if asking for something that isn't there.
 * <p>
 * Such a tree is acquired by calling {@link BuildParseTree#build} on an instance of {@link
 * BuildParseTree}.
 */
public final class ParseTree implements Iterable<ParseTree>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String accessor;

    public final String value;

    private @NonNull Set<String> tags;

    private @NonNull Array<ParseTree> children;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree(String accessor, String value, @NonNull Set<String> tags, @NonNull Array<ParseTree> children)
    {
        this.accessor = accessor;
        this.value = value;
        this.tags = tags;
        this.children = children;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Does this node have the given tag?
     */
    boolean hasTag(String tag)
    {
        return tags.contains(tag);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the first child with the given accessor, or null.
     */
    public ParseTree get(String accessor)
    {
        return children.first(x -> x.accessor.equals(accessor));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the value of the first child with the given accessor, or null.
     */
    public String value(String accessor)
    {
        ParseTree child = get(accessor);
        return child == null ? null : child.value;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns all the children with the given accessor.
     */
    public Array<ParseTree> group(String accessor)
    {
        return children.filter(x -> x.accessor.equals(accessor));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns all the children.
     */
    public Array<ParseTree> children()
    {
        return children;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the first child, or null.
     */
    public ParseTree child()
    {
        return children.size() > 0 ? children.get(0) : null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the child at the given index, or null.
     */
    public ParseTree child(int i)
    {
        return i >= 0 && i < children.size() ? children.get(i) : null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return all nodes that have the given tag).
     */
    public Array<ParseTree> tagged(String tag)
    {
        return children.filter(x -> x.hasTag(tag));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does the tree have a child with the given accessor?
     */
    public boolean has(String accessor)
    {
        return get(accessor) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<ParseTree> iterator()
    {
        return children.iterator();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

        if (accessor != null)
        {
            builder.append(accessor);

            if (tags == null && value != null)
            {
                builder.append(" - ");
            }
            else if (tags != null)
            {
                builder.append(" ");
            }
        }

        if (tags != null)
        {
            builder.append(tags);

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
            Objects.equals(accessor, that.accessor)
         && Objects.equals(value,    that.value)
         && tags.equals(that.tags)
         && children.equals(that.children);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = accessor != null ? accessor.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + tags.hashCode();
        result = 31 * result + children.hashCode();
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}