package com.norswap.autumn.test.grammars;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.source.Source;

import java.io.IOException;

public final class TestGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).build()).build();

        ParseResult result = Autumn.parseFile(grammar, "src/com/norswap/autumn/test/grammars/Syntax.test");

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }

        System.err.println(result.tree);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

