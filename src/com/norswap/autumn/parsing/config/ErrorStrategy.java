package com.norswap.autumn.parsing.config;

import java.util.function.Supplier;

/**
 * An error strategy is a supplier of error handlers.
 * <p>
 * Error handlers have state, using a strategy instead allows {@link ParserConfiguration} to be
 * reusable.
 */
@FunctionalInterface
public interface ErrorStrategy extends Supplier<ErrorHandler>
{
}
