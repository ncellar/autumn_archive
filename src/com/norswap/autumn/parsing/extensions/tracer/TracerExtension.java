package com.norswap.autumn.parsing.extensions.tracer;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.graph.Transformer;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.extensions.CustomStateIndex;

/**
 * This extension wraps every rule in a {@link Trace} parsing expression that logs the parsing
 * expression whenever it is entered, excepted in dumb mode.
 */
public final class TracerExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new TraceState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        grammar.transform(new Transformer(pe -> new Trace(pe)));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
