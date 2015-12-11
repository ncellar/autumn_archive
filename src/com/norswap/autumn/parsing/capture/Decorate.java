package com.norswap.autumn.parsing.capture;

/**
 * The interface for parse tree decorations: parse tree node modifications to be applied after the
 * parser, when building the final tree. The reason those are not applied during the parse
 * is that sometimes a node is captured by an expression, then a modification is applied to that
 * node by a parent expression. Applying the modification during parse would defeat memoization.
 * Instead a new {@link ParseTreeBuild} node is create with the desired modification but without the
 * {@link ParseTreeBuild#capture} flag.
 */
public interface Decorate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void decorate(ParseTree tree);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
