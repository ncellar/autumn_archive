package com.norswap.autumn.test;

import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;

public final class TestConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParserConfiguration parserConfig = new ParserConfiguration();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Parser parser(String src)
    {
        return new Parser(Source.fromString(src), parserConfig);
    }

    public static Parser parser(Source src)
    {
        return new Parser(src, parserConfig);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
