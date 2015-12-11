package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.extensions.Extension;

/**
 * This class is used to encapsulate inputs that we wish to persist/transfer accross different
 * parsers.
 * <p>
 * Create them with {@link CustomState#export}
 */
public final class ExportedInputs
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Class<? extends Extension> extension;
    public final Object actualInputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExportedInputs(Class<? extends Extension> extension, Object actualInputs)
    {
        this.extension = extension;
        this.actualInputs = actualInputs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
