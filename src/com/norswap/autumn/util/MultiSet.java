package com.norswap.autumn.util;

import java.util.HashMap;

public class MultiSet<T> extends HashMap<T, Integer>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public MultiSet<T> add(T t, int x)
    {
        Integer count = get(t);

        if (count == null) {
            count = 0;
        }

        put(t, count + x);

        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public MultiSet<T> add(T t)
    {
        return add(t, 1);
    }

    // ---------------------------------------------------------------------------------------------

    public MultiSet<T> drop(T t)
    {
        return add(t, -1);
    }

    // ---------------------------------------------------------------------------------------------

    public MultiSet<T> addAll(Iterable<T> ts)
    {
        for (T t: ts)
        {
            add(t, 1);
        }

        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public MultiSet<T> addAll(T[] ts)
    {
        for (T t: ts)
        {
            add(t, 1);
        }

        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
