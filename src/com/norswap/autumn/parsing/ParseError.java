package com.norswap.autumn.parsing;

/**
 * Encapsulate error information about the parse.
 *
 * A parse error may contain information about multiple errors. It may also refer to a successful
 * parse (since errors are par for the course when parsing, they just cause backtracking).
 *
 * Implementations of this class can supply a wide variety of information, but the primary goal
 * should be to help the programmer find and fix errors by supplying an helpful error message.
 */
public interface ParseError
{
    /**
     * A message fit to be displayed to the user, detailing (some of) the error(s) that occured
     * during the parse, and optionally hints about how to fix or diagnose them.
     */
    String message();
}
