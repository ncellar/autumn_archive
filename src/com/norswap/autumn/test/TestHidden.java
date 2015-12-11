package com.norswap.autumn.test;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.extensions.tracer.TracerExtension;
import com.norswap.autumn.parsing.source.Source;

import java.io.IOException;

public final class TestHidden
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarText =
            "A = [b]? C ;" +
            "C = A [a] / [a] ;";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromString(grammarText).build())
            .withExtension(new TracerExtension())
            .build();

        ParseResult result = Autumn.parseString(grammar, "bbaa");

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }

        System.err.println(result.tree);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
