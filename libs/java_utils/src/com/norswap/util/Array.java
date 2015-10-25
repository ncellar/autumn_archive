package com.norswap.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * An enriched version of {@link java.util.ArrayList}.
 * <p>
 * TODO
 * <ul>
 *     <li>Robustify collection & list methods to accept null a maximum of the time?</li>
 *     <li>List "indexed addAll" variants.</li>
 *     <li>Implement deepcopy</li>
 *     <li>Extract kernel read/write interfaces</li>
 *     <li>Read only subset</li>
 *     <li>Implement sublist</li>
 *     <li>Simplify some JArray overloads using a method to retrieve the store + strong guarantees?</li>
 *     <li>Methods to work with primitive arrays?</li>
 * </ul>
 */
public final class Array<T> implements List<T>, Queue<T>, RandomAccess, Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Default array capacity. Users can change this property (not thread-safe!) although it is not
     * recommended. Must be > 0.
     */
    public static int DEFAULT_CAPACITY = 4;

    /**
     * The growth factor for arrays. Users can change this property (not thread-safe!) although it
     * is not recommended. Must be > 1.
     */
    public static double GROWTH_FACTOR = 2.0f;

    /**
     * The unique instance returned by {@link #empty}. Immutable.
     */
    private static final Array<Object> EMPTY = new Array<>(0);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Object[] array;
    private int next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new array using the given array as backing store. The first {@code size} elements
     * of the array are considered to be part of the array.
     */
    public Array(T[] array, int size)
    {
        assert size <= array.length;

        this.array = array;
        this.next = size;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array containing the given items.
     */
    @SafeVarargs
    public Array(T... items)
    {
        this.array = items;
        this.next = items.length;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array with the given capacity.
     */
    public Array(int capacity)
    {
        array = new Object[capacity];
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array with the default capacity.
     */
    public Array()
    {
        assert DEFAULT_CAPACITY > 0;

        array = new Object[DEFAULT_CAPACITY];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FACTORY METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an immutable empty array. The same instance is used each time and dynamically
     * cast to the required type.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> empty()
    {
        return (Array<T>) EMPTY;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array containing a single item. Useful because passing a single array argument to
     * {@link #Array(Object[])} is ambiguous; or to create an array containing a single integer.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> fromItem(T item)
    {
        return new Array<>((T) item);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array containing the items contained within the array. Useful because passing a
     * single array argument to {@link #Array(Object[])} is ambiguous.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> fromArray(T[] array)
    {
        return new Array<>((T[]) array);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array containing all the items iterated over by the passed iterable.
     */
    public static <T> Array<T> from(Iterable<? extends T> iterable)
    {
        Array<T> out = new Array<>();
        out.addAll(iterable);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Equivalent to {@link #Array(Object[])}, but allows using an instance of {@code Object[]}
     * instead of {@code T[]} as backing store.
     */
    public static <T> Array<T> fromUnsafe(Object[] array)
    {
        return fromUnsafe(array, array.length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Equivalent to {@link #Array(Object[], int)}, but allows using an instance of {@code Object[]}
     * instead of {@code T[]} as backing store.
     */
    public static <T> Array<T> fromUnsafe(Object[] array, int size)
    {
        assert size <= array.length;

        Array<T> out = new Array<>();
        out.array = array;
        out.next = size;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array of size n where each element is initialized to the given item.
     */
    public static <T> Array<T> fromItem(int n, T item)
    {
        Array<T> out = new Array<>(n);
        out.ensureSize(n);
        out.fill(item);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array of size n where each element is initialized to null.
     */
    public static <T> Array<T> ofSize(int n)
    {
        Array<T> out = new Array<>(n);
        out.ensureSize(n);
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // STATIC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an array containing a copy if the first n items of the given java array.
     */
    public static <T> Array<T> copyOf(T[] array, int n)
    {
        assert n <= array.length;

        Array<T> out = Array.ofSize(n);
        out.copy(array, 0, 0, n);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns an array containing a copy fo the [from, to[ range of the given java array.
     */
    public static <T> Array<T> copyOf(T[] array, int from, int to)
    {
        Array<T> out = Array.ofSize(to - from);
        out.copy(array, from, to, to - from);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return an array which is a concatenation of all the given arrays.
     */
    @SafeVarargs
    public static <T> Array<T> concat(Array<? extends T> ...arrays)
    {
        Array<T> out = new Array<>();

        for (Array<? extends T> array: arrays)
        {
            out.addAll(array);
        }

        return out;
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * Maps the function f over the given java array and return an array of the results.
     */
    public static <T, U> Array<T> map(U[] array, Function<U, T> f)
    {
        Array<T> out = new Array<>(array.length);

        for (U u: array)
        {
            out.add(f.apply(u));
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Maps the function f over the given iterable and return an array of the results.
     */
    public static <T, U> Array<T> map(Iterable<U> iterable, Function<U, T> f)
    {
        Array<T> out = new Array<>();

        for (U u: iterable)
        {
            out.add(f.apply(u));
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all pairs of elements (src1[i], src2[i]) for all {@code 0 <= i <
     * min(src1.size(), src2.size())}, placing the results in the corresponding slot of {@code dst},
     * which is then returned.
     */
    public static <T, U, R> Array<R>
    bimap(Array<T> src1, Array<U> src2, Array<R> dst, BiFunction<? super T, ? super U, ? extends R> f)
    {
        int min = Math.min(src1.size(), src2.size());

        assert dst.size() >= min;

        for (int i = 0; i < min; ++i)
        {
            dst.set(i, f.apply(src1.get(i), src2.get(i)));
        }

        return dst;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all pairs of elements (src1[i], src2[i]) for all {@code 0 <= i <
     * min(src1.size(), src2.size())}, placing the results in the corresponding slot of a new array,
     * which is then returned.
     */
    public static <T, U, R> Array<R>
    bimap(Array<T> src1, Array<U> src2, BiFunction<? super T, ? super U, ? extends R> f)
    {
        return bimap(src1, src2, new Array<>(Math.min(src1.size(), src2.size())), f);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COPY METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a copy of the [from, to[ range of this array.
     */
    public Array<T> copyOfRange(int from, int to)
    {
        assert to >= from;

        Array<T> out = Array.ofSize(to - from);
        out.copy(this, from, 0, to - from);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [0, n[ range of this array.
     */
    public Array<T> copyOfPrefix(int n)
    {
        assert n >= 0 && n <= next;

        return copyOfRange(0, n);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [from, size[ range of this array.
     */
    public Array<T> copyFromIndex(int from)
    {
        assert from >= 0 && from <= next;

        return copyOfRange(from, next);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [size - n, size[ range of this array.
     */
    public Array<T> copyOfSuffix(int n)
    {
        assert n >= 0 && n <= next;

        return copyOfRange(next - n, next);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of {@code src} starting at position {@code srcPos} to the
     * position {@code dstPos} of this array. The size of this array should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copy(T[] src, int srcPos, int dstPos, int length)
    {
        assert src.length >= srcPos + length;
        assert next >= dstPos + length;

        System.arraycopy(src, srcPos, array, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of {@code src} starting at position {@code srcPos} to the
     * position {@code dstPos} of this array. The size of this array should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copy(Array<? extends T> src, int srcPos, int dstPos, int length)
    {
        assert src.next >= srcPos + length;
        assert next >= dstPos + length;

        System.arraycopy(src.array, srcPos, array, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of this starting at position {@code srcPos} to the position
     * {@code dstPos} of {@code dst}.
     */
    public void copyTo(T[] dst, int srcPos, int dstPos, int length)
    {
        assert dst.length >= dstPos + length;
        assert next >= srcPos + length;

        System.arraycopy(array, srcPos, dst, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of this starting at position {@code srcPos} to the position
     * {@code dstPos} of {@code dst}. The size of {@code dst} should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copyTo(Array<? super T> dst, int srcPos, int dstPos, int length)
    {
        dst.copy(this, srcPos, dstPos, length);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COLLECTION IMPLEMENTATION
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int size()
    {
        return next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean isEmpty()
    {
        return next == 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object t)
    {
        return indexOf(t) >= 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<T> iterator()
    {
        return new ArrayIterator();
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
            out = JArrays.newInstance(out, next);
        }

        System.arraycopy(array, 0, out, 0, next);

        if (out.length != next)
        {
            out[next] = null;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean add(T t)
    {
        if (next == array.length)
        {
            ensureCapacity(array.length + 1);
        }

        array[next++] = t;

        return true;
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

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return containsAll((Iterable<?>) c);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * If {@code collection} is null, it will be treated as empty.
     */
    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        return addAll((Iterable<? extends T>) c);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return removeAll((Iterable<?>) c);
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
    // COLLECTION METHODS VARIANTS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Like {@link #containsAll(Collection)}, but for iterables.
     */
    public boolean containsAll(Iterable<?> iterable)
    {
        for (Object t: iterable)
        {
            if (!contains(t)) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #removeAll(Collection)}, but for iterables.
     */
    public boolean removeAll(Iterable<?> iterable)
    {
        int size = next;
        iterable.forEach(this::remove);
        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #retainAll(Collection)}, but for iterables.
     * <p>
     * Note that the collection overload will be faster if the collection support
     * sub-linear-time {@link Collection#contains}.
     */
    public boolean retainAll(Iterable<?> iterable)
    {
        int size = next;

        for (Object o: iterable)
        {
            int i = indexOf(o);
            if (i >= 0) {
                remove(i);
            }
        }

        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #addAll(Collection)}, but optimized for arrays.
     * <p>
     * If {@code iterable} is null, it will be treated as empty.
     */
    public boolean addAll(Array<? extends T> array)
    {
        int dstPos = next;
        int size = array == null ? 0 : array.size();

        if (size == 0)
        {
            return false;
        }

        ensureSize(next + size);
        copy(array, 0, dstPos, size);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #addAll(Collection)}, but for iterables.
     * <p>
     * If {@code iterable} is null, it will be treated as empty.
     */
    public boolean addAll(Iterable<? extends T> iterable)
    {
        int size = next;

        if (iterable != null)
        {
            iterable.forEach(this::add);
        }

        return size != next;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LIST IMPLEMENTATION
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index)
    {
        return (T) array[index];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T set(int index, T t)
    {
        @SuppressWarnings("unchecked")
        T element = (T) array[index];
        array[index] = t;
        return element;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void add(int index, T element)
    {
        if (next == array.length)
        {
            ensureCapacity(array.length + 1);
        }

        System.arraycopy(array, index, array, index + 1, next - index - 1);
        array[index] = element;
        next++;
    }

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

    @Override
    public Array<T> subList(int from, int to)
    {
        throw new UnsupportedOperationException("Array doesn't support sublist for now");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * If {@code collection} is null, it will be treated as empty.
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        if (c == null || c.isEmpty())
        {
            return false;
        }

        int csize = c.size();

        if (array.length < next + csize)
        {
            ensureCapacity(next + csize);
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
    // QUEUE IMPLEMENTATION
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean offer(T t)
    {
        return add(t);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public T remove()
    {
        if (next == 0)
        {
            throw new NoSuchElementException();
        }

        T out = (T) array[next - 1];
        array[--next] = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T poll()
    {
        return next == 0 ? null : remove();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public T peek()
    {
        return next == 0 ? null : (T) array[next - 1];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public T element()
    {
        if (next == 0)
        {
            throw new NoSuchElementException();
        }

        return (T) array[next - 1];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // QUEUE-RELATED ALIASES / METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Alias for {@link #offer}.
     */
    public void push(T t)
    {
        add(t);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Alias for {@link #remove}.
     */
    public T pop()
    {
        return remove();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #poll} but returns {@code t} whenever it would return null.
     */
    public T popOr(T t)
    {
        return next == 0 ? t : pop();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #peek} but returns {@code t} whenever it would return null.
     */
    public T peekOr(T t)
    {
        return next == 0 ? t : peek();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // NEW OPERATIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return the size of the java array currently backing this Array object. The Array can grow to
     * include this many elements before a resize operation is necessary.
     */
    public int capacity()
    {
        return array.length;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the java array currently backing this Array object.
     */
    public Object[] backingArray()
    {
        return array;
    }

    // ---------------------------------------------------------------------------------------------

    public void ensureSize(int size)
    {
        if (array.length < size) {
            ensureCapacity(size);
        }

        next = size;
    }

    // ---------------------------------------------------------------------------------------------

    public void ensureCapacity(int capacity)
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

    @SuppressWarnings("unchecked")
    public T popush(T t)
    {
        T out = (T) array[next - 1];
        array[next - 1] = t;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public T first()
    {
        return (T) array[0];
    }

    // ---------------------------------------------------------------------------------------------

    public void setFirst(T t)
    {
        array[0] = t;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public T last()
    {
        return (T) array[next - 1];
    }

    // ---------------------------------------------------------------------------------------------

    public void setLast(T t)
    {
        array[next - 1] = t;
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

    public void fill(T value)
    {
        Arrays.fill(array, 0, next, value);
    }

    // ---------------------------------------------------------------------------------------------

    public void fill(T value, int from, int to)
    {
        assert next <= to;

        Arrays.fill(array, from, to, value);
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

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a Java array containing the same elements as this array. The returned array
     * will be produced by the given supplier (in general something like {@code Object[]::new}).
     */
    public T[] toArray(IntFunction<T[]> supplier)
    {
        T[] out = supplier.apply(next);
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public T put(Integer key, T value)
    {
        if (next <= key)
        {
            ensureSize(key + 1);
        }

        return set(key, value);
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void transform(Function<T, T> transform)
    {
        for (int i = 0; i < next; ++i)
        {
            array[i] = transform.apply((T) array[i]);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void transformNonNull(Function<T, T> transform)
    {
        for (int i = 0; i < next; ++i)
        {
            if (array[i] != null)
            {
                array[i] = transform.apply((T) array[i]);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void cloneElements()
    {
        Method clone = null;

        try {
            for (int i = 0; i < next; ++i)
            {
                T elem = (T) array[i];

                if (elem != null)
                {
                    assert elem instanceof Cloneable;

                    if (clone == null)
                    {
                        clone = elem.getClass().getMethod("clone");
                        clone.setAccessible(true);
                    }

                    array[i] = clone.invoke(elem);
                }
            }
        }
        catch (ReflectiveOperationException e)
        {
            throw new Error(e); // shouldn't happen
        }
    }

    // ---------------------------------------------------------------------------------------------

    public Array<T> deepClone()
    {
        Array<T> out = clone();
        out.cloneElements();
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <U> Array<U> map(Function<? super T, U> f)
    {
        Array<U> out = new Array<>(next);

        for (int i = 0; i < next; ++i)
        {
            out.add(f.apply((T) array[i]));
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public Object[] mapToArray(Function<? super T, ?> f)
    {
        return mapToArray(f, new Object[next]);
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <U> U[] mapToArray(Function<? super T, ? extends U> f, U[] out)
    {
        assert array.length >= next;

        for (int i = 0; i < next; ++i)
        {
            out[i] = f.apply((T) array[i]);
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public <U> U[] mapToArray(Function<? super T, ? extends U> f, IntFunction<U[]> generator)
    {
        return mapToArray(f, generator.apply(next));
    }

    // ---------------------------------------------------------------------------------------------

    public Array<T> filter(Predicate<? super T> p)
    {
        Array<T> out = new Array<>();

        for (int i = 0; i < next; ++i)
        {
            @SuppressWarnings("unchecked")
            T elem = (T) array[i];

            if (p.test(elem))
            {
                out.add(elem);
            }
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public T first(Predicate<? super T> p)
    {
        for (int i = 0; i < next; ++i)
        {
            @SuppressWarnings("unchecked")
            T elem = (T) array[i];

            if (p.test(elem))
            {
                return elem;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public T last(Predicate<? super T> p)
    {
        for (int i = next - 1 ; i >= next; --i)
        {
            @SuppressWarnings("unchecked")
            T elem = (T) array[i];

            if (p.test(elem))
            {
                return elem;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #contains}, but uses identity comparison (==) instead of {@link #equals}.
     */
    public boolean containsID(T t)
    {
        for (int i = 0; i < next; i++)
        {
            if (array[i] == t) return true;
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #forEach(Consumer)} but the action takes the index as addition argument.
     */
    @SuppressWarnings("unchecked")
    public void forEach(BiConsumer<? super T, Integer> action)
    {
        for (int i = 0; i < next; ++i)
        {
            action.accept((T) array[i], i);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Swap the backing java array by one whose size is the number of elements held in the
     * collection.
     */
    public void compact()
    {
        array = toArray();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OBJECT OVERRIDES
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

    // ---------------------------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings({"unchecked", "CloneDoesntCallSuperClone"})
    public Array<T> clone()
    {
        return new Array(array.clone(), next);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OBJECT-RELATED METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a clone of this array, where the size of the backing java array is the number of
     * items in the collection.
     */
    public Array<T> compactClone()
    {
        Array<T> out = Array.fromUnsafe(new Object[next], next);
        System.arraycopy(array, 0, out.array, 0, next);
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ITERATOR
    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ITERATOR
    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
