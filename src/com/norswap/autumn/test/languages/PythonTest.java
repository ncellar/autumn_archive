package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.test.languages.python.PythonExtension;
import com.norswap.util.Glob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PythonTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "grammars/Python.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).columnStart(1).build())
            .withExtension(new PythonExtension())
            .build();

        for (Path path: Glob.glob("**/*.py", Paths.get("../_readonly/django-1.9")))
        {
            // These are templates to be preprocessed.
            if (match(path, "../_readonly/django-1.9/django/conf/app_template")
            ||  match(path, "../_readonly/django-1.9/tests/template_tests")
            // Python 3 syntax.
            ||  match(path, "../_readonly/django-1.9/tests/view_tests/tests/py3")
            // Inconsistent indents.
            || match(path, "../_readonly/django-1.9/django/contrib/gis/gdal/envelope.py")
            || match(path, "../_readonly/django-1.9/django/test/testcases.py"))
                continue;

            System.err.println(path);
            ParseResult result = Autumn.parseFile(grammar, path.toString());

            if (!result.matched)
            {
                System.err.println(result.error.message());

                return;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean match(Path path, String start)
    {
        return path.toString().startsWith(start.replace('/', File.separatorChar));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
