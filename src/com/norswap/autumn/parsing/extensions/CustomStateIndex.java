package com.norswap.autumn.parsing.extensions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * This class is responsible for allocating indices for grammar extensions. All extensions share the
 * same allocation space, regardless of which grammars they are used in. Indices are allocated
 * sequentially by the thread-safe {@link #allocate()} method.
 * <p>
 * If you went crazy on the extensions and want to avoid a largely sparse custom state array in some
 * grammars, you can change the way indices are allocated by assigning a custom allocator to {@link
 * #allocator}. Your allocator should be thread-safe. Usually you'll want to allow extensions which
 * you know won't be used in the same grammars to use the same index.
 */
public final class CustomStateIndex
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Supplier<Integer> allocator = new DefaultAllocator();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int allocate()
    {
        return allocator.get();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class DefaultAllocator implements Supplier<Integer>
    {
        private AtomicInteger i = new AtomicInteger(0);

        @Override
        public Integer get()
        {
            return i.getAndIncrement();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
