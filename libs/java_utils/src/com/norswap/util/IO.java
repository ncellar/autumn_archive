package com.norswap.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class IO
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reads a complete file and returns its contents as a string.
     */
    public static String readFile(String file)
    {
        try {
            return new String(Files.readAllBytes(Paths.get(file)));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
