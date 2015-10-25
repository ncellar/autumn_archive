package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.ParsingExpression;

import java.util.HashMap;

/**
 * The default memoization strategy memoizes every changeset that it is asked to.
 * <p>
 * It is implement by combining the parsing expression and all parse inputs from the parse state
 * as a single key {@link ParseInputs} in a traditional HashMap.
 */
public final class DefaultMemoHandler implements MemoHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParseInputs, ParseChanges> store;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void memoize(ParsingExpression pe, ParseState state, ParseChanges changeset)
    {
        store.put(state.inputs(pe), changeset);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParseChanges get(ParsingExpression pe, ParseState state)
    {
        return store.get(state.inputs(pe));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
