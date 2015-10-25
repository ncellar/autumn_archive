package com.norswap.autumn.parsing.source;

import com.norswap.util.Encoding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Builder pattern for {@link Source}.
 */
public final class SourceBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String string;
    private String identifier;
    private int columnStart = 0;
    private int tabSize = 4;
    private Charset encoding = Encoding.UTF_8;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    SourceBuilder() {}

    // ---------------------------------------------------------------------------------------------

    public static SourceBuilder fromString(String string)
    {
        SourceBuilder out = new SourceBuilder();
        out.string = string + '\0';
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public static SourceBuilder fromFile(String filename) throws IOException
    {
        return fromFile(filename, Encoding.UTF_8);
    }

    // ---------------------------------------------------------------------------------------------

    public static SourceBuilder fromFile(String filename, Charset charset) throws IOException
    {
        SourceBuilder out = new SourceBuilder();
        out.identifier = filename;

        File file = new File(filename);
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

        byte[] terminator = "\0".getBytes(charset);
        byte[] data = new byte[(int) file.length() + terminator.length];

        stream.read(data);
        stream.close();

        // EOF terminator
        System.arraycopy(terminator, 0, data, data.length - terminator.length, terminator.length);

        out.string = new String(data, charset);

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SourceBuilder identifier(String identifier)
    {
        this.identifier = identifier;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public SourceBuilder columnStart(int start)
    {
        this.columnStart = start;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public SourceBuilder tabSize(int size)
    {
        this.tabSize = size;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public SourceBuilder encoding(Charset charset)
    {
        this.encoding = charset;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Source build()
    {
        return Source.fromZeroTerminatedString(string, identifier, columnStart, tabSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
