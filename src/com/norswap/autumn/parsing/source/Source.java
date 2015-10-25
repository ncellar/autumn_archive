package com.norswap.autumn.parsing.source;

import com.norswap.util.Strings;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Contains the source text and associated meta-data.
 * <p>
 * The source text is available as a string whose final final character is 0 (handy to detect EOF).
 * <p>
 * Each source has an optional string identifier which is used to refer to it in textual output.
 * <p>
 * Users can configure how wide a tab character should appear (default: 4), and at which character
 * lines start (default: 0). Lines always start at 1.
 */
public final class Source
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String text;
    public final String identifier;
    public final int columnStart;
    public final int tabSize;
    private LineMap lineMap;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The length of the source text, excluding the NUL terminator.
     */
    public int length()
    {
        return text.length() - 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // private because text needs to be 0-terminated and tabs needs to be replaced

    private Source(String text, String identifier, int columnStart, int tabSize)
    {
        this.text = text;
        this.identifier = identifier;
        this.columnStart = columnStart;
        this.tabSize = tabSize;
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromZeroTerminatedString(String string, String identifier, int lineStart, int tabSize)
    {
        string = string.replaceAll("\t", Strings.times(tabSize, " "));
        return new Source(string, identifier, lineStart, tabSize);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromString(String string, String identifier, int lineStart, int tabSize)
    {
        return fromZeroTerminatedString(string + '\0', identifier, lineStart, tabSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static SourceBuilder fromString(String string)
    {
        return SourceBuilder.fromString(string);
    }

    // ---------------------------------------------------------------------------------------------

    public static SourceBuilder fromFile(String filename) throws IOException
    {
        return SourceBuilder.fromFile(filename);
    }

    // ---------------------------------------------------------------------------------------------

    public static SourceBuilder fromFile(String filename, Charset encoding) throws IOException
    {
        return SourceBuilder.fromFile(filename, encoding);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition position(int fileOffset)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.positionFromOffset(fileOffset);
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(TextPosition position)
    {
        return fileOffset(position.line, position.column);
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(int line, int column)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.offset(line, column, columnStart);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Prints a position, prefixed by this source's identifier.
     */
    String posToString(TextPosition position)
    {
        return position.toString() + " in source \"" + identifier + "\"";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
