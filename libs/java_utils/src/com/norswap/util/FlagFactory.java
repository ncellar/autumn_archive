package com.norswap.util;

/**
 * Instances sequentially generate all 1-bit flags for 32-bit or 64-bit integers.
 * (From the least-significant bit flag to the most-significant bit flag)
 */
public class FlagFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int next()
    {
        if (next >= 32)
        {
            throw new RuntimeException("Flag space (32 flags) exhausted.");
        }

        return 1 << next++;
    }

    // ---------------------------------------------------------------------------------------------

    public long next64()
    {
        if (next >= 64)
        {
            throw new RuntimeException("Flag space (64 flags) exhausted.");
        }

        return 1L << next++;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
