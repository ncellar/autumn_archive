package com.norswap.autumn.parsing.source;

/**
 * Denotes a position in a file by both its character position in the file and a (line, column)
 * pair.
 * <p>
 * Lines are numbered from 1, while columns start at an offset determined by the {@link Source}
 * object that created this text position (usually it's 0 or 1).
 */
public final class TextPosition
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final int line;
    public final int column;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition(int position, int line, int column)
    {
        this.position = position;
        this.line = line;
        this.column = column;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "line " + line + ", column " + column;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
