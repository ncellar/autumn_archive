package com.norswap.autumn.test.languages.clike;

import com.norswap.autumn.parsing.extensions.CustomStateIndex;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.state.CustomState;

public final class CLikeExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    private static final SyntaxExtension[] syntaxExtensions = new SyntaxExtension[] {
        new CLikeSyntaxExtension("TYPEDEF"),
        new CLikeSyntaxExtension("TYPEUSE")};

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new CLikeState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public SyntaxExtension[] syntaxExtensions()
    {
        return syntaxExtensions;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
