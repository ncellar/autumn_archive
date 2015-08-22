package com.norswap.util.slot;

/**
 * A slot that encapsulates its value.
 * This is classic indirection.
 */
public class SelfSlot<T> implements Slot<T>
{
    private T t;

    public SelfSlot(T t)
    {
        this.t = t;
    }

    @Override
    public Slot<T> set(T t)
    {
        this.t = t;
        return this;
    }

    @Override
    public T get()
    {
        return t;
    }
}
