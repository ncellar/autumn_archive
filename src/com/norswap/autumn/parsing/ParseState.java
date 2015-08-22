package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.HandleMap;

import static com.norswap.autumn.parsing.Registry.*; // PSF_*

public final class ParseState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int start;
    public int blackStart;
    public int precedence;

    public int flags;
    public Array<Seed> seeds;

    public String accessor;
    public Array<String> tags;

    // output
    public int end;
    public int blackEnd;
    public ParseTree tree;
    public Array<String> cuts;

    public int treeChildrenCount;
    public int cutsCount;

    public HandleMap ext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseState root()
    {
        ParseState root = new ParseState();
        root.end = 0;
        root.blackEnd = 0;
        root.tags = new Array<>();
        root.cuts = new Array<>();
        root.ext = new HandleMap();
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    private ParseState()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public ParseState(ParseState parent)
    {
        this.start = parent.start;
        this.blackStart = parent.blackStart;
        this.precedence = parent.precedence;
        this.seeds = parent.seeds;
        this.flags = parent.flags;
        this.accessor = parent.accessor;
        this.tags = parent.tags;
        this.end = parent.end;
        this.blackEnd = parent.blackEnd;
        this.tree = parent.tree;
        this.treeChildrenCount = parent.treeChildrenCount;
        this.cuts = parent.cuts;
        this.cutsCount = parent.cutsCount;
        this.ext = parent.ext;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the seed for the given expression, if any; otherwise returns null.
     */
    public OutputChanges getSeed(ParsingExpression pe)
    {
        if (seeds == null)
        {
            return null;
        }

        for (Seed seed: seeds)
        {
            if (seed.expression == pe)
            {
                return seed.changes;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a seed for the given expression.
     */
    public void pushSheed(ParsingExpression pe, OutputChanges changes)
    {
        if (seeds == null)
        {
            seeds = new Array<>();
        }

        seeds.push(new Seed(pe, changes));
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the seed of the innermost left-recursion or exression cluster being parsed.
     */
    public void setSeed(OutputChanges changes)
    {
        seeds.peek().changes = changes;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes the seed of the innermost left-recursive or expression cluster being parsed.
     */
    public OutputChanges popSeed()
    {
        return seeds.pop().changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void advance()
    {
        if (end > start)
        {
            seeds = null;
        }

        start = end;
        blackStart = blackEnd;
        treeChildrenCount = tree.childrenCount();
        cutsCount = cuts.size();
    }

    // ---------------------------------------------------------------------------------------------

    public void advance(int n)
    {
        if (n != 0)
        {
            end += n;
            blackEnd = end;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void resetOutput()
    {
        end = start;
        blackEnd = blackStart;

        if (tree.children != null)
        {
            tree.children.truncate(treeChildrenCount);
        }

        // Of course, we don't reset cuts; they are only meaningful in case of failure!
    }

    // ---------------------------------------------------------------------------------------------

    public void resetAllOutput()
    {
        resetOutput();
        cuts.truncate(cutsCount);
    }

    // ---------------------------------------------------------------------------------------------

    public void fail()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    // ---------------------------------------------------------------------------------------------

    public void merge(ParseState child)
    {
        this.end = child.end;
        this.blackEnd = child.blackEnd;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Standard Flags
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void forbidMemoization()
    {
        setFlags(PSF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isMemoizationForbidden()
    {
        return hasFlagsSet(PSF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public void forbidErrorRecording()
    {
        setFlags(PSF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isErrorRecordingForbidden()
    {
        return hasFlagsSet(PSF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public void setGroupingCapture()
    {
        setFlags(PSF_GROUPING_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCaptureGrouping()
    {
        return hasFlagsSet(PSF_GROUPING_CAPTURE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Generic Flag Manipulation Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    public boolean hasFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    public void setFlags(int flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    public void clearFlags(int flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
