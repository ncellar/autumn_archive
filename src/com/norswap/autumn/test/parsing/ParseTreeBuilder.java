package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.util.Array;

public class ParseTreeBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseTree $(ParseTree... children)
    {
        ParseTree tree = new ParseTree();
        tree.children = new Array<>(children);
        return tree;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String name, ParseTree... children)
    {
        ParseTree tree = new ParseTree();
        tree.name = name;
        tree.children = new Array<>(children);
        return tree;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String name, String value, ParseTree... children)
    {
        ParseTree tree = $(name, children);
        tree.value = value;
        return tree;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
