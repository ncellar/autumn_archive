package com.norswap.autumn.parsing.extensions.cluster;

import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.extensions.CustomStateIndex;

/**
 * Extension that enables using expression clusters in the grammar.
 */
public final class ClusterExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final SyntaxExtension[] syntaxExtensions = new SyntaxExtension[] {
        new SyntaxExtensionCluster(),
        new SyntaxExtensionFilter()};

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new ClusterState();
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
