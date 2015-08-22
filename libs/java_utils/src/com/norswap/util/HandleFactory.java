package com.norswap.util;

/**
 * A generator of unique handles (int or long).
 *
 * A handle factory is parameterized by a (start, end, stride) triplet. The handles are allocated
 * in the [start, end[ range; unless end is 0, in which case the range extends up to limit of the
 * integer type being used. In any case, the range never wraps around.
 *
 * The stride is the amount by which to increment the handle each time. Using a stride is
 * interesting when taking the remainder of a handle in order to store it in a linear-addressing
 * hash map (as is done in {@link com.norswap.util.HandleMap}). Sequential handles are often used
 * together; using a stride produces holes in the table that avoid probing too far ahead for an
 * empty slot. It is recommended to use an odd stride (3 is a typical value).
 */
public final class HandleFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final long start;
    public final long end;
    public final int stride;

    private long next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Allocates handles on the given range with the given stride.
     * If end is 0, the range extends to the end of the integer range.
     */
    public HandleFactory(int start, int end, int stride)
    {
        this.start = start;
        this.end = end;
        this.stride = stride;
        this.next = start - stride;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Allocates handles on the whole integer range with the given stride.
     */
    public HandleFactory(int stride)
    {
        this(0, 0, stride);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Allocates handles on the whole integer range with a stride of 1.
     */
    public HandleFactory()
    {
        this(0, 0, 1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int next()
    {
        if (end > 0 && next >= end - stride || next >= Integer.MAX_VALUE - stride)
        {
            throw new RuntimeException(
                "Handle space (2^32 handles / stride (" + stride + ")) exhausted.");
        }

        next += stride;
        return (int) next;
    }

    // ---------------------------------------------------------------------------------------------

    public long next64()
    {
        if (end > 0 && next >= end - stride || next >= Long.MAX_VALUE - stride)
        {
            throw new RuntimeException(
                "Handle space (2^64 handles / stride (" + stride + ")) exhausted.");
        }

        next += stride;
        return next;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
