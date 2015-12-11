package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.annotations.NonNull;

/**
 * Interfaces that extensions to the parser must implement.
 * <p>
 * An extension is made of an optional grammar transformation and an optional custom parse state.
 */
public interface Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new instance of the custom parse state, if the extension requires one, null
     * otherwise (the default). Called once per parser invocation ({@link Parser#parseRoot} or
     * {@link Parser#parse}).
     */
    default CustomState customParseState()
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies transformation to the given grammar. See {@link GrammarBuilderExtensionView}.
     */
    default void transform(GrammarBuilderExtensionView grammar) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a list of grammar syntax extensions defined by this extension.
     */
    default @NonNull SyntaxExtension[] syntaxExtensions()
    {
        return new SyntaxExtension[0];
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the index at with the custom state for this extension can be found in {@link
     * ParseState#customStates}. This index should be made static to the extension and must be
     * obtained by a call to {@link CustomStateIndex#allocate}.
     * <p>
     * The default return value (-1) indicates the extension does not require custom state.
     */
    default int stateIndex()
    {
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
