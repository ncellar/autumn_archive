package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * This class implements the pure input parse state pattern. This state is set by a parsing
 * expression with the intent to modify the behaviour of some of its descendants.
 * <p>
 * This implementation assumes the state is held in an immutable value of type {@link T}.
 * <p>
 * The pattern is very simple: state changes are only visible to the descendants of the node that
 * performs the change. Here's some equivalent pseudo-code:
 * <p>
 * <pre>{@code
 * T oldMyState = state.myState; // snapshot
 * operand.parse(parser, state);
 * ...
 * state.myState = oldMyState; // restore or uncommit
 * ...
 * }
 * </pre>
 * But there is also {@link #inputs} to consider!
 */
public class ValueInput<T> implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public T content;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object inputs(ParseState state)
    {
        return content;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void load(Object inputs)
    {
        content = (T) inputs;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object snapshot(ParseState state)
    {
        return content;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(Object snapshot, ParseState state)
    {
        content = (T) snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(Object snapshot, ParseState state)
    {
        content = (T) snapshot;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
