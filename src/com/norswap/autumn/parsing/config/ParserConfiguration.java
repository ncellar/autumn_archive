package com.norswap.autumn.parsing.config;

import java.util.function.Supplier;

/**
 * [Immutable] The parser configuration allows the user to configure operational details of the
 * parse, such as how errors and memoization are handled.
 */
public final class ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The default configuration uses {@link DefaultErrorHandler} and {@link DefaultMemoHandler}.
     */
    public static final ParserConfiguration DEFAULT
        = new ParserConfiguration(DefaultErrorHandler::new, DefaultMemoHandler::new);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Supplier<ErrorHandler> errorHandler;

    public final Supplier<MemoHandler> memoizationStrategy;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParserConfiguration(Supplier<ErrorHandler> errorHandler, Supplier<MemoHandler> memoizationStrategy)
    {
        this.errorHandler = errorHandler;
        this.memoizationStrategy = memoizationStrategy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
