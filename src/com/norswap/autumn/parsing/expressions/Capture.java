package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;
import com.norswap.util.annotations.NonNull;

import static com.norswap.autumn.parsing.ParsingExpressionFlags.*; // PEF_*

/**
 * Invokes its operand on the input, succeeding if the operand does, with the same end position.
 * <p>
 * This either specify the capture of its operand or specify an alteration (specifying the accessor
 * or adding tags) of captures occuring during the invocation of its operand, which it does by
 * wrapping the new {@link BuildParseTree} instances in other {@link BuildParseTree} instances. The
 * structure will be flattened by {@link BuildParseTree#build}.
 * <p>
 * If {@link #shouldCapture}, adds a new child node to the current parse tree node. This node
 * becomes the current parse tree node for the invocation of the operand. If {@link
 * #shouldCaptureText}, the text matching the captured expression will be saved.
 */
public final class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;
    public Array<String> tags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(ParsingExpression operand, String accessor, @NonNull Array<String> tags, int flags)
    {
        this.operand = operand;
        this.accessor = accessor;
        this.tags = tags;
        this.flags = flags;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (shouldCapture())
        {
            if (operand == null) // marker capture
            {
                BuildParseTree newTree = new BuildParseTree();
                newTree.accessor = accessor;
                newTree.tags = tags;
                state.tree.add(newTree);
            }
            else
            {
                // save
                BuildParseTree oldTree = state.tree;
                int oldCount = state.treeChildrenCount;

                // setup
                BuildParseTree newTree = state.tree = new BuildParseTree();
                state.treeChildrenCount = 0;

                // parse
                operand.parse(parser, state);

                // restore
                state.tree = oldTree;
                state.treeChildrenCount = oldCount;

                // add new into old
                if (state.succeeded())
                {
                    oldTree.add(newTree);
                    newTree.accessor = accessor;
                    newTree.tags = tags;

                    if (shouldCaptureText())
                    {
                        newTree.value = parser.text
                            .subSequence(state.start, state.blackEnd)
                            .toString();
                    }
                }
            }
        }
        else
        {
            operand.parse(parser, state);

            int start = state.treeChildrenCount;
            int end = state.tree.childrenCount();

            for (int i = start; i < end; ++i)
            {
                annotate(i, state.tree);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void annotate(int i, BuildParseTree parent)
    {
        if (accessor == null && tags == null)
        {
            return;
        }

        BuildParseTree oldTree = parent.child(i);
        BuildParseTree newTree = new BuildParseTree();
        newTree.tags = tags;
        newTree.wrappee = oldTree;

        if (accessor != null)
        {
            if (oldTree.accessor != null)
            {
                throw new RuntimeException(String.format(
                    "Trying to override accessor \"%s\" with accessor \"%s\".",
                    oldTree.accessor,
                    accessor));
            }

            newTree.accessor = accessor;
        }

        parent.setChild(i, newTree);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return String.format("accessor: %s, tags: %s, capture: %s",
            accessor, tags,
            shouldCaptureText() ? "text" : shouldGroup() ? "group" : shouldCapture());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] children()
    {
        return operand != null
            ? super.children()
            : new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean shouldCapture()
    {
        return (flags & PEF_CAPTURE) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean shouldCaptureText()
    {
        return (flags & PEF_CAPTURE_TEXT) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean shouldGroup()
    {
        return (flags & PEF_CAPTURE_GROUPED) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
