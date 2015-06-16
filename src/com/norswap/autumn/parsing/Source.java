package com.norswap.autumn.parsing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Contains the source text and associated meta-data.
 *
 * The source text is available as a CharSequence whose final character is 0 (handy to detect EOF).
 */
public final class Source
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String text;

    private String filename;

    private LineMap lineMap;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CharSequence text()
    {
        return text;
    }

    /**
     * The length of the source text, excluding the NUL terminator.
     */
    public int length()
    {
        return text.length() - 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source() {}

    // ---------------------------------------------------------------------------------------------

    public static Source fromFile(String filename) throws IOException
    {
        File file = new File(filename);
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        byte[] data = new byte[(int) file.length() + 1];
        stream.read(data);
        stream.close();

        // EOF terminator
        data[data.length - 1] = 0;

        Source result = new Source();
        result.text = new String(data, "UTF-8");

        result.filename = filename;

        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromString(String string)
    {
        byte[] data = new byte[(int) string.length() + 1];
        System.arraycopy(string.getBytes(), 0, data, 0, string.length());

        // EOF terminator
        data[data.length - 1] = 0;

        Source result = new Source();
        result.text = new String(data);

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition position(int fileOffset)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.position(fileOffset);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int fileOffset(int line, int column)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.fileOffset(line, column);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
