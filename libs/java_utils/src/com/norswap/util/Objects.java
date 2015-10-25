package com.norswap.util;

/**
 * Utilities on all objects.
 */
public final class Objects
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Compares two objects. true if both o1 and o2 are null, false if a single one is null,
     * otherwise equivalent to {@code o1.equals(o2)}.
     */
    public boolean equals(Object o1, Object o2)
    {
        return o1 == o2 || o1 != null && o2 != null && o1.equals(o2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
