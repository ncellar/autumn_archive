package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.extensions.Extension;

/**
 * Interface implemented by classes that hold custom state to be added to {@link ParseState}. You
 * should read the documentation of {@link ParseState} before going forward.
 * <p>
 * Each method in this interface is called from the method with the equivalent name in {@code
 * ParseState}. Each instance of this class acts as its own mini {@code ParseState} complete with
 * its own snapshot, parse changes and inputs objects. The user can use any arbitrary object he
 * wants for these (casting will be required).
 */
public interface CustomState

//* It would be tricky to parameterize this interface in terms of the concrete classes used for the
//* snapshot, parse changes and inputs objects. Instead we use the marker interfaces {@link
//* Snapshot}, {@link Inputs} and {@link Result}, as well as the interface {@link CustomChanges} in
//* the method signatures. You will have to cast these interfaces to the concrete type in the method
//* that accept them as parameter.
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    default Object inputs(ParseState state) { return null; }

    default Object snapshot(ParseState state) { return null; }

    default void restore(Object snapshot, ParseState state) {}

    default void uncommit(Object snapshot, ParseState state) {}

    default void discard(ParseState state) {}

    default void commit(ParseState state) {}

    default Object extract(ParseState state) { return null; }

    default void merge(Object changes, ParseState state) {}

    default void load(Object inputs) {}

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
