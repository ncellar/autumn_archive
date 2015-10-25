package com.norswap.autumn.parsing.state.errors;

import java.util.Collection;

/**
 * Encapsulate error information about the parse.
 * <p>
 * A parse error may contain information about multiple errors. It may also refer to a successful
 * parse (since errors are par for the course when parsing, they just cause backtracking).
 * <p>
 * Implementations of this class can supply a wide variety of information, but the primary goal
 * should be to help the programmer find and fix errors by supplying an helpful error message.
 */
public interface ErrorReport
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A message fit to be displayed to the user, detailing (some of) the error(s) that occured
     * during the parse, and optionally hints about how to fix or diagnose them.
     */
    String message();

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a list of error locations corresponding to the errors that the error state
     * retained and chose to expose to the user. See {@link ErrorLocation}.
     */
    Collection<ErrorLocation> locations();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
