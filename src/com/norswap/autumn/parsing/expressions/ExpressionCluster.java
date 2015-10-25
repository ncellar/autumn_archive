package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.state.Seed;
import com.norswap.autumn.parsing.expressions.abstrakt.NaryParsingExpression;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.DeepCopy;

import java.util.Arrays;
import java.util.function.Predicate;

public final class ExpressionCluster extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class PrecedenceEntry
    {
        public final ExpressionCluster cluster;
        public final int initialPosition;
        public int minPrecedence;

        public PrecedenceEntry(ExpressionCluster cluster, int initialPosition, int minPrecedence)
        {
            this.cluster = cluster;
            this.initialPosition = initialPosition;
            this.minPrecedence = minPrecedence;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Group extends NaryParsingExpression
    {
        public int precedence;
        public boolean leftRecursive;
        public boolean leftAssociative;

        @Override
        public void parse(Parser parser, ParseState state)
        {
            throw new Error("The parse method of " + getClass().getName()
                + " is not supposed to be called.");
        }

        @Override
        public String ownDataString()
        {
            return "precedence: " + precedence + ", " +
                (leftAssociative
                    ? "associative"
                    : leftRecursive
                        ? "recursive"
                        : "");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Each groups holds alternates of similar precedence. The array is sorted
     * in order of decreasing precedence.
     */
    public Group[] groups;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        // NOTE(norswap): Clusters can't contain LeftRecursive sub-expressions that go through
        // the expression to achieve left-recursion. Neither can the cluster recurse through
        // other clusters. Formally, any recursion cycle that a cluster is a part of
        // can't contain any LeftRecursive or other cluster nodes.

        // This variable holds the current seed value throughout the function.
        ParseChanges changes = Seed.get(state, this);

        if (changes != null)
        {
            // If this cluster is already in the process of being parsed at this position, use
            // the seed value.

            state.merge(changes);
            return;
        }

        changes = ParseChanges.failure();
        Seed.push(state, this, changes);
        int changesPrecedence = 0;

        // Acquire the current precedence for the cluster.

        PrecedenceEntry entry = state.minPrecedence.peek();
        final int minPrecedence;

        if (entry == null || entry.cluster != this)
        {
            // This is the root invocation of the cluster, register an entry.
            entry = new PrecedenceEntry(this, state.start, 0);
            state.minPrecedence.push(entry);
            minPrecedence = 0;
        }
        else
        {
            minPrecedence = entry.minPrecedence;
        }

        // Used to sometimes inhibit error reporting.
        boolean report = true;

        for (Group group : groups)
        {
            // This condition, coupled with the subsequent {@link Parser#setMinPrecedence} call,
            // blocks recursion into alternates of lower precedence. It also blocks recursion into
            // alternates of the same precedence if the current alternate is left-associative, in
            // order to prevent right-recursion (left-recursion is handled via the seed).

            if (group.precedence < minPrecedence)
            {
                if (changes.failed())
                {
                    // Bypass error handling: it's unfair to say that the whole cluster
                    // failed at this position, because maybe a lower precedence operator would
                    // have matched if the minPrecedence was lower.

                    report = false;
                }

                // Because alternates are tried in decreasing order of precedence, we can exit
                // the loop immediately.
                break;
            }

            // Safe because inter-expression recursion is forbidden.
            entry.minPrecedence = group.precedence + (group.leftAssociative ? 1 : 0);

            ParseChanges oldChanges;
            do {
                oldChanges = changes;

                for (ParsingExpression operand: group.operands)
                {
                    operand.parse(parser, state);

                    if (state.end > changes.end
                        || group.precedence > changesPrecedence && state.end != -1)
                    {
                        // The seed was grown, try to grow it again starting from the first
                        // recursive rule.

                        state.clusterAlternate = operand;
                        changes = state.extract();
                        changesPrecedence = group.precedence;
                        Seed.set(state, changes);
                        state.discard();
                        break;
                    }
                    else
                    {
                        // This rule couldn't grow the seed, try the next one.
                        state.discard();
                    }
                }

                // If no rule could grow the seed, exit the loop.
                if (oldChanges.end >= changes.end)
                {
                    changes = oldChanges;
                    break;
                }

            // Non-left recursive rules will not yield longer matches, so no use trying them.
            } while (group.leftRecursive);
        }

        state.merge(changes);
        Seed.pop(state);

        // This is the root invocation of the cluster, unregister the entry.
        if (entry.initialPosition == state.start)
        {
            state.minPrecedence.pop();
        }
        // This is a recursive invocation of the cluster, restore the previous precedence.
        else
        {
            entry.minPrecedence = minPrecedence;
        }

        if (state.failed() && report)
        {
            state.fail(this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] children()
    {
        return Arrays.stream(groups)
            .flatMap(g -> Arrays.stream(g.operands))
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        int pos = 0;

        for (Group group: groups)
        {
            if (position < pos + group.operands.length)
            {
                group.operands[position - pos] = pe;
                return;
            }

            pos += group.operands.length;
        }

        throw new RuntimeException(
            "Requesting child " + position + " of an expression with only " + pos + "children.");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void copyOwnData()
    {
        groups = DeepCopy.deepClone(groups);

        for (Group group: groups)
        {
            group.operands = group.operands.clone();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String childDataString(int position)
    {
        int pos = 0;

        for (Group group: groups)
        {
            if (position < pos + group.operands.length)
            {
                return "precedence: " + group.precedence;
            }

            pos += group.operands.length;
        }

        throw new RuntimeException(
            "Requesting child " + position + " of an expression with only " + pos + "children.");
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.any(this, firsts(null));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return Arrays.stream(groups)
            .filter(g -> !g.leftRecursive)
            .flatMap(g -> Arrays.stream(g.operands))
            .toArray(ParsingExpression[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
