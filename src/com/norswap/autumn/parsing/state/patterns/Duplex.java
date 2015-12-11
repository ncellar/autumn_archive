package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * A state where the uncommitted state is held in the object, and the committed state is held in its
 * {@link #committed} field. A subclass must define {@link #copy} to make copies of the object,
 * excepted the {@link #committed} field (to produce snapshots), and {@link #copy(Duplex)} to
 * transfer to content (again, excepted the {@link #committed} field) between committed and
 * uncommitted state.
 * <p>
 * Note that this class does not define {@link #inputs}, {@link #load}, {@link #extract} and {@link
 * #merge}. Don't forget to implement them if required (most likely for load and extract).
 * <p>
 * Beware of infinite recursion when calling the constructor of this class! Don't do {@code
 * super(new SubDuplex());}.
 */
public abstract class Duplex<T extends Duplex<T>> implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final T committed;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Duplex(T committed)
    {
        this.committed = committed;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract T copy();

    protected abstract void copy(T other);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object snapshot(ParseState state)
    {
        return committed.copy();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(Object snapshot, ParseState state)
    {
        @SuppressWarnings("unchecked")
        T other = (T) snapshot;
        committed.copy(other);
        this.copy(other);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(Object snapshot, ParseState state)
    {
        @SuppressWarnings("unchecked")
        T other = (T) snapshot;
        committed.copy(other);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        this.copy(committed);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        @SuppressWarnings("unchecked")
        T self = (T) this;
        committed.copy(self);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
