package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.errors.DefaultErrorState;
import com.norswap.autumn.parsing.errors.ErrorState;

import java.util.function.Supplier;

/**
 * [Immutable] The parser configuration allows the user to configure operational details of the
 * parse, such as how errors and memoization are handled.
 */
public interface ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ParserConfiguration DEFAULT = new Builder().build();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static Builder with()
    {
        return new Builder();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ErrorState errorState();

    MemoHandler memoHandler();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class Builder
    {
        // -----------------------------------------------------------------------------------------

        // Underscore to not conflict with function names in javadoc tags.

        private Supplier<? extends ErrorState> _errorState;
        private Supplier<? extends MemoHandler> _memoHandler;

        // -----------------------------------------------------------------------------------------

        public Builder errorState(Supplier<? extends ErrorState> supplier)
        {
            this._errorState = supplier;
            return this;
        }

        // -----------------------------------------------------------------------------------------

        public Builder memoStrategy(Supplier<? extends MemoHandler> supplier)
        {
            this._memoHandler = supplier;
            return this;
        }

        // -----------------------------------------------------------------------------------------

        public ParserConfiguration build()
        {
            return new ParserConfiguration()
            {
                @Override
                public ErrorState errorState()
                {
                    return _errorState != null
                        ? _errorState.get()
                        : new DefaultErrorState();
                }

                @Override
                public MemoHandler memoHandler()
                {
                    return _memoHandler != null
                        ? _memoHandler.get()
                        : new DefaultMemoHandler();
                }
            };
        }

        // -----------------------------------------------------------------------------------------
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
