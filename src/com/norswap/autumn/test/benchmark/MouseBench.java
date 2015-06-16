package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.test.mouse.MouseJava8Parser;
import com.norswap.autumn.util.Glob;
import mouse.runtime.Source;
import mouse.runtime.SourceFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class MouseBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        MouseJava8Parser parser = new MouseJava8Parser();

        Instant start = Instant.now();
        int iters = 1;
        for (int i = 0; i < iters; ++i)
        {
            parseDirectory("../guava", parser);
        }
        Instant end = Instant.now();
        System.out.println("Guava parsed in: " + Duration.between(start, end).dividedBy(iters));
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseDirectory(String directory, MouseJava8Parser parser) throws IOException
    {
        System.err.println(new File(directory).getCanonicalPath());
        for (Path path: Glob.glob("**/*.java", new File(directory).getCanonicalFile().toPath()))
        {
            parseFile(path.toString(), parser);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseFile(String file, MouseJava8Parser parser)
    {
        Source source = new SourceFile(file);

        if (parser.parse(source))
        {
        }
        else
        {
            System.err.println(file);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
