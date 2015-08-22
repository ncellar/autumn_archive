package com.norswap.util;

public final class Strings
{
    public static String times(int n, String string)
    {
        return new String(new char[n]).replace("\0", string);
    }
}
