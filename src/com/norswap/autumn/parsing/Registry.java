package com.norswap.autumn.parsing;

import com.norswap.autumn.util.FlagFactory;
import com.norswap.autumn.util.HandleFactory;

/**
 * The registry manages flags and handle spaces for the parser.
 *
 * It registers the standard flags and handles; and allows the user to register his own flags
 * and handles via its factories.
 *
 * This is not synchronized, so take care if concurrent access is required.
 */
public final class Registry
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final HandleFactory ParserHandleFactory = new HandleFactory();

    public static final HandleFactory ParsingExpressionHandleFactory = new HandleFactory();

    public static final FlagFactory ParsingExpressionFlagsFactory = new FlagFactory();

    public static final FlagFactory ParseStateFlagsFactory = new FlagFactory();

    public static final HandleFactory ParseStateHandleFactory = new HandleFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSER HANDLES (PH)

    /**
     * Fetches the recursion depth (number of {@code Trace} expression traversed).
     */
    public static final int PH_DEPTH
        = ParserHandleFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING EXPRESSION HANDLES (PEH)

    /**
     * Fetches the name of the expression, if it has one.
     */
    public static final int PEH_NAME
        = ParsingExpressionHandleFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING EXPRESSION FLAGS (PEF)

    /**
     * Indicates that errors occuring while matching this expression should be recorded for the
     * sake of error reporting. Note that some expressions (such as captures or memos)
     * voluntarily bypass error recording.
     */
    public static final int PEF_ERROR_RECORDING
        = ParsingExpressionFlagsFactory.next();

    /**
     * (For {@code Capture} only) Indicates the matched text should be captured.
     */
    public static final int PEF_CAPTURE_TEXT
        = ParsingExpressionFlagsFactory.next();

    /**
     * (For {@code Capture} only) Indicates that tree nodes resulting from this capture should be
     * grouped together under a tree node sporting the capture name.
     */
    public static final int PEF_CAPTURE_GROUPED
        = ParsingExpressionFlagsFactory.next();

    /**
     * Indicates that all recursions in the sub-expressions of this expression have been resolved.
     */
    public static final int PEF_RESOLVED
        = ParsingExpressionFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE STATE FLAGS (PSF)

    /**
     * Indicates that we shouldn't memoize any of the sub-expressions of the expression
     * associated with this parse state.
     */
    public static final int PSF_DONT_MEMOIZE
        = ParseStateFlagsFactory.next();

    /**
     * Indicates that we shouldn't memoize any expression at the current position.
     */
    public static final int PSF_DONT_MEMOIZE_POSITION
        = ParseStateFlagsFactory.next();

    /**
     * Indicates that we shouldn't record errors when sub-expressions of the expression
     * associated with this parse state fail to parse.
     */
    public static final int PSF_DONT_RECORD_ERRORS
        = ParseStateFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE STATE HANDLES (PSH)

    /**
     * Fetches the stack trace that shows parsing expressions we are currently traversing.
     */
    public static final int PSH_STACK_TRACE
        = ParseStateHandleFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
