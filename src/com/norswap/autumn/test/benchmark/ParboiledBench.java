package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.test.parboiled.ParboiledJava6Parser;
import com.norswap.util.Array;
import com.norswap.util.Glob;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

public class ParboiledBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        ParboiledJava6Parser parser = Parboiled.createParser(ParboiledJava6Parser.class);
        Rule root = parser.CompilationUnit();
        // Rule root = parser.CompilationUnit().suppressNode(); // don't build parse tree

        Array<Duration> durations = new Array<>();
        Instant start = Instant.now();
        Instant mid = start;
        int iters = 20;

        for (int i = 0; i < iters; ++i)
        {
            parseDirectory(
                //"../guava", root);
                "/Users/nilaurent/Documents/spring-framework", root);

            Instant tmp = Instant.now();
            durations.add(Duration.between(mid, tmp));
            mid = tmp;
        }

        Instant end = Instant.now();
        System.out.println("Code parsed in: " + Duration.between(start, end).dividedBy(iters));
        System.out.println(durations);
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseDirectory(String directory, Rule root) throws IOException
    {
        for (Path path: Glob.glob("**/*.java", new File(directory).toPath()))
        {
            parseFile(path.toString(), root);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseFile(String file, Rule root) throws IOException
    {
        try {

            File f = new File(file);
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
            byte[] buffer = new byte[(int) f.length()];
            stream.read(buffer);
            stream.close();

            String source = new String(buffer, "UTF-8");
            ParsingResult<?> result = new ReportingParseRunner(root).run(source);

            if (result.matched)
            {
            }
            else
            {
                System.out.printf(
                    "\nParse error(s) in file '%s':\n%s", file, printParseErrors(result));
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + file);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
