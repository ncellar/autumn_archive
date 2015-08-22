package com.norswap.util;

public final class Caster
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Unchecked and type-inferenced cast that doesn't incur a unchecked cast warning, nor the
     * run-time overhead of type checking.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj)
    {
        return (T) obj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
