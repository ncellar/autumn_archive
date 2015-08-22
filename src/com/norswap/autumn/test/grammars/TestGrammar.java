package com.norswap.autumn.test.grammars;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;

import java.io.IOException;

public final class TestGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Autumn.grammarFromFile(grammarFile);

        ParseResult result = Autumn.parseFile(grammar, "src/com/norswap/autumn/test/grammars/Syntax.test");

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }

        System.err.println(result.tree.toTreeString());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

