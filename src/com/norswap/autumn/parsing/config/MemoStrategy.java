package com.norswap.autumn.parsing.config;

import java.util.function.Supplier;

/**
 * A memoization strategy is a supplier of memoization handlers.
 * <p>
 * Memoization handlers have state, using a strategy instead allows {@link ParserConfiguration} to
 * be reusable.
 */
@FunctionalInterface
public interface MemoStrategy extends Supplier<MemoHandler>
{
}
