package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.test.antlr.Java7Lexer;
import com.norswap.autumn.test.antlr.Java7Parser;
import com.norswap.autumn.test.antlr.Java8Lexer;
import com.norswap.autumn.test.antlr.Java8Parser;
import com.norswap.util.Array;
import com.norswap.util.Glob;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public final class AntlrBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Array<Duration> durations = new Array<>();
        Instant start = Instant.now();
        Instant mid = start;
        int iters = 20;

        for (int i = 0; i < iters; ++i)
        {
            parseDirectory(
                //"../guava");
                "/Users/nilaurent/Documents/spring-framework");

            Instant tmp = Instant.now();
            durations.add(Duration.between(mid, tmp));
            mid = tmp;
        }
        Instant end = Instant.now();
        System.out.println("Code parsed in: " + Duration.between(start, end).dividedBy(iters));
        System.out.println(durations);
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseDirectory(String directory) throws IOException
    {
        for (Path path: Glob.glob("**/*.java", new File(directory).toPath()))
        {
            parseFile(path.toString());
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseFile(String file)
    {
        try {
            //Lexer lexer = new Java8Lexer(new ANTLRFileStream(file));
            Lexer lexer = new Java7Lexer(new ANTLRFileStream(file));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            //Java8Parser parser = new Java8Parser(tokens);
            Java7Parser parser = new Java7Parser(tokens);

            ParserRuleContext t = parser.compilationUnit();

        }
        catch (Exception e)
        {
            // In case of error, ANTLR will print it regardless.
            System.err.println(file);
            System.err.println("parser exception: " + e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
