package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.capture.ParseTreeBuild;
import com.norswap.util.Array;

/**
 * See {@link ParseState}, "Committed and Uncommitted State" section.
 */
public final class ParseChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;
    public final int blackEnd;
    public final Array<ParseTreeBuild> children;
    public final Array<Object> customChanges;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges(
        int end,
        int blackEnd,
        Array<ParseTreeBuild> children,
        Array<Object> customChanges)
    {
        this.end = end;
        this.blackEnd = blackEnd;
        this.children = children;
        this.customChanges = customChanges;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseChanges failure()
    {
        return new ParseChanges(-1, -1, Array.empty(), Array.empty());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
