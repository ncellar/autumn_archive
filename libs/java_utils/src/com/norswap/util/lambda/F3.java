package com.norswap.util.lambda;

@FunctionalInterface
public interface F3<R, T1, T2, T3>
{
    R apply(T1 t1, T2 t2, T3 t3);

    default F2<R, T2, T3> curry(T1 t1)
    {
        return (x, y) -> apply(t1, x, y);
    }

    default F2<R, T1, T3> curry2(T2 t2)
    {
        return (x, y) -> apply(x, t2, y);
    }

    default F2<R, T1, T2> curry3(T3 t3)
    {
        return (x, y) -> apply(x, y, t3);
    }
}