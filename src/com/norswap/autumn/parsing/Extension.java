package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfigurationBuilder;

/**
 * Interface to be implemented by parser extensions.
 */
public interface Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Registers the extension with the configuration builder (e.g. register custom parse states).
     */
    void register(ParserConfigurationBuilder builder);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
