package com.norswap.autumn.test;

/**
 * The exception to throw in case of test failure due to the violation of an expectation.
 *
 * @see {@link Ensure}
 */
public final class TestFailed extends RuntimeException
{
    public TestFailed() {}

    public TestFailed(String message)
    {
        super(message);
    }
}
