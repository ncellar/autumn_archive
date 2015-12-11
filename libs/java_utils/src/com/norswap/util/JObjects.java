package com.norswap.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities on all objects, use to supplement {@link Objects}.
 */
public final class JObjects
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Same as {@link Objects#hashCode}, but does not clash with the object's own {@link
     * Object#hashCode} method.
     */
    public static int hash(Object o)
    {
        return o == null ? 0 : o.hashCode();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Same as {@link Objects#equals}, but does not clash with the object's own {@link
     * Object#equals} method.
     * <p>
     * For arrays, use {@link Arrays#equals} !
     */
    public static boolean same(Object a, Object b)
    {
        return (a == b) || (a != null && a.equals(b));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
