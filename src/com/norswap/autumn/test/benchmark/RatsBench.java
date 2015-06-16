package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.test.rats.RatsJava7Parser;
import com.norswap.autumn.util.Glob;
import xtc.parser.ParseError;
import xtc.parser.Result;
import xtc.parser.SemanticValue;
import xtc.tree.Node;
import xtc.tree.Printer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class RatsBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Instant start = Instant.now();
        int iters = 1;
        for (int i = 0; i < iters; ++i)
        {
            parseDirectory("../guava");
        }
        Instant end = Instant.now();
        System.out.println("Guava parsed in: " + Duration.between(start, end).dividedBy(iters));
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
            Reader reader = new BufferedReader(new FileReader(file));
            RatsJava7Parser parser = new RatsJava7Parser(reader, file);
            Result result = parser.pCompilationUnit(0);

            if (result.hasValue())
            {
                /* // Print AST

                SemanticValue v = (SemanticValue)result;

                if (v.value instanceof Node)
                {
                    Printer ptr =
                        new Printer(new BufferedWriter(new OutputStreamWriter(System.out)));

                    ptr.format((Node)v.value).pln().flush();
                }
                else
                {
                    System.out.println(v.value.toString());
                }
                */
            }
            else
            {
                ParseError err = (ParseError) result;
                if (-1 == err.index) {
                    System.err.println("  Parse error");
                } else {
                    System.err.println("  " + parser.location(err.index) + ": " + err.msg);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + file);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
