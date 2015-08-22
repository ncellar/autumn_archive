package com.norswap.autumn;

import com.norswap.autumn.parsing.config.DefaultErrorHandler;
import com.norswap.autumn.parsing.config.DefaultMemoHandler;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.config.ErrorStrategy;
import com.norswap.autumn.parsing.ParseException;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.config.MemoStrategy;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Printer;
import com.norswap.autumn.parsing.graph.Replacer;
import com.norswap.autumn.parsing.graph.LeftRecursionDetector;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.GrammarGrammar;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class Autumn
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@code parseSource(grammarFromFile(grammarFile), Source.fromFile(string), ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseString(String grammarFile, String string)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(string), ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammarFromFile(grammarFile), Source.fromFile(string), config)}
     */
    public static ParseResult parseString(String grammarFile, String string, ParserConfiguration config)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(string), config);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammar, Source.fromString(string), ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseString(Grammar grammar, String string)
    {
        return parseSource(grammar, Source.fromString(string), ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammar, Source.fromString(string), config)}
     */
    public static ParseResult parseString(Grammar grammar, String string, ParserConfiguration config)
    {
        return parseSource(grammar, Source.fromString(string), config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@code parseSource(grammarFromFile(grammarFile), Source.fromFile(inputFile), ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseFile(String grammarFile, String inputFile)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(inputFile), ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammarFromFile(grammarFile), Source.fromFile(inputFile), config)}
     */
    public static ParseResult parseFile(String grammarFile, String inputFile, ParserConfiguration config)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(inputFile), config);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammar, Source.fromFile(inputFile), ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseFile(Grammar grammar, String inputFile)
        throws IOException
    {
        return parseSource(grammar, Source.fromFile(inputFile), ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammar, Source.fromFile(inputFile), config)}
     */
    public static ParseResult parseFile(Grammar grammar, String inputFile, ParserConfiguration config)
        throws IOException
    {
        return parseSource(grammar, Source.fromFile(inputFile), config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@code parseSource(grammarFromFile(grammarFile), source, ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseSource(String grammarFile, Source source)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), source, ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammarFromFile(grammarFile), source, config)}
     */
    public static ParseResult parseSource(String grammarFile, Source source, ParserConfiguration config)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), source, config);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code parseSource(grammar, source, ParserConfiguration.DEFAULT)}
     */
    public static ParseResult parseSource(Grammar grammar, Source source)
    {
        return parseSource(grammar, source, ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parse the given source, using the given grammar and the given parser configuration.
     *
     * TODO
     */
    public static ParseResult parseSource(Grammar grammar, Source source, ParserConfiguration config)
    {
        return Parser.parse(grammar, source, config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@code return grammarFromSource(Source.fromFile(grammarFile));}
     */
    public static Grammar grammarFromFile(String grammarFile) throws IOException
    {
        return grammarFromSource(Source.fromFile(grammarFile));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code return grammarFromSource(Source.fromString(grammarString));}
     */
    public static Grammar grammarFromString(String grammarString)
    {
        return grammarFromSource(Source.fromString(grammarString));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parses and compiles the grammar specification contained in the passed source.
     *
     * TODO exceptions
     */
    public static Grammar grammarFromSource(Source source)
    {
        ParseResult result = Parser.parse(GrammarGrammar.grammar, source);

        if (!result.matched)
        {
            throw new ParseException(result.error);
        }
        else
        {
            return GrammarCompiler.compile(result.tree);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code return grammarFromExpression(pe, Collections.emptyList(), whitespace, true);}
     */
    public static Grammar grammarFromExpression(ParsingExpression pe, ParsingExpression whitespace)
    {
        return grammarFromExpression(pe, Collections.emptyList(), whitespace, true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code return grammarFromExpression(pe, Collections.emptyList(), Whitespace.DEFAULT(), true);}
     */
    public static Grammar grammarFromExpression(ParsingExpression pe)
    {
        return grammarFromExpression(pe, Collections.emptyList(), Whitespace.DEFAULT(), true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Make a new grammar given a root, a collection of rules, a whitespace expression and an
     * indication whether to match whitespace at the start of the input.
     * <p>
     * The rule set can be empty. The semantics are the same whether the rule set include the root
     * and the whitespace expression (or either) or not.
     *
     * TODO preprocess in grammar
     */
    public static Grammar grammarFromExpression(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace,
        boolean preprocess)
    {
        Grammar grammar = new Grammar(root, rules, whitespace, processLeadingWhitespace);

        if (preprocess)
        {
            grammar.walk(new ReferenceResolver());

            LeftRecursionDetector detector = new LeftRecursionDetector(grammar);
            grammar.walk(detector);

            grammar.walk(new Replacer(detector.leftRecursives));
        }

        return grammar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@code return new ParserConfiguration(errorStrat, DefaultMemoizationStrategy::new);}
     */
    public static ParserConfiguration configuration(ErrorStrategy errorStrat)
    {
        return new ParserConfiguration(errorStrat, DefaultMemoHandler::new);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@code return new ParserConfiguration(DefaultErrorHandler::new, memoStrat);}
     */
    public static ParserConfiguration configuration(MemoStrategy memoStrat)
    {
        return new ParserConfiguration(DefaultErrorHandler::new, memoStrat);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Make a new configuration using the supplied error handling strategy and memoization strategy.
     * This configuration can be shared between parsers.
     */
    public static ParserConfiguration configuration(ErrorStrategy errorStrat, MemoStrategy memoStrat)
    {
        return new ParserConfiguration(errorStrat, memoStrat);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
