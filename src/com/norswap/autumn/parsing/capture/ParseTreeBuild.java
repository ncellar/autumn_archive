package com.norswap.autumn.parsing.capture;

import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.Strings;
import com.norswap.util.annotations.NonNull;
import java.util.Arrays;

/**
 * A parse tree node, as constructed by the parser. After the parse, this tree will be transformed
 * to a user-visible {@link ParseTree}.
 * <p>
 * Only the instances of this class with the {@link #capture} flag will be converted into proper
 * parse tree nodes, the other only describe changes to be applied to their children, via their
 * {@link #decorations} field.
 * <p>
 * For instances with the {@link #capture} flag, the changes described by the decorations are
 * applied to the node itself.
 * <p>
 * {@link #value} describe the matched text for node with text capture. This isn't a decoration,
 * because the captured text is only known after parsing the children of a {@link Capture} parsing
 * expression, and the decorations are added before that.
 */
public final class ParseTreeBuild
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final boolean capture;

    private final @NonNull Decorate[] decorations;
    private @NonNull Array<ParseTreeBuild> children = EMPTY_BUILD;
    public String value;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final ParseTree[]            EMPTY_TREE      = new ParseTree[0];
    private static final Array<ParseTreeBuild>  EMPTY_BUILD     = Array.empty();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTreeBuild(boolean capture, @NonNull Decorate[] decorations)
    {
        this.capture = capture;
        this.decorations = decorations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addChild(ParseTreeBuild child)
    {
        if (children == EMPTY_BUILD) children = new Array<>();
        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addChildren(Array<ParseTreeBuild> array)
    {
        if (children == EMPTY_BUILD) children = new Array<>();
        children.addAll(array);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        if (children == EMPTY_BUILD) return;
        children.truncate(size);
    }

    // ---------------------------------------------------------------------------------------------

    public int childrenCount()
    {
        return children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public Array<ParseTreeBuild> children()
    {
        return children;
    }

    // ---------------------------------------------------------------------------------------------

    public Array<ParseTreeBuild> childrenFromIndex(int i)
    {
        return children != EMPTY_BUILD
            ? children.copyFromIndex(i)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    public @NonNull ParseTree[] build()
    {
        ParseTree[] concatenatedChildren = children == EMPTY_BUILD
            ? EMPTY_TREE
            : JArrays.concat(
                ParseTree[]::new,
                children.mapToArray(
                    ParseTreeBuild::build,
                    ParseTree[][]::new));

        ParseTree[] out = capture
            ? new ParseTree[]{ new ParseTree(value, concatenatedChildren) }
            : concatenatedChildren;

        for (ParseTree node: out)
            for (Decorate deco: decorations)
                deco.decorate(node);

        return out;
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
        builder.append(capture);
        builder.append(" ");
        builder.append(Arrays.toString(decorations));
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

        for (ParseTreeBuild child: children)
            child.toString(builder, depth + 1);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}