package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.ReferenceResolver;

import java.util.function.Supplier;

public final class ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The configuration a Parser will use by default if a configuration is not set explicitly.
     */
    public static final ParserConfiguration DEFAULT = new ParserConfiguration();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Supplier<ErrorHandler> errorHandler = DefaultErrorHandler::new;

    public Supplier<ParsingExpression> whitespace = () ->
        ReferenceResolver.resolve(Whitespace.whitespace.deepCopy());

    public Supplier<MemoizationStrategy> memoizationStrategy = DefaultMemoizationStrategy::new;

    public boolean processLeadingWhitespace = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
