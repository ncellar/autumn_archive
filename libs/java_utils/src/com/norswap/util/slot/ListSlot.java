package com.norswap.util.slot;

import java.util.List;

/**
 * A slot corresponding to a list position.
 */
public final class ListSlot<T> implements Slot<T>
{
    public final List<T> list;
    public final int index;

    public ListSlot(List<T> list, int index)
    {
        this.list = list;
        this.index = index;
    }

    @Override
    public Slot<T> set(T t)
    {
        list.set(index, t);
        return this;
    }

    @Override
    public T get()
    {
        return list.get(index);
    }
}
