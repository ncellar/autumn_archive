package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.ParseTreeBuild;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.capture.Decorate;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.annotations.NonNull;
import java.util.Arrays;

/**
 * Invokes its operand on the input, succeeding if the operand does, with the same end position.
 * <p>
 * This either specifies the capture of its operand or specifies an modification of captures
 * occuring during the invocation of its operand, via the {@link #decorations} field.
 * <p>
 * This parsing expression results in the creation of a new node in the parse tree. Note however
 * that this parse tree is only a temporary parse tree. See {@link ParseTreeBuild} for
 * explanations.
 */
public class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public @NonNull Decorate[] decorations;
    public boolean capture;
    public boolean captureText;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(
        boolean capture,
        boolean captureText,
        ParsingExpression operand,
        @NonNull Decorate... decorations)
    {
        this.operand = operand;
        this.capture = capture;
        this.captureText = captureText;
        this.decorations = decorations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        // save
        ParseTreeBuild oldTree = state.tree;
        int oldCount = state.treeChildrenCount;

        // setup
        ParseTreeBuild newTree = state.tree = new ParseTreeBuild(capture, decorations);
        state.treeChildrenCount = 0;

        // parse
        operand.parse(parser, state);

        // restore
        state.tree = oldTree;
        state.treeChildrenCount = oldCount;

        // add new into old
        if (state.succeeded())
        {
            if (captureText)
            {
                String value = parser.text
                    .subSequence(state.start, state.blackEnd)
                    .toString();
                newTree.value = value;
            }

            if (capture || newTree.childrenCount() != 0)
                oldTree.addChild(newTree);
        }
    }

    @Override
    public int parseDumb(Parser parser, int position)
    {
        // TODO
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        String s = Arrays.toString(decorations);
        return s.substring(1, s.length() - 1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
