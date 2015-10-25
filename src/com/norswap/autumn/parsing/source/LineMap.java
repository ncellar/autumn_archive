package com.norswap.autumn.parsing.source;

import java.util.Arrays;

/**
 * Maps line numbers to their file offsets and allows the reverse mapping in O(log(number of
 * lines)).
 * <p>
 * The class implementation assumes that lines start at 1, while columns start at 0, but allows to
 * add an offset for the starting index of columns when getting position pairs out of the class
 * (using the {@code columnStart} parameter).
 */
public final class LineMap
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int[] linePositions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LineMap(CharSequence seq)
    {
        linePositions = new int[128];
        linePositions[0] = -1;
        linePositions[1] = 0;
        int next = 2;

        for (int i = 0; i < seq.length(); ++i)
        {
            char c = seq.charAt(i);

            if (c == '\n')
            {
                if (next == linePositions.length)
                {
                    linePositions = Arrays.copyOf(linePositions, linePositions.length * 2);
                }

                linePositions[next++] = i + 1;
            }
        }

        linePositions = Arrays.copyOf(linePositions, next);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Return a position for the given offset, assuming that columns start at 0.
     */
    public TextPosition positionFromOffset(int offset)
    {
        return positionFromOffset(offset, 0);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the given offset, assuming that columns start at 0.
     */
    public TextPosition positionFromOffset(int offset, int columnStart)
    {
        int line = Arrays.binarySearch(linePositions, offset);

        if (line >= 0)
        {
            return new TextPosition(offset, line, 0);
        }
        else
        {
            line = -line - 2;

            return new TextPosition(offset, line, offset - linePositions[line] + columnStart);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the (line, column) pair, assuming that columns start at 0.
     */
    public TextPosition position(int line, int column)
    {
        return new TextPosition(offset(line, column), line, column);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the (line, column) pair, assuming that columns start at {@code
     * columnStart}.
     */
    public TextPosition position(int line, int column, int columnStart)
    {
        return new TextPosition(offset(line, column - columnStart), line, column);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the file offset for the (line, column) pair, assuming that columns start at 0.
     */
    public int offset(int line, int column)
    {
        return linePositions[line] + column;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the file offset for the (line, column) pair, assuming that columns start at {@code
     * columnStart}.
     */
    public int offset(int line, int column, int columnStart)
    {
        return linePositions[line] + column - columnStart;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
