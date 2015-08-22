package com.norswap.util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Exceptions
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Like {@link java.util.function.Supplier}, but allows throwing checked exceptions from
     * the get method.
     */
    @ FunctionalInterface
    public static interface ThrowingSupplier<T>
    {
        T get() throws Exception;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link java.lang.Runnable}, but allows throwing checked exceptions from
     * the run method.
     */
    @ FunctionalInterface
    public static interface ThrowingRunnable
    {
        void run() throws Exception;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link java.util.function.Function} but allows throwing checked exceptions from the
     * apply method.
     */
    @ FunctionalInterface
    public static interface ThrowingFunction<T, R>
    {
        R apply(T t) throws Exception;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Throw an {@link java.lang.AssertionError} Can be used in places where expressions are
     * expected.
     */
    public static <X> X assertionError(String msg)
    {
        throw new AssertionError(msg);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns {@code supplier.get()}, re-throwing any exception thrown as a run-time exception.
     */
    public static <T> T rt(ThrowingSupplier<T> supplier)
    {
        try {
            return supplier.get();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns {@code supplier.get()}, returning null instead if it threw an exception.
     */
    public static <T> T swallow(ThrowingSupplier<T> supplier)
    {
        try {
            return supplier.get();
        }
        catch (Exception e) {
            return null;
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Runs {@code runnable.run()}, re-throwing any exception thrown as a run-time exception.
     */
    public static void rt(ThrowingRunnable runnable)
    {
        try {
            runnable.run();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Runs {@code runnable.run()}, swallowing any thrown exception.
     */
    public static void swallow(ThrowingRunnable runnable)
    {
        try {
            runnable.run();
        }
        catch (Exception e) {}
    }

    // ---------------------------------------------------------------------------------------------

    public static <T, R> R rt(ThrowingFunction<T, R> function, T t)
    {
        try {
            return function.apply(t);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static <T, R> R swallow(ThrowingFunction<T, R> function, T t)
    {
        try {
            return function.apply(t);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a supplier that returns {@code supplier.get()} but re-throws any exceptions it might
     * throw as run-time exceptions.
     */
    public static <T> Supplier<T> rting(ThrowingSupplier<T> supplier)
    {
        return () -> rt(supplier);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a supplier that returns {@code supplier.get()} or null if an exception was thrown.
     */
    public static <T> Supplier<T> swallowing(ThrowingSupplier<T> supplier)
    {
        return () -> swallow(supplier);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a runnable that runs {@code runnable.run()} but re-throws any exceptions it might
     * throw as run-time exceptions.
     */
    public static Runnable rting(ThrowingRunnable runnable)
    {
        return () -> rt(runnable);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a runnable that runs {@code runnable.run()} but swallows any exceptions it might
     * throw.
     */
    public static Runnable swallowing(ThrowingRunnable runnable)
    {
        return () -> swallow(runnable);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a supplier that runs {@code supplier.get()} but re-throws any exceptions it might
     * throw as run-time exceptions.
     */
    public static <T, R> Function<T, R> rting(ThrowingFunction<T, R> supplier)
    {
        return (t) -> rt(supplier, t);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a supplier that returns {@code supplier.get()} or null if an exception was thrown.
     */
    public static <T, R> Function<T, R> swallowing(ThrowingFunction<T, R> supplier)
    {
        return (t) -> swallow(supplier, t);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}