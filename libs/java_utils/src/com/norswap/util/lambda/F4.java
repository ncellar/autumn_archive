package com.norswap.util.lambda;

@FunctionalInterface
public interface F4<R, T1, T2, T3, T4>
{
    R apply(T1 t1, T2 t2, T3 t3, T4 t4);

    default F3<R, T2, T3, T4> curry(T1 t1)
    {
        return (x, y, z) -> apply(t1, x, y, z);
    }

    default F3<R, T1, T3, T4> curry2(T2 t2)
    {
        return (x, y, z) -> apply(x, t2, y, z);
    }

    default F3<R, T1, T2, T4> curry3(T3 t3)
    {
        return (x, y, z) -> apply(x, y, t3, z);
    }

    default F3<R, T1, T2, T3> curry4(T4 t4)
    {
        return (x, y, z) -> apply(x, y, z, t4);
    }
}
