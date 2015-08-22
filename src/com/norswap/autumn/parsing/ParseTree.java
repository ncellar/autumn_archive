package com.norswap.autumn.parsing;

import com.norswap.util.Array;
import com.norswap.util.Strings;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class ParseTree implements Iterable<ParseTree>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;
    public String value;
    public boolean group;
    public Array<String> tags;
    public Array<ParseTree> children;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree() {}

    public ParseTree(String accessor, Array<String> tags, boolean group)
    {
        this.accessor = accessor;
        this.tags = tags;
        this.group = group;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int childrenCount()
    {
        return children == null ? 0 : children.size();
    }

    // ---------------------------------------------------------------------------------------------

    void truncateChildren(int childrenCount)
    {
        children.truncate(childrenCount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void add(ParseTree child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Iterable<ParseTree> children)
    {
        children.forEach(this::add);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree getOrNull(String accessor)
    {
        if (children == null)
        {
            return null;
        }

        for (ParseTree child: children)
        {
            if (accessor.equals(child.accessor))
            {
                return child;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree get(String accessor)
    {
        ParseTree node = getOrNull(accessor);

        if (node == null)
        {
            throw new RuntimeException(
                "Node \"" + this.accessor + "\" doesn't have a child named \"" + accessor + "\"");
        }

        return node;
    }

    // ---------------------------------------------------------------------------------------------

    public String value(String accessor)
    {
        ParseTree node = get(accessor);

        if (node.value == null)
        {
            throw new RuntimeException(
                "Node \"" + accessor + "\" under node \"" + this.accessor + "\" doesn't have a value.");
        }

        return node.value;
    }

    // ---------------------------------------------------------------------------------------------

    public List<ParseTree> group(String accessor)
    {
        Array<ParseTree> group = new Array<>();

        for (ParseTree child: children)
        {
            if (accessor.equals(child.accessor))
            {
                if (!child.group)
                {
                    throw new RuntimeException(
                        "Node " + child.info() + " under node " + info() + " doesn't belong to a group.");
                }

                group.add(child);
            }
        }

        return group;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree child()
    {
        return child(0);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree child(int i)
    {
        if (children == null || children.size() <= i)
        {
            throw new RuntimeException(
                "Node " + info() + " doesn't have a child with index: " + i);
        }

        return children.get(i);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean has(String accessor)
    {
        return getOrNull(accessor) != null;
    }

    // ---------------------------------------------------------------------------------------------

    public Iterable<ParseTree> tagged(String tag)
    {
        // TODO array filter function

        return children.stream()
            .filter(child -> child.hasTag(tag))
            .collect(Collectors.toCollection(() -> new Array<>()));
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree tagged1(String tag)
    {
        for (ParseTree child: children)
        {
            if (child.hasTag(tag))
            {
                return child;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasTag(String tag)
    {
        return tags != null && tags.contains(tag);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String info()
    {
        return String.format("[%s%s%s]",
            accessor != null ? accessor : "",
            accessor != null && tags != null ? " / " : "",
            tags != null ? tags : "");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        int trailing = 0;

        if (accessor != null)
        {
            builder.append(accessor);
            builder.append(": ");
            trailing = 2;
        }

        if (children != null && !children.isEmpty())
        {
            builder.append("[");

            for (ParseTree child: children)
            {
                builder.append(child);
                builder.append(", ");
            }

            if (children.size() > 0)
            {
                builder.setLength(builder.length() - 2);
            }

            builder.append("] ");
            trailing = 1;
        }

        if (value != null)
        {
            builder.append(" \"");
            builder.append(value);
            builder.append("\" ");
            trailing = 1;
        }

        if (tags != null && !tags.isEmpty())
        {
            builder.append(" (");

            for (String tag: tags)
            {
                builder.append(tag);
                builder.append(", ");
            }

            if (tags.size() > 0)
            {
                builder.setLength(builder.length() - 2);
            }

            builder.append(") ");
            trailing = 1;

        }

        builder.setLength(builder.length() - trailing);

        if (trailing == 0)
        {
            builder.append("[]");
        }

        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public String toTreeString()
    {
        StringBuilder builder = new StringBuilder();
        toTreeString(builder, 0);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void toTreeString(StringBuilder builder, int depth)
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

        if (children != null)
        {
            for (ParseTree child: children)
            {
                child.toTreeString(builder, depth + 1);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<ParseTree> iterator()
    {
        return children != null
            ? children.iterator()
            : Collections.emptyIterator();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        // mostly auto-generated; reformated & modified to accept null == empty

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ParseTree pt = (ParseTree) o;

        if (group != pt.group)
            return false;

        if (accessor != null ? !accessor.equals(pt.accessor) : pt.accessor != null)
            return false;

        if (value != null ? !value.equals(pt.value) : pt.value != null)
            return false;

        if (tags != null
                ? !tags.equals(pt.tags)
                : (pt.tags != null && !pt.tags.isEmpty()))
            return false;

        if (children != null
                ? !children.equals(pt.children)
                : (pt.children != null && !pt.children.isEmpty()))
            return false;

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        // mostly auto-generated; reformated & modified to accept null empty

        int result = accessor != null ? accessor.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (tags == null || tags.isEmpty() ? 0 : tags.hashCode());
        result = 31 * result + (children == null || children.isEmpty() ? 0 : children.hashCode());
        result = 31 * result + (group ? 1 : 0);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
