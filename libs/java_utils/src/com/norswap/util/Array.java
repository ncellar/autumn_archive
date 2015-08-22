package com.norswap.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.function.Function;

/**
 * A dynamic array to serve as substitute to ArrayList.
 *
 * The idea to to be able to implement functions not implemented by ArrayList, such as {@link
 * #truncate}.
 *
 * But mostly, I did it for fun.
 *
 * Future plans:
 * - implement Deque
 */
public final class Array<T> implements List<T>, RandomAccess, Cloneable
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

    public Array(T... items)
    {
        this.array = items;
        this.next = items.length;
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

    public static <T> Array<T> fromItem(T item)
    {
        return new Array<>((T) item);
    }

    public static <T> Array<T> fromArray(T[] array)
    {
        return new Array<>((T[]) array);
    }

    public static <T> Array<T> fromUnsafe(Object[] array)
    {
        Array<T> out = new Array<>();
        out.array = array;
        out.next = array.length;
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isEmpty()
    {
        return next == 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int size()
    {
        return next;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERTIONS

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean add(T t)
    {
        if (next == array.length)
        {
            growCapacity(array.length + 1);
        }

        array[next++] = t;

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void add(int index, T element)
    {
        if (next == array.length)
        {
            growCapacity(array.length + 1);
        }

        System.arraycopy(array, index, array, index + 1, next - index - 1);
        array[index] = element;
        next++;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T get(int index)
    {
        return Caster.cast(array[index]);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T set(int index, T t)
    {
        @SuppressWarnings("unchecked")
        T element = (T) array[index];
        array[index] = t;
        return t;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * If {@code collection} is null, it will be treated as empty.
     */
    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        int size = next;
        if (collection != null)
        {
            collection.forEach(this::add);
        }
        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        int csize = c.size();

        if (array.length < next + csize)
        {
            growCapacity(next + csize);
        }

        System.arraycopy(array, index, array, index + csize, next - index - 1);

        int i = index;
        for (T t: c)
        {
            array[i++] = t;
        }

        return csize != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // NEW OPERATIONS

    // ---------------------------------------------------------------------------------------------

    public void growSize(int size)
    {
        if (array.length < size) {
            growCapacity(size);
        }

        next = size;
    }

    // ---------------------------------------------------------------------------------------------

    public void growCapacity(int capacity)
    {
        int size = array.length;

        if (size < capacity)
        {
            while (size < capacity)
            {
                size = (int) (size * GROWTH_FACTOR);
            }

            array = Arrays.copyOf(array, size);
        }
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

    public T popOr(T t)
    {
        return next == 0 ? t : pop();
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

    public T peekOr(T t)
    {
        return next == 0 ? t : peek();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // REMOVALS

    // ---------------------------------------------------------------------------------------------

    @Override
    public T remove(int index)
    {
        @SuppressWarnings("unchecked")
        T item = (T) array[index];
        System.arraycopy(array, index + 1, array, index, next - index - 1);
        --next;
        return item;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean remove(Object t)
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

    @Override
    public boolean removeAll(Collection<?> c)
    {
        int size = next;
        c.forEach(this::remove);
        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean retainAll(Collection<?> c)
    {
        int size = next;

        for (int i = 0; i < next; ++i)
        {
            if (!c.contains(array[i]))
            {
                remove(i--);
            }
        }

        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void clear()
    {
        truncate(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MISC

    // ---------------------------------------------------------------------------------------------

    @Override
    public int indexOf(Object t)
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

    @Override
    public int lastIndexOf(Object t)
    {
        for (int i = next - 1; i >= 0; --i)
        {
            if (t == null ? array[i] == null : t.equals(array[i]))
            {
                return i;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean contains(Object t)
    {
        return indexOf(Caster.cast(t)) >= 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object o: c)
        {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException("Array doesn't support sublist for now");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ITERATORS

    // ---------------------------------------------------------------------------------------------

    private class ArrayIterator implements ListIterator<T>
    {
        int index;
        int direction;

        @Override
        public boolean hasNext()
        {
            return index < next;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next()
        {
            direction = 1;
            return (T) array[index++];
        }

        @Override
        public boolean hasPrevious()
        {
            return index > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T previous()
        {
            direction = -1;
            return (T) array[--index];
        }

        @Override
        public int nextIndex()
        {
            return index;
        }

        @Override
        public int previousIndex()
        {
            return index - 1;
        }

        @Override
        public void remove()
        {
            if (direction > 0)
            {
                Array.this.remove(index - 1);
                --index;
            }
            else if (direction < 0)
            {
                Array.this.remove(index);
            }
            else {
                throw new IllegalStateException(
                    "Calling remove twice, or after add, or before calling next.");
            }

            direction = 0;
        }

        @Override
        public void set(T t)
        {
            if (direction > 0)
            {
                array[index - 1] = t;
            }
            else if (direction < 0)
            {
                array[index] = t;
            }
            else {
                throw new IllegalStateException(
                    "Calling set after remove or add, or before calling next.");
            }
        }

        @Override
        public void add(T t)
        {
            Array.this.add(index, t);
            ++index;
            direction = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private final class ReverseArrayIterator extends ArrayIterator
    {
        {
            index = next - 1;
        }

        @Override
        public boolean hasNext()
        {
            return super.hasPrevious();
        }

        @Override
        public T next()
        {
            return super.previous();
        }

        @Override
        public boolean hasPrevious()
        {
            return super.hasNext();
        }

        @Override
        public T previous()
        {
            return super.next();
        }

        @Override
        public int nextIndex()
        {
            return super.previousIndex();
        }

        @Override
        public int previousIndex()
        {
            return super.nextIndex();
        }

        @Override
        public void add(T t)
        {
            Array.this.add(index, t);
            // ++index; // remove this from superclass to satisfy method contract
            direction = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<T> iterator()
    {
        return new ArrayIterator();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ListIterator<T> listIterator()
    {
        return new ArrayIterator();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ListIterator<T> listIterator(int index)
    {
        ArrayIterator out = new ArrayIterator();
        out.index = index;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public Iterable<T> reverseIterable()
    {
        return this::reverseIterator;
    }

    // ---------------------------------------------------------------------------------------------

    public ListIterator<T> reverseIterator()
    {
        return new ReverseArrayIterator();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MAP METHODS

    // TODO offer a map view on this collection

    public boolean containsValue(Object value)
    {
        return contains(value);
    }

    public boolean containsKey(Object key)
    {
        return (int) key < next;
    }

    public T get(Object key)
    {
        return get((int) key);
    }

    public T put(Integer key, T value)
    {
        if (next <= key)
        {
            growSize(key + 1);
        }

        return set(key, value);
    }

    public Collection<T> values()
    {
        return this;
    }

    /* TODO

    @Override


    @Override
    public Set<Entry<Integer, T>> entrySet()
    {
        return null;
    }

    @Override
    public Set<Integer> keySet()
    {
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends T> m)
    {

    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TO ARRAY

    // ---------------------------------------------------------------------------------------------

    public T[] toArray(Function<Integer, T[]> supplier)
    {
        T[] out = supplier.apply(next);
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object[] toArray()
    {
        Object[] out = new Object[next];
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <U> U[] toArray(U[] out)
    {
        if (out.length < next)
        {
            out = (U[])
                java.lang.reflect.Array.newInstance(out.getClass().getComponentType(), next);
        }

        System.arraycopy(array, 0, out, 0, next);

        if (out.length != next)
        {
            out[next] = null;
        }

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
