package com.norswap.util;

/**
 * Utilities to deal with bitfields.
 */
public final class Flags
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(int flags, int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasAllFlagsSet(int flags, int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    // ---------------------------------------------------------------------------------------------

    public int setFlags(int flags, int flagsToSet)
    {
        return flags | flagsToSet;
    }

    // ---------------------------------------------------------------------------------------------

    public int clearFlags(int flags, int flagsToClear)
    {
        return flags & ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(long flags, long flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasAllFlagsSet(long flags, long flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    // ---------------------------------------------------------------------------------------------

    public long setFlags(long flags, long flagsToSet)
    {
        return flags | flagsToSet;
    }

    // ---------------------------------------------------------------------------------------------

    public long clearFlags(long flags, long flagsToClear)
    {
        return flags & ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
