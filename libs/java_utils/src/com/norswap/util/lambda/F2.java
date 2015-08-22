package com.norswap.util.lambda;

import java.util.function.BiFunction;

@FunctionalInterface
public interface F2<R, T1, T2> extends BiFunction<T1, T2, R>
{
    default F1<R, T2> curry(T1 t1)
    {
        return x -> apply(t1, x);
    }

    default F1<R, T1> curry2(T2 t2)
    {
        return x -> apply(x, t2);
    }
}
