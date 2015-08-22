package com.norswap.util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class Glob
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a list of all the paths that match the given glob pattern within the given directory.
     * The patter syntax is described in the doc of {@link FileSystem#getPathMatcher} --
     * the "glob:" part should be omitted.
     */
    public static List<Path> glob(String pattern, Path directory) throws IOException
    {
        PathMatcher matcher =
            FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        ArrayList<Path> result = new ArrayList<>();

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (matcher.matches(file))
                {
                    result.add(file);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                return FileVisitResult.CONTINUE;
            }
        });

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
