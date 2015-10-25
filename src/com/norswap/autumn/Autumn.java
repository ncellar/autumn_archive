package com.norswap.autumn;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.source.Source;

import java.io.IOException;

/**
 * A collection of entry points into the library for the most common tasks.
 */
public final class Autumn
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A STRING
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseString(Grammar grammar, String string)
    {
        return parseSource(
            grammar,
            Source.fromString(string).build(),
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseString(Grammar grammar, String string, ParserConfiguration config)
    {
        return parseSource(
            grammar,
            Source.fromString(string).build(),
            config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A FILE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseFile(Grammar grammar, String inputFile)
        throws IOException
    {
        return parseSource(
            grammar,
            Source.fromFile(inputFile).build(),
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseFile(Grammar grammar, String inputFile, ParserConfiguration config)
        throws IOException
    {
        return parseSource(
            grammar,
            Source.fromFile(inputFile).build(),
            config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A SOURCE OBJECT
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseSource(Grammar grammar, Source source)
    {
        return parseSource(
            grammar,
            source,
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseSource(Grammar grammar, Source source, ParserConfiguration config)
    {
        return new Parser(grammar, source, config).parseRoot();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
