package com.norswap.util.slot;

/**
 * A slot is an assignable location for a value of type T.
 */
public interface Slot<T>
{
    Slot<T> set(T t);
    T get();
}
