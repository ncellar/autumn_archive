package com.norswap.autumn.parsing.state;

/**
 * Interface implemented by classes that hold custom state to be added to {@link ParseState}. You
 * should read the documentation of {@link ParseState} before going forward.
 * <p>
 * Each method in this interface is called from the method with the equivalent name in {@code
 * ParseState}. Each instance of this class acts as its own mini {@code ParseState} complete with
 * its own snapshot, parse changes and inputs objects (to be defined by the user).
 * <p>
 * It would be tricky to parameterize this interface in terms of the concrete classes used for the
 * snapshot, parse changes and inputs objects. Instead we use the marker interfaces {@link
 * Snapshot}, {@link Inputs} and {@link Result}, as well as the interface {@link CustomChanges} in
 * the method signatures. You will have to cast these interfaces to the concrete type in the method
 * that accept them as parameter.
 */
public interface CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    interface Snapshot {}
    interface Inputs {}
    interface Result {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void commit();

    void discard();

    CustomChanges extract();

    void merge(CustomChanges changes);

    Snapshot snapshot();

    void restore(Snapshot snapshot);

    void uncommit(Snapshot snapshot);

    Inputs inputs();

    Result result();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
