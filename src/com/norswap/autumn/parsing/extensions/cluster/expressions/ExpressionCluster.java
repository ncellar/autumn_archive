package com.norswap.autumn.parsing.extensions.cluster.expressions;

import com.norswap.autumn.parsing.extensions.cluster.ClusterExtension;
import com.norswap.autumn.parsing.extensions.cluster.ClusterState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.NaryParsingExpression;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.DeepCopy;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.norswap.util.Caster.cast;

public final class ExpressionCluster extends ParsingExpression
{
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
        ClusterState cstate = cast(state.customStates[ClusterExtension.INDEX]);
        ParseChanges changes;

        if ((changes = cstate.getSeed(this)) != null)
        {
            // If this is a re-entry, use the seed value.
            state.merge(changes);
            return;
        }

        changes = ParseChanges.failure();
        cstate.setSeed(this, changes, state.start);

        ClusterState.Precedence precedence = cstate.getPrecedence(this);
        int oldPrecedence = precedence.oldPrecedence();

        // Iterate over groups in order of decreasing precedence.
        for (Group group: groups)
        {
            // Blocks recursion into alternates of lower precedence. Also blocks recursion into
            // alternate of similar precedence for left-associative groups, to prevent
            // right-recursion.

            if (group.precedence < oldPrecedence)
                break;

            precedence.value = group.precedence + (group.leftAssociative ? 1 : 0);

            leftRec: do {
                for (ParsingExpression alternate: group.operands)
                {
                    alternate.parse(parser, state);

                    if (state.end > changes.end)
                    {
                        // The seed was grown, try growing it again starting from first group rule.
                        cstate.setAlternate(alternate);
                        changes = state.extract();
                        cstate.setSeed(this, changes, state.start);
                        state.discard();
                        continue leftRec;
                    }
                    else
                    {
                        state.discard();
                    }
                }

                // No rule could grow the seed, exit the loop.
                break;
            }
            while (group.leftRecursive);
        }

        cstate.removeSeed(this);
        cstate.removePrecedence(this, precedence);
        state.merge(changes);

        if (state.failed()) {
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
            group.operands = group.operands.clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String childDataString(int position)
    {
        int pos = 0;

        for (Group group: groups)
        {
            if (position < pos + group.operands.length)
                return "precedence: " + group.precedence;

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
