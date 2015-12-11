package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.cluster.ClusterState;
import com.norswap.autumn.parsing.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursive;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursionState;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

/**
 * Holds a set of mapping between parsing expressions ({@link ExpressionCluster} and {@link
 * LeftRecursive} instances whose invocation is ongoing) and their seed (an instance of {@link
 * ParseChanges}).
 * <p>
 * Both {@link LeftRecursionState} and {@link ClusterState} require an instance of this.
 */
public final class Seeds implements CustomState, Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The position associated to the seeds.
     */
    private int position;

    /**
     * The set of all expressions for which we currently have a seed value.
     */
    private @Nullable Array<ParsingExpression> seeded;

    /**
     * The set of seeds matching the parsing expressions in {@link #seeded}.
     */
    private @Nullable Array<ParseChanges> seeds;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges get(ParsingExpression pe)
    {
        if (seeded == null) return null;

        int size = seeded.size();

        for (int i = 0; i < size; ++i)
        {
            if (pe == seeded.get(i))
            {
                return seeds.get(i);
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public void set(ParsingExpression pe, ParseChanges seed, int position)
    {
        if (seeded == null)
        {
            this.position = position;
            seeded = new Array<>(pe);
            seeds = new Array<>(seed);
        }
        else if (seeded.peekOr(null) == pe)
        {
            seeds.setLast(seed);
        }
        else
        {
            seeded.push(pe);
            seeds.push(seed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void remove(ParsingExpression pe)
    {
        seeded.pop();
        seeds.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Object inputs(ParseState state)
    {
        return copy();
    }

    // ---------------------------------------------------------------------------------------------

    public void load(Object inputs)
    {
        Seeds that = (Seeds) inputs;
        this.position = that.position;

        if (that.seeded != null)
        {
            this.seeded = that.seeded.clone();
            this.seeds  = that.seeds .clone();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public Object snapshot(ParseState state)
    {
        return seeded != null ? clone() : null;
    }

    // ---------------------------------------------------------------------------------------------

    public void restore(Object snapshot, ParseState state)
    {
        if (snapshot != null)
        {
            Seeds that = (Seeds) snapshot;
            this.position = that.position;
            this.seeded = that.seeded;
            this.seeds = that.seeds;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void uncommit(Object snapshot, ParseState state)
    {
        restore(snapshot, state);
    }

    // ---------------------------------------------------------------------------------------------

    public void commit(ParseState state)
    {
        if (state.end > position)
        {
            position = 0;
            seeded = null;
            seeds = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected Seeds clone()
    {
        try {
            return (Seeds) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("impossible");
        }
    }

    // ---------------------------------------------------------------------------------------------

    private Seeds copy()
    {
        Seeds out = clone();

        if (seeded != null)
        {
            out.seeded = seeded.clone();
            out.seeds = seeds.clone();
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o)
    {
        Seeds that;
        return this == o
            || o instanceof Seeds
            && (that = (Seeds) o) != null
            && position == that.position
            && (seeded == that.seeded || seeded != null && seeded.equals(that.seeded))
            && (seeds  == that.seeds  || seeds  != null && seeds .equals(that.seeds ));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = position;
        result = 31 * result + (seeded != null ? seeded.hashCode() : 0);
        result = 31 * result + (seeds  != null ? seeds .hashCode() : 0);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
