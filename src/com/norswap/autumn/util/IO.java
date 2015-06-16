package com.norswap.autumn.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IO
{
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
}
