package com.norswap.autumn.parsing.support.dynext;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.state.ParseState;
import static com.norswap.util.Caster.cast;

/**
 * See {@link DynExtExtension}.
 * <p>
 * Because we don't know the expression that is going to be called, we take the safe route and
 * indicate that this expression is nullable.
 */
public final class DynRef extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        DynExtState destate = cast(state.customStates[DynExtExtension.INDEX]);
        destate.target.parse(parser, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
