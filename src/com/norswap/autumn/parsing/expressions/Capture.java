package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.util.Array;

import static com.norswap.autumn.parsing.Registry.*; // PEF_* PSF_*

/**
 * Invokes its operand on the input, succeeding if the operand does, with the same end position.
 * <p>
 * This either specify the capture of its operand or alters the effect of captures occuring during
 * the invocation of its operand by modifying the parse state.
 * <p>
 * For these captures, the accessor will be {@link #accessor} or {@link ParseState#accessor} (which
 * overrides the former). The tags will be the union of {@link #tags} and {@link ParseState#tags}.
 * If {@link #shouldGroup}, the capture will belong to a group of captures with the same accessor.
 * <p>
 * Capture specifications do not accumulate: after a capture is performed, the {@link
 * ParseState#accessor} and {@link ParseState#tags} are reset for the children of the captured
 * expression.
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

    @Override
    public void parse(Parser parser, ParseState state)
    {
        int oldFlags = state.flags;
        String oldAccessor = state.accessor;

        if (shouldGroup())
        {
            state.setGroupingCapture();
        }

        if (state.accessor == null)
        {
            state.accessor = accessor;
        }

        int oldTagsCount = state.tags.size();
        state.tags.addAll(tags);

        if (!shouldCapture())
        {
            operand.parse(parser, state);
        }
        else
        {
            ParseTree oldTree = state.tree;

            ParseTree newTree = new ParseTree(
                state.accessor,
                state.tags != null && !state.tags.isEmpty()
                    ? state.tags.clone()
                    : null,
                state.isCaptureGrouping());

            int oldCount = state.treeChildrenCount;
            Array<String> oldTags = state.tags;

            state.accessor = null;
            state.tags = new Array<>();
            state.tree = newTree;
            state.treeChildrenCount = 0;

            operand.parse(parser, state);

            state.tags = oldTags;
            state.tree = oldTree;
            state.treeChildrenCount = oldCount;

            if (state.succeeded())
            {
                oldTree.add(newTree);

                if (shouldCaptureText())
                {
                    newTree.value = parser.text
                        .subSequence(state.start, state.blackEnd)
                        .toString();
                }
            }
        }

        state.flags = oldFlags;
        state.accessor = oldAccessor;
        state.tags.truncate(oldTagsCount);
    }

    // ---------------------------------------------------------------------------------------------

    public void addTag(String tag)
    {
        if (tags == null)
        {
            tags = new Array<>();
        }

        tags.add(tag);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownPrintableData()
    {
        return String.format("accessor: %s, tags: %s, capture: %s",
            accessor, tags,
            shouldCaptureText() ? "text" : shouldGroup() ? "group" : shouldCapture());
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
