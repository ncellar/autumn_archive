package com.norswap.util;

/**
 * A simple modifiable counter for use inside forEach loops and the such.
 * <p>
 * e.g.:
 * <pre>
 *      Counter c = new Counter();
 *      Arrays.stream(array).forEach(x -> c.i++);
 * </pre>
 */
public class Counter
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int i;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Counter() {}

    // ---------------------------------------------------------------------------------------------

    public Counter(int i)
    {
        this.i = i;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
