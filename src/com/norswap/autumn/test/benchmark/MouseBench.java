package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.test.mouse.MouseJava8Parser;
import com.norswap.util.Array;
import com.norswap.util.Glob;
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

        Array<Duration> durations = new Array<>();
        Instant start = Instant.now();
        Instant mid = start;
        int iters = 1;

        for (int i = 0; i < iters; ++i)
        {
            parseDirectory(
                //"../guava", parser);
                "/Users/nilaurent/Documents/spring-framework", parser);

            Instant tmp = Instant.now();
            durations.add(Duration.between(mid, tmp));
            mid = tmp;
        }

        Instant end = Instant.now();
        System.out.println("Code parsed in: " + Duration.between(start, end).dividedBy(iters));
        System.out.println(durations);
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

        try {
            if (parser.parse(source))
            {
            }
            else
            {
                System.err.println(file);
            }
        }
        catch (Exception e)
        {
            System.err.println(file);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
