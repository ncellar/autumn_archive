package com.norswap.autumn.parsing.extensions.leftrec;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.Seeds;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashSet;

import static com.norswap.util.Caster.cast;

public final class LeftRecursionState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The seeds for ongoing left recursive parsing expression.
     */
    private Seeds seeds = new Seeds();

    /**
     * Set of expressions in which we can't recurse, in order to ensure left-associativity.
     */
    private Array<LeftRecursive> blocked = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges getSeed(ParsingExpression pe)
    {
        return seeds.get(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void setSeed(ParsingExpression pe, ParseChanges seed, int position)
    {
        seeds.set(pe, seed, position);
    }

    // ---------------------------------------------------------------------------------------------

    public void removeSeed(ParsingExpression pe)
    {
        seeds.remove(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void block(LeftRecursive pe)
    {
        blocked.add(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void unblock(LeftRecursive pe)
    {
        blocked.remove(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean blocked(LeftRecursive pe)
    {
        return blocked.contains(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Inputs inputs(ParseState state)
    {
        return Inputs.create(seeds.inputs(state), cast(blocked.clone()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void load(Object inputs)
    {
        Inputs in = (Inputs) inputs;
        seeds.load(in.seeds());
        this.blocked = cast(in.blocked().clone());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object snapshot(ParseState state)
    {
        return seeds.snapshot(state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(Object snapshot, ParseState state)
    {
        seeds.restore(snapshot, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(Object snapshot, ParseState state)
    {
        seeds.uncommit(snapshot, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        seeds.commit(state);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @AutoValue
    public static abstract class Inputs
    {
        public static Inputs create(
            @Nullable Object seeds,
            HashSet<ParsingExpression> blocked)
        {
            return new AutoValue_LeftRecursionState_Inputs(seeds, blocked);
        }

        abstract @Nullable Object seeds();
        abstract HashSet<ParsingExpression> blocked();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
