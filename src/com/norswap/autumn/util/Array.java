package com.norswap.autumn.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * A dynamic array to serve as minimal substitute to ArrayList.
 *
 * The idea to to be able to implement functions not implemented by ArrayList, such as {@link
 * #truncate}.
 */
public final class Array<T> implements Iterable<T>, Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int DEFAULT_SIZE = 4;
    public static double GROWTH_FACTOR = 2.0f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Object[] array;
    private int next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Array(T[] array, int next)
    {
        this.array = array;
        this.next = next;
    }

    public Array(T[] array)
    {
        this.array = array;
        this.next = array.length;
    }

    public Array(int size)
    {
        array = new Object[size];
    }

    public Array()
    {
        array = new Object[DEFAULT_SIZE];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isEmpty()
    {
        return next == 0;
    }

    // ---------------------------------------------------------------------------------------------

    public int size()
    {
        return next;
    }

    // ---------------------------------------------------------------------------------------------

    public void add(T t)
    {
        if (next == array.length)
        {
            grow(array.length + 1);
        }

        array[next++] = t;
    }

    // ---------------------------------------------------------------------------------------------

    public T get(int index)
    {
        return Caster.cast(array[index]);
    }

    // ---------------------------------------------------------------------------------------------

    public void set(int index, T t)
    {
        array[index] = t;
    }

    // ---------------------------------------------------------------------------------------------

    public void grow(int capacity)
    {
        int size = array.length;

        while (size < capacity)
        {
            size = (int) (size * GROWTH_FACTOR);
        }

        array = Arrays.copyOf(array, size);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        for (int i = size; i < next; ++i)
        {
            array[i] = null;
        }

        if (size < next)
        {
            next = size;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void clear()
    {
        truncate(0);
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Collection<? extends T> collection)
    {
        collection.forEach(this::add);
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Array<? extends T> array)
    {
        array.forEach(this::add);
    }

    // ---------------------------------------------------------------------------------------------

    public void push(T t)
    {
        add(t);
    }

    // ---------------------------------------------------------------------------------------------

    public T pop()
    {
        return Caster.cast(array[--next]);
    }

    // ---------------------------------------------------------------------------------------------

    public T popOrNull()
    {
        return next == 0 ? null : pop();
    }

    // ---------------------------------------------------------------------------------------------

    public T peek()
    {
        return Caster.cast(array[next - 1]);
    }

    // ---------------------------------------------------------------------------------------------

    public T peekOrNull()
    {
        return next == 0 ? null : peek();
    }

    // ---------------------------------------------------------------------------------------------

    public void remove(int index)
    {
        System.arraycopy(array, index + 1, array, index, next - index - 1);
        --next;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean remove(T t)
    {
        for (int i = 0; i < next; ++i)
        {
            if (array[i].equals(t))
            {
                remove(i);
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean removeFromEnd(T t)
    {
        for (int i = next - 1; i >= 0; --i)
        {
            if (array[i].equals(t))
            {
                remove(i);
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    public int indexOf(T t)
    {
        for (int i = 0; i < next; ++i)
        {
            if (t == null ? array[i] == null : t.equals(array[i]))
            {
                return i;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean contains(T t)
    {
        return indexOf(t) >= 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private int index;

            @Override
            public boolean hasNext()
            {
                return index < next;
            }

            @Override
            public T next()
            {
                return Caster.cast(array[index++]);
            }
        };
    }

    // ---------------------------------------------------------------------------------------------

    public Iterable<T> reverseIterable()
    {
        return this::reverseIterator;
    }

    // ---------------------------------------------------------------------------------------------

    public Iterator<T> reverseIterator()
    {
        return new Iterator<T>()
        {
            private int index = next - 1;

            @Override
            public boolean hasNext()
            {
                return index >= 0;
            }

            @Override
            public T next()
            {
                return Caster.cast(array[index--]);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public T[] toArray(Function<Integer, T[]> supplier)
    {
        T[] out = supplier.apply(next);
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("[");

        for (int i = 0; i < next; ++i)
        {
            b.append(array[i]);
            b.append(", ");
        }

        if (next > 0)
        {
            b.setLength(b.length() - 2);
        }

        b.append("]");
        return b.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @SuppressWarnings({"unchecked", "CloneDoesntCallSuperClone"})
    public Array<T> clone()
    {
        return new Array(array.clone(), next);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        // part auto-generated, part lifted from Arrays.equals

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Array<?> array1 = (Array<?>) o;

        if (next != array1.next)
            return false;

        for (int i = 0; i < next; ++i)
        {
            Object o1 = array[i];
            Object o2 = array1.array[i];

            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        // lifted from Arrays.hashCode

        int result = 1;

        for (T t: this)
        {
            result = 31 * result + (t == null ? 0 : t.hashCode());
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
