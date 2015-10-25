package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.state.CustomStateFactory;
import com.norswap.autumn.parsing.state.errors.ErrorState;
import com.norswap.util.Array;

/**
 * [Immutable] The parser configuration allows the user to configure operational details of the
 * parse, such as how errors and memoization are handled.
 */
public interface ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParserConfiguration build()
    {
        return new ParserConfigurationBuilder().build();
    }

    // ---------------------------------------------------------------------------------------------

    static ParserConfigurationBuilder with()
    {
        return new ParserConfigurationBuilder();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ErrorState errorState();

    MemoHandler memoHandler();

    Array<CustomStateFactory> customStateFactories();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
