package com.norswap.util.slot;

/**
 * * A slot corresponding to an array position.
 */
public class ArraySlot<T> implements Slot<T>
{
    public final T[] array;
    public final int index;

    public ArraySlot(T[] list, int index)
    {
        this.array = list;
        this.index = index;
    }

    @Override
    public Slot<T> set(T t)
    {
        array[index] = t;
        return this;
    }

    @Override
    public T get()
    {
        return array[index];
    }
}
