package com.norswap.util;

import java.lang.reflect.Array;
import java.util.function.Function;

/**
 * This interface indicates that the object is capable of making a deep copy of itself
 * (or in the case of containers, that it will perform a best effort towards this goal, falling
 * back on {@link #clone} or reference copy if necessary).
 *
 * The object for the copy should be obtained by calling {@link Object#clone}, {@link
 * DeepCopy#clone} or {@code super .deepCopy()} in case of a subclass. Ultimately the returned
 * object *must* have been obtained by a call to {@link Object#clone} (which is what {@link
 * DeepCopy#clone} calls through reflection).
 *
 * It is expected that objects that implement this interface will also implement {@link Cloneable}
 * properly, although that is not an absolute requirement.
 *
 * Subclasses that would like not to support this operation can throw {@link DeepCopy.NotSupported}.
 */
public interface DeepCopy extends Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    static <T extends DeepCopy> T[] of(T[] array, Function<Integer, T[]> supplier)
    {
        T[] copy = supplier.apply(array.length);

        for (int i = 0; i < array.length; ++i)
        {
            copy[i] = (T) array[i].deepCopy();
        }

        return copy;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static <T extends DeepCopy> T[] of(T[] array)
    {
        return of(array,
            size -> (T[]) Array.newInstance(array.getClass().getComponentType(), array.length));
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static <T extends Cloneable> T clone(T t)
    {
        try {
            return (T) t.getClass().getMethod("clone").invoke(t);
        }
        catch (ReflectiveOperationException e)
        {
            throw new Error(); // never happens
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sometimes, it is useful to clone array one level-deep (i.e. clone the array itself than
     * replace each of its elements by their clones).
     *
     * This is different from a deep copy: a deep copy will continue to recurse over the members of
     * the array elements.
     */
    static <T extends Cloneable> T[] deepClone(T[] array)
    {
        T[] out = array.clone();

        for (int i = 0; i < array.length; ++i)
        {
            out[i] = clone(array[i]);
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class NotSupported extends RuntimeException {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    DeepCopy deepCopy();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
