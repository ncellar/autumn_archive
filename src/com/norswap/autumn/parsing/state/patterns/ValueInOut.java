package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * This class implements the input/output parse state pattern. This is a pattern that combines the
 * characteristics of {@link ValueInput} and {@link ValueOutput}: it's a value can be read/written
 * by a parsing expression and its descendants (and consequently also by its ancestors).
 * <p>
 * This implementation assumes the state is held in an immutable value of type {@link T}.
 */
public class ValueInOut<T> implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private T contentUncommitted;
    private T contentCommitted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void set(T content)
    {
        this.contentUncommitted = content;
    }

    // ---------------------------------------------------------------------------------------------

    public T get()
    {
        return contentUncommitted;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object inputs(ParseState state)
    {
        return contentUncommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void load(Object inputs)
    {
        contentCommitted = (T) inputs;
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object snapshot(ParseState state)
    {
        return contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(Object snapshot, ParseState state)
    {
        contentCommitted = (T) snapshot;
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(Object snapshot, ParseState state)
    {
        contentCommitted = (T) snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        contentCommitted = contentUncommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return contentCommitted != contentUncommitted
            ? contentUncommitted
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object changes, ParseState state)
    {
        if (changes != null) {
            contentUncommitted = (T) changes;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
