package com.norswap.util.lambda;

import java.util.function.Function;

@FunctionalInterface
public interface F1<R, T> extends Function<T, R>
{
    default F0<R> curry(T t)
    {
        return () -> apply(t);
    }
}
