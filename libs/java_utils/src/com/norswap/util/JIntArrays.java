package com.norswap.util;

/**
 * int-specialization of {@link JArrays}.
 */
public final class JIntArrays
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A simple syntactic sugar for array creation that will do type inference.
     * When passing 0 parameters, T will default to Object.
     */
    @SafeVarargs
    public static int[] array(int... ts)
    {
        return ts;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Concatenate all arrays into a single newly-allocated one, and returns it.
     * This is a method that is suspiciously absent of Guava, although the primitive pendants
     * do exist.
     */
    @SafeVarargs
    public static int[] concat(int[]... arrays)
    {
        int size = 0;
        for (int[] array : arrays) {
            size += array.length;
        }

        int[] out = arrays.length == 0
            ? Caster.cast(new Object[0])
            : Caster.cast(java.lang.reflect.Array.newInstance(arrays[0].getClass().getComponentType(), size));

        int i = 0;
        for (int[] array : arrays)
        {
            setRange(out, i, array, 0, array.length);
            i += array.length;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Concatenates all items in {@code ts} to the given array.
     */
    public static int[] concat(int[] array, int... ts)
    {
        return concat(array, ts);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copies bytes src[i2] to src[i2 + size - 1] into dst[i1] to dst[i1 + size - 1].
     * Both arrays must be sufficiently large for this to be possible.
     * Returns dst.
     */
    public static int[] setRange(int[] dst, int i1, int[] src, int i2, int size)
    {
        assert dst.length <= i1 + size;
        assert src.length <= i2 + size;

        for (int i = 0 ; i < size ; ++i) {
            dst[i1 + i] = src[i2 + i];
        }

        return dst;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
