package com.norswap.autumn.parsing;

/**
 * Denotes a position in a file by both its character position in the file and a (line, column)
 * pair.
 *
 * Following the tradition, lines start at 1, while file positions and columns start at 0.
 */
public final class TextPosition
{
    public final int position;
    public final int line;
    public final int column;

    public TextPosition(int position, int line, int column)
    {
        this.position = position;
        this.line = line;
        this.column = column;
    }

    public String toString()
    {
        return "line " + line + ", column " + column;
    }
}
