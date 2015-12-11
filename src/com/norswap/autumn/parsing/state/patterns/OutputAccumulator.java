package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 * This class implement the output accumulator pattern. It maintains a list of items to which
 * parsing expressions can add. Whenever a parsing expression fails, all the items added by the its
 * sub-expressions are removed.
 * <p>
 * This is notably the pattern used to build up the list of children of a parse tree node.
 */
public class OutputAccumulator<T> implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Array<T> items = new Array<>();
    private int count;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void add(T t)
    {
        items.add(t);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object snapshot(ParseState state)
    {
        return count;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(Object snapshot, ParseState state)
    {
        count = (int) snapshot;
        items.truncate(count);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(Object snapshot, ParseState state)
    {
        count = (int) snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        items.truncate(count);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        count = items.size();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return items.copyFromIndex(count);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object changes, ParseState state)
    {
        items.addAll((Array<T>) changes);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
