package com.norswap.autumn.util;

public class FlagFactory
{
    private int next;

    public int next()
    {
        if (next >= 32)
        {
            throw new RuntimeException("Flag space (32 flags) exhausted.");
        }

        return 1 << next++;
    }
}
