package com.norswap.autumn.test;

import com.norswap.autumn.test.parsing.FeatureTests;
import com.norswap.autumn.test.parsing.OperatorTests;

public final class Main
{
    /**
     * Runs all tests.
     */
    public static void main(String[] args)
    {
        OperatorTests.run();
        FeatureTests.run();
    }
}
