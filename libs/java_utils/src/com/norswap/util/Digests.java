package com.norswap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Easy access common digest algorithms.
 */
public final class Digests
{
    public static final MessageDigest SHA_1;
    public static final MessageDigest SHA_256;
    public static final MessageDigest MD5;

    static {
        try
        {
            SHA_1 = MessageDigest.getInstance("SHA-1");
            SHA_256 = MessageDigest.getInstance("SHA-256");
            MD5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            // Shouldn't happen, Java implementations are required to include those algorithms.
            throw new RuntimeException(e);
        }
    }
}
