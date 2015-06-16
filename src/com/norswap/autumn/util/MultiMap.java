package com.norswap.autumn.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A map that maps a key to a set of values.
 */
public class MultiMap<K, V> extends HashMap<K, Set<V>>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Set<V> get(Object key)
    {
        Set<V> out = super.get(key);
        return out == null ? Collections.emptySet() : out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Set<V> remove(Object key)
    {
        Set<V> out = super.remove(key);
        return out == null ? Collections.emptySet() : out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Set<V> add(K key, V value)
    {
        Set<V> set = computeIfAbsent(key, k -> new HashSet<V>());
        set.add(value);
        return set;
    }

    // ---------------------------------------------------------------------------------------------

    public Set<V> addAll(K key, V[] values)
    {
        Set<V> set = computeIfAbsent(key, k -> new HashSet<V>());

        for (V v: values) {
            set.add(v);
        }

        return set;
    }

    // ---------------------------------------------------------------------------------------------

    public Set<V> addAll(K key, Iterable<V> values)
    {
        Set<V> set = computeIfAbsent(key, k -> new HashSet<V>());

        for (V v: values) {
            set.add(v);
        }

        return set;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
