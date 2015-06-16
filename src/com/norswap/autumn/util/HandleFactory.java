package com.norswap.autumn.util;

public final class HandleFactory
{
    public final int start;
    public final int end;
    public final int stride;

    private int next;

    public HandleFactory(int start, int end, int stride)
    {
        this.start = start;
        this.end = end;
        this.stride = stride;
        this.next = start;
    }

    public HandleFactory(int stride)
    {
        this(0, 0, stride);
    }

    public HandleFactory()
    {
        this(0, 0, 1);
    }

    public int next()
    {
        if (end > 0 && next >= end)
        {
            throw new RuntimeException("Handle space (2^32 handles / stride) exhausted.");
        }

        int result = next;
        next += stride;
        return result;
    }
}
