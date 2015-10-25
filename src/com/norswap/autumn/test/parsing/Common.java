package com.norswap.autumn.test.parsing;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.tree.ParseTree;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.TestFailed;

public final class Common
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parse(ParsingExpression pe, String string)
    {
        return Autumn.parseString(Grammar.fromRoot(pe).build(), string);
    }

    // ---------------------------------------------------------------------------------------------

    public static void ensureMatch(ParsingExpression pe, String string)
    {
        ensureMatch(parse(pe, string));
    }

    // ---------------------------------------------------------------------------------------------

    public static void ensureMatch(ParseResult result)
    {
        if (!result.succeeded)
        {
            throw new TestFailed("expecting match, got failure");
        }

        if (!result.matched)
        {
            throw new TestFailed(
                "expecting match, got success with end position: " + result.endPosition);
        }
    }


    // ---------------------------------------------------------------------------------------------

    public static void ensureSuccess(ParsingExpression pe, String string)
    {
        ensureSuccess(parse(pe, string));
    }

    // ---------------------------------------------------------------------------------------------

    public static void ensureSuccess(ParseResult result)
    {
        if (!result.succeeded)
        {
            throw new TestFailed("expecting success, got failure");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static void ensureFail(ParsingExpression pe, String string)
    {
        ensureFail(parse(pe, string));
    }

    // ---------------------------------------------------------------------------------------------

    public static void ensureFail(ParseResult result)
    {
        if (result.matched)
        {
            throw new TestFailed("expecting failure, got match");
        }

        if (result.succeeded)
        {
            throw new TestFailed(
                "expecting failure, got success with end position: " + result.endPosition);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree tree(ParsingExpression pe, String string)
    {
        ParseResult result = parse(pe, string);
        ensureMatch(result);
        return result.tree;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
