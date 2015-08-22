package com.norswap.util;

/**
 * A pair of items of the same type.
 */
public class Two<T> extends Pair<T, T>
{
    public Two(T one, T two)
    {
        super(one, two);
    }
}
