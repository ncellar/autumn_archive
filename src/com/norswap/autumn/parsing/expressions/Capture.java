package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

import static com.norswap.autumn.parsing.Registry.*; // PEF_*

/**
 * Invokes its operand on the input, succeeding if the operand does, with the same end position.
 *
 * On success, adds a new child node to the current parse tree node whose name is {@link #name}.
 * This node becomes the current parse tree node for the invocation of the operand.
 */
public final class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseTree oldTree = state.tree;
        ParseTree newTree = new ParseTree(name);
        int oldCount = state.treeChildrenCount;

        state.tree = newTree;
        state.treeChildrenCount = 0;

        operand.parse(parser, state);

        state.tree = oldTree;
        state.treeChildrenCount = oldCount;

        if (state.succeeded())
        {
            if (isCaptureGrouped())
            {
                oldTree.addGrouped(newTree);
            }
            else
            {
                oldTree.add(newTree);
            }

            if (shouldCaptureText())
            {
                newTree.value = parser.text
                    .subSequence(state.start, state.blackEnd)
                    .toString();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("capture(\"");
        builder.append(name);
        builder.append("\", ");
        operand.toString(builder);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean shouldCaptureText()
    {
        return (flags & PEF_CAPTURE_TEXT) != 0;
    }

    public boolean isCaptureGrouped()
    {
        return (flags & PEF_CAPTURE_GROUPED) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
