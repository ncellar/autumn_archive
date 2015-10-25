package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.Extension;
import com.norswap.autumn.parsing.config.ParserConfigurationBuilder;
import com.norswap.autumn.parsing.state.CustomState.Inputs;

/**
 * Extensions ({@link Extension} using custom state must register a factory with the {@link
 * ParserConfigurationBuilder}. The factory must supply the root inputs for the state, as well as the
 * ability to create the custom state from an associated inputs object. See {@link CustomState}.
 */
public interface CustomStateFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    CustomState build(Inputs inputs);

    // ---------------------------------------------------------------------------------------------

    Inputs rootInputs();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
