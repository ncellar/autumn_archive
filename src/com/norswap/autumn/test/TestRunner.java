package com.norswap.autumn.test;

/**
 * Runs a series of test (instances of {@link Runnable}), potentially stopping after the first
 * error, as signaled by an exception. Use {@link TestFailed} to signal failures to meet
 * expectations.
 */
public final class TestRunner
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean stopOnFirstError = true;

    public Runnable[] tests;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TestRunner(Runnable[] tests)
    {
        this.tests = tests;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void run()
    {
        for (Runnable test : tests)
        {
            if (stopOnFirstError)
            {
                test.run();
            }
            else
            {
                try
                {
                    test.run();
                }

                // NOTE(norswap): No need to differentiate for now.

                catch (TestFailed fail)
                {
                    fail.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
