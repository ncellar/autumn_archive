package com.norswap.autumn.parsing;

import java.util.Arrays;

/**
 * Maps line numbers to their file position and allows the reverse mapping in
 * O(log(number of lines)).
 *
 * Following the tradition, lines start at 1, while file positions and columns start at 0.
 */
public final class LineMap
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int[] linePositions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition position(int position)
    {
        int line = Arrays.binarySearch(linePositions, position);

        if (line >= 0)
        {
            return new TextPosition(position, line, 0);
        }
        else
        {
            line = -line - 2;

            return new TextPosition(position, line, position - linePositions[line]);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public TextPosition position(int line, int column)
    {
        return new TextPosition(fileOffset(line, column), line, column);
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(int line, int column)
    {
        return linePositions[line] + column;
    }

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
}
