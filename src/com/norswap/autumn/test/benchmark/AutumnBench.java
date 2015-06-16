package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.graph.FirstCalculator;
import com.norswap.autumn.parsing.graph.LeftRecursionBreaker;
import com.norswap.autumn.parsing.graph.nullability.NullabilityCalculator;
import com.norswap.autumn.parsing.support.GrammarDriver;
import com.norswap.autumn.util.Glob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class AutumnBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        //System.in.read();

        Instant start = Instant.now();

        ParsingExpression[] rules = GrammarDriver.compile(grammarFile);

        Instant end = Instant.now();
        System.out.println("Grammar compiled in: " + Duration.between(start, end));

        ParsingExpression whitespace = Arrays.stream(rules)
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().get();

        NullabilityCalculator nullCalc = new NullabilityCalculator();
        nullCalc.run(rules);

        // Test NullabilityCalculator
        //nullCalc.nullables().forEach(x -> System.err.println(x));

        FirstCalculator.nullCalc = nullCalc;
        /*
        FirstCalculator firstCalc = new FirstCalculator();
        firstCalc.run(rules);
        */

        // Test FirstCalculator
        //firstCalc.first.entrySet().stream().forEach(e -> System.err.println(e));

        LeftRecursionBreaker leftBreaker = new LeftRecursionBreaker();
        rules = leftBreaker.run(rules);

        /*
        // Left Recursion Breaker Test
        {
            Source source = Source.fromString("xxxxx");
            Parser parser = new Parser(source, new ParserConfiguration());
            parser.parse(rules[rules.length - 1]);
            System.err.println("breaker test: " + parser.succeeded());
        }

        //*/

        ParsingExpression root = rules[0];

        start = Instant.now();
        int iters = 1;
        for (int i = 0; i < iters; ++i)
        {
            parseDirectory("../guava", root, whitespace);
        }
        end = Instant.now();
        System.out.println("Guava parsed in: " + Duration.between(start, end).dividedBy(iters));
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseDirectory(
        String directory, ParsingExpression root, ParsingExpression whitespace)
        throws IOException
    {
        for (Path path: Glob.glob("**/*.java", new File(directory).toPath()))
        {
            ParserConfiguration config = new ParserConfiguration();
            config.whitespace = () -> whitespace;

            parseFile(path.toString(), root, config);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseFile(
        String file, ParsingExpression root, ParserConfiguration config)
    {
        try
        {
            Source source = Source.fromFile(file);
            Parser parser = new Parser(source, config);
            parser.parse(root);

            if (parser.succeeded())
            {
                //System.err.println(file);
                //System.out.println(parser.tree());
            }
            else
            {
                System.err.println(file);
                parser.report();
                System.err.println();
                System.exit(-1);
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + file);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
