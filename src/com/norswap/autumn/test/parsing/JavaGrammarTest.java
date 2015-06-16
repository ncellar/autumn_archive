package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.expressions.instrument.StackTrace;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.graph.FirstCalculator;
import com.norswap.autumn.parsing.graph.FunctionalTransformer;
import com.norswap.autumn.parsing.graph.LeftRecursionBreaker;
import com.norswap.autumn.parsing.graph.nullability.NullabilityCalculator;
import com.norswap.autumn.parsing.support.GrammarDriver;
import com.norswap.autumn.util.Glob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public final class JavaGrammarTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        ParsingExpression[] rules = GrammarDriver.compile(grammarFile);

        NullabilityCalculator nullCalc = new NullabilityCalculator();
        nullCalc.run(rules);
        FirstCalculator.nullCalc = nullCalc;

        LeftRecursionBreaker.breakCycles(rules);

        ParsingExpression root = rules[0];

        root = FunctionalTransformer.apply(root, JavaGrammarTest::transform, true);

        ParsingExpression whitespace = Arrays.stream(rules)
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().get();

        parseDirectory("../guava", root, whitespace);
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

    public static void parseFile(
        String file, ParsingExpression root, ParserConfiguration config)
    {
        try
        {
            Source source = Source.fromFile(file);
            Parser parser = new Parser(source, config);
            parser.parse(root);

            if (parser.succeeded())
            {
                //System.err.println(filename);
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

    // ---------------------------------------------------------------------------------------------

    private static ParsingExpression transform(ParsingExpression pe)
    {
        StackTrace out = new StackTrace();
        out.operand = pe;
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
