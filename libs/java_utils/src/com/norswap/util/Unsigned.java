package com.norswap.util;

/**
 * This class supplies three things:
 *
 * 1) Methods to encode positive values as unsigned quantities in  types where they would not
 *    normally fit. For instances, values [0, 255] can be encoded in a byte,
 *    whose range is normally [-128,  127].
 *
 * 2) The reverse methods, which decode a unsigned values encoded in a signed type.
 *
 * 3) Constants indicated the maximum positive value that can be encoded in a types.
 *
 * If you need to manipulate unsigned values > 2^63 -1, use {@link java.math.BigInteger}.
 */
public final class Unsigned
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Positive representation of the maximum unsigned quantity that can be encoded in a byte.
     */
    public static short MAX_BYTE = Byte.MIN_VALUE * -2;

    /**
     * Positive representation of the maximum unsigned quantity that can be encoded in a short.
     */
    public static int MAX_SHORT = Short.MIN_VALUE * -2;

    /**
     * Positive representation of the maximum unsigned quantity that can be encoded in an int.
     */
    public static long MAX_INT = (long) Integer.MIN_VALUE * -2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get a positive short value representing the unsigned quantity encoded in the byte value.
     */
    public static short fromByte(byte b)
    {
        return (short) (b & 0xff);
    }

    /**
     * Get a positive int value representing the unsigned quantity encoded in the short value.
     */
    public static int fromShort(short s)
    {
        return s & 0xffff;
    }

    /**
     * Get a positive long value representing the unsigned quantity encoded in the int value.
     */
    public static long fromInt(int i)
    {
        return i & 0xffff_ffffl;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Encode the given positive value < {@link #MAX_BYTE} in a byte.
     */
    public static byte encodeAsByte(long i)
    {
        assert i < Byte.MIN_VALUE * -2;
        return (byte) (i <= Byte.MAX_VALUE ? i : i - Byte.MIN_VALUE * -2);
    }

    /**
     * Encode the given positive value < {@link #MAX_SHORT} in a short.
     */
    public static short encodeAsShort(long i)
    {
        assert i < Short.MIN_VALUE * -2;
        return (short) (i <= Short.MAX_VALUE ? i : i - Short.MIN_VALUE * -2);
    }

    /**
     * Encode the given positive value < {@link #MAX_INT} in an int.
     */
    public static int encodeAsInt(long i)
    {
        assert i < (long) Integer.MIN_VALUE * -2;
        return (int) (i <= Integer.MAX_VALUE ? i : i - (long) Integer.MIN_VALUE * -2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
