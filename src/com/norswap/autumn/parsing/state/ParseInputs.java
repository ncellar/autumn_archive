package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.expressions.ExpressionCluster.PrecedenceEntry;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.CustomState.Inputs;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.Arrays;

/**
 * See {@link ParseState}, "Parse Inputs" section.
 */
public final class ParseInputs
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;
    public final int start;
    public final int blackStart;
    public final int precedence;
    public final boolean recordErrors;
    public final @Nullable Array<Seed> seeds;
    public final Array<LeftRecursive> blocked;
    public final Array<PrecedenceEntry> minPrecedence;
    public final Inputs[] customInputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseInputs(
        ParsingExpression pe,
        int start,
        int blackStart,
        int precedence,
        boolean recordErrors,
        Array<Seed> seeds,
        Array<LeftRecursive> blocked,
        Array<PrecedenceEntry> minPrecedence,
        Inputs[] customInputs)
    {
        this.pe = pe;
        this.start = start;
        this.blackStart = blackStart;
        this.precedence = precedence;
        this.recordErrors = recordErrors;
        this.seeds = seeds;
        this.blocked = blocked;
        this.minPrecedence = minPrecedence;
        this.customInputs = customInputs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ParseInputs)) return false;

        ParseInputs that = (ParseInputs) o;

        if (start != that.start) return false;
        if (blackStart != that.blackStart) return false;
        if (precedence != that.precedence) return false;
        if (!pe.equals(that.pe)) return false;
        if (!seeds.equals(that.seeds)) return false;
        if (!blocked.equals(that.blocked)) return false;

        return Arrays.equals(customInputs, that.customInputs);

    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = pe.hashCode();
        result = 31 * result + start;
        result = 31 * result + blackStart;
        result = 31 * result + precedence;
        result = 31 * result + seeds.hashCode();
        result = 31 * result + blocked.hashCode();
        result = 31 * result + Arrays.hashCode(customInputs);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
