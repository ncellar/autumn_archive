package com.norswap.util;

/**
 * A pair of items.
 */
public class Pair<A, B> implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static <A, B> Pair<A, B> $(A a, B b)
    {
        return new Pair<>(a, b);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public A a;
    public B b;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Pair(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object object)
    {
        Pair<?, ?> other;
        return object instanceof Pair
            && (other = (Pair<?, ?>) object) != null
            && (a == null ? other.a == null : a.equals(other.a))
            && (b == null ? other.b == null : b.equals(other.b));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "(" + a + ", " + b + ")";
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Pair<A, B> clone()
    {
        return (Pair<A, B>) Exceptions.rt(() -> super.clone());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
