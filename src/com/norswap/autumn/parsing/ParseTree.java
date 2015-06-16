package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Array;

import java.util.Collections;
import java.util.Iterator;

/**
 * - name != null && !grouped : normal node
 * - name != null && grouped  : group capture
 * - name == null             : container
 */
public final class ParseTree implements Iterable<ParseTree>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;
    public String value;
    public Array<ParseTree> children;
    public boolean grouped;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree(){}

    public ParseTree(String name)
    {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int childrenCount()
    {
        return children == null
            ? 0
            : children.size();
    }

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

        if (child.name == null)
        {
            if (child.children != null)
            {
                children.addAll(child.children);
            }
        }
        else
        {
            children.add(child);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void addGrouped(ParseTree child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        ParseTree container = getOrNull(child.name);

        if (container == null)
        {
            container = new ParseTree(child.name);
            container.grouped = true;
            container.children = new Array<>();
            children.add(container);
        }

        container.children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree getOrNull(String name)
    {
        if (children == null)
        {
            return null;
        }

        for (ParseTree child: children)
        {
            if (name.equals(child.name))
            {
                return child;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree get(String name)
    {
        ParseTree node = getOrNull(name);

        if (node == null)
        {
            throw new RuntimeException(
                "Node \"" + this.name + "\" doesn't have a child named \"" + name + "\"");
        }

        return node;
    }

    // ---------------------------------------------------------------------------------------------

    public String value(String name)
    {
        ParseTree node = get(name);

        if (node.value == null)
        {
            throw new RuntimeException(
                "Node \"" + name + "\" under node \"" + this.name + "\" doesn't have a value.");
        }

        return node.value;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree group(String name)
    {
        ParseTree node = get(name);

        if (!node.grouped)
        {
            throw new RuntimeException(
                "Node \"" + name + "\" under node \"" + this.name + "\" isn't a group.");
        }

        return node;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree child()
    {
        if (children == null || children.size() == 0)
        {
            throw new RuntimeException("Node \"" + name + "\" doesn't have children.");
        }
        else if (children.size() != 1)
        {
            throw new RuntimeException("Node \"" + name + "\" has more than one child.");
        }

        return children.get(0);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree child(int i)
    {
        if (children == null || children.size() <= i)
        {
            throw new RuntimeException(
                "Node \"" + name + "\" doesn't have a child with index: " + i);
        }

        return children.get(i);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean has(String name)
    {
        return getOrNull(name) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        if (name != null)
        {
            builder.append(name);
            builder.append(": ");
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

            builder.append("]");
        }
        else if (value != null)
        {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        }
        else if (name != null)
        {
            builder.setLength(builder.length() - 2);
        }
        else
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
        builder.append(new String(new char[depth]).replace("\0", "-|"));
        builder.append(name != null ? name : "container");
        builder.append("\n");

        if (grouped)
        {
            int i = 0;
            for (ParseTree child: children)
            {
                builder.append(new String(new char[depth + 1]).replace("\0", "-|"));
                builder.append(" ");
                builder.append(i);
                builder.append("\n");

                if (child.children != null)
                for (ParseTree grandChild: child.children)
                {
                    grandChild.toTreeString(builder, depth + 2);
                }

                ++i;
            }
        }
        else if (children != null)
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
        // mostly auto-generated; modified to accept null children == empty children

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ParseTree parseTree = (ParseTree) o;

        if (grouped != parseTree.grouped)
            return false;

        if (name != null ? !name.equals(parseTree.name) : parseTree.name != null)
            return false;

        if (value != null ? !value.equals(parseTree.value) : parseTree.value != null)
            return false;

        return children == null
            ? parseTree.children == null || parseTree.children.isEmpty()
            : children.equals(parseTree.children);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        // mostly auto-generated; modified to accept null children == empty children

        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (children == null || children.isEmpty() ? 0 : children.hashCode());
        result = 31 * result + (grouped ? 1 : 0);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
