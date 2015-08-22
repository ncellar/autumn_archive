package com.norswap.autumn.test;

/**
 * A bunch of static assertion method to express expectations on test outcomes.
 *
 * In case an expectation is violated, a {@link TestFailed} exception is thrown.
 */
public final class Ensure
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void ensure(boolean test)
    {
        if (!test)
        {
            throw new TestFailed();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T> void equals(T x, T y)
    {
        if (!x.equals(y))
        {
            throw new TestFailed("got: " + x + ", expected: " + y);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T> void different(T x, T y)
    {
        if (x.equals(y))
        {
            throw new TestFailed("got: " + x + ", expected it to be different");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T extends Comparable<T>> void greaterThan(T x, T y)
    {
        if (x.compareTo(y) <= 0)
        {
            throw new TestFailed("got: " + x + ", not greater than: " + y);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T extends Comparable<T>> void lessThan(T x, T y)
    {
        if (x.compareTo(y) >= 0)
        {
            throw new TestFailed("got: " + x + ", not less than: " + y);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
