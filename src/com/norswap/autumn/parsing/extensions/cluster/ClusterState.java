package com.norswap.autumn.parsing.extensions.cluster;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.Seeds;
import com.norswap.autumn.parsing.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.cluster.expressions.Filter;
import com.norswap.autumn.parsing.extensions.cluster.expressions.WithMinPrecedence;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ExportedInputs;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.state.patterns.ValueOutput;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashMap;

import static com.norswap.util.Caster.cast;

public final class ClusterState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Precedence
    {
        Precedence(int value)
        {
            this.value = value;
        }

        // Value is above the history stack.

        public int value;
        Array<Integer> history = new Array<>();

        public int oldPrecedence()
        {
            return history.peekOr(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The seeds for ongoing expression clusters.
     */
    private Seeds seeds = new Seeds();

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     */
    private ValueOutput<ParsingExpression> alternate = new ValueOutput<>();

    /**
     * Maps from expression clusters to their current precedence.
     */
    private HashMap<ExpressionCluster, Precedence> precedences = new HashMap<>();

    /**
     * A stack of expressions with a precedence level that are currently being visited. These
     * expressions are the keys of {@link #precedences}.
     * <p>
     * This data structure is necessary for {@link #getCurrentPrecedence} (used by {@link
     * WithMinPrecedence}) to work.
     */
    private Array<ExpressionCluster> history = new Array<>();

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

    public ParsingExpression getAlternate()
    {
        return alternate.get();
    }

    // ---------------------------------------------------------------------------------------------

    public void setAlternate(ParsingExpression pe)
    {
        alternate.set(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public Precedence getPrecedence(ExpressionCluster pe)
    {
        history.push(pe);

        return precedences.compute(pe, (k, v) -> {
            if (v != null)
            {
                v.history.push(v.value);
                return v;
            }
            else
            {
                return new Precedence(0);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    public void removePrecedence(ExpressionCluster pe, Precedence precedence)
    {
        history.pop();

        if (precedence.history.isEmpty())
        {
            precedences.remove(pe);
        }
        else
        {
            precedence.value = precedence.history.pop();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public Precedence getCurrentPrecedence()
    {
        if (history.isEmpty())
        {
            throw new Error(
                "Trying to retrieve a cluster precedence while none is currently parsing.");
        }

        return precedences.get(history.peek());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Inputs inputs(ParseState state)
    {
        return Inputs.create(
            seeds.inputs(state),
            cast(precedences.clone()),
            history.clone());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void load(Object inputs)
    {
        Inputs in = (Inputs) inputs;
        seeds.load(cast(in.seeds()));
        precedences = cast(in.precedences().clone());
        history = in.history().clone();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object snapshot(ParseState state)
    {
        return new Snapshot(seeds.snapshot(state), alternate.snapshot(state));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(Object snapshot, ParseState state)
    {
        Snapshot snap = (Snapshot) snapshot;
        seeds.restore(snap.seeds, state);
        alternate.restore(snap.alternate, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(Object snapshot, ParseState state)
    {
        Snapshot snap = (Snapshot) snapshot;
        seeds.uncommit(snap.seeds, state);
        alternate.uncommit(snap.alternate, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        alternate.discard(state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        seeds.commit(state);
        alternate.commit(state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return alternate.extract(state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(Object changes, ParseState state)
    {
        alternate.merge(changes, state);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final class Snapshot
    {
        final Object seeds, alternate;

        Snapshot(Object seeds, Object alternate)
        {
            this.seeds = seeds;
            this.alternate = alternate;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @AutoValue
    public static abstract class Inputs
    {
        static Inputs create(
            @Nullable Object seeds,
            HashMap<ParsingExpression, Precedence> precedences,
            Array<ExpressionCluster> history)
        {
            return new AutoValue_ClusterState_Inputs(seeds, precedences, history);
        }

        abstract @Nullable Object seeds();
        abstract HashMap<ParsingExpression, Precedence> precedences();
        abstract Array<ExpressionCluster> history();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
