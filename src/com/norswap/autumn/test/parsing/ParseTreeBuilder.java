package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.tree.ParseTree;
import com.norswap.util.Array;

import java.util.Collections;

public class ParseTreeBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseTree $(ParseTree... children)
    {
        return new ParseTree(null, null, Collections.emptySet(), new Array<>(children));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, ParseTree... children)
    {
        return new ParseTree(accessor, null, Collections.emptySet(), new Array<>(children));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, String value, ParseTree... children)
    {
        return new ParseTree(accessor, value, Collections.emptySet(), new Array<>(children));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
