package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.nullability.Nullability;
import com.norswap.autumn.util.DeepCopy;

import java.util.Arrays;

public final class ExpressionCluster extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class PrecedenceEntry
    {
        public ExpressionCluster cluster;
        public int initialPosition;
        public int minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO use this?
    public final static class Group implements DeepCopy
    {
        public int precedence;
        public boolean leftRecursive;
        public boolean leftAssociative;
        public ParsingExpression[] operands;

        @Override
        public Group deepCopy()
        {
            Group copy = DeepCopy.clone(this);
            copy.operands = DeepCopy.of(operands, ParsingExpression[]::new);
            return copy;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Operand implements DeepCopy
    {
        public ParsingExpression operand;
        public int precedence;
        public boolean leftRecursive;
        public boolean leftAssociative;


        @Override
        public Operand deepCopy()
        {
            Operand copy = DeepCopy.clone(this);
            copy.operand = operand.deepCopy();
            return copy;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Each sub-array is a group that holds alternates of similar precedence. The array is sorted
      * in order of decreasing precedence.
     */
    public Operand[][] groups;

    /**
     * Each sub-array is a sub-group of the corresponding group in {@link #groups}, holding only
     * the left-recursive operands.
     */
    public Operand[][] recursiveGroups;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        // NOTE(norswap): Clusters can't contain left-recursive sub-expressions that go through
        // the expression to achieve left-recursion. Neither can the cluster recurse through
        // other clusters. Formally, any recursion cycle that a cluster is a part of
        // can't contain any LeftRecursive or other cluster nodes.

        // This variable holds the current seed value throughout the function.
        OutputChanges changes = state.getSeed(this);

        if (changes != null)
        {
            // If this cluster is already in the process of being parsed at this position; use
            // the seed value.

            changes.mergeInto(state);
            return;
        }

        changes = OutputChanges.failure();
        state.pushSheed(this, changes);
        int changesPrecedence = 0;

        // Because of precedence, memoized results might not be correct (might have been obtained
        // with another precedence).

        final int oldFlags = state.flags;
        state.forbidMemoization();

        final int minPrecedence = parser.enterPrecedence(this, state.start, 0);

        // Used to sometimes inhibit error reporting.
        boolean report = true;

        for (int i = 0; i < groups.length; ++i)
        {
            Operand[] group = groups[i], recursiveGroup = recursiveGroups[i];

            int groupPrecedence = group[0].precedence;

            // This condition, coupled with the subsequent {@link Parser#setMinPrecedence} call,
            // blocks recursion into alternates of lower precedence. It also blocks recursion into
            // alternates of the same precedence if the current alternate is left-associative; in
            // order to prevent right-recursion (left-recursion is handled via the seed).

            if (groupPrecedence < minPrecedence)
            {
                if (changes.failed())
                {
                    // Bypass error handling: it's unfair to say that the whole cluster
                    // failed at this position, because maybe a lower precedence operator would
                    // have matched (e.g. prefix operator with higher precedence than a postfix
                    // operator).

                    report = false;
                }

                // Because alternates are tried in decreasing order of precedence, we can exit
                // the loop immediately.
                break;
            }

            parser.setMinPrecedence(
                groupPrecedence + (group[0].leftAssociative ? 1 : 0));

            while (true)
            {
                OutputChanges oldChanges = changes;

                for (Operand operand: group)
                {
                    operand.operand.parse(parser, state);

                    if (state.end > changes.end
                        || groupPrecedence > changesPrecedence && state.end != -1)
                    {
                        // The seed was grown, try to grow it again starting from the first
                        // recursive rule.

                        parser.clusterAlternate = operand.operand;
                        changes = new OutputChanges(state);
                        changesPrecedence = groupPrecedence;
                        state.setSeed(changes);
                        state.resetAllOutput();
                        break;
                    }
                    else
                    {
                        // This rule couldn't grow the seed, try the next one.

                        // Reset cuts as well, as a precaution, while further thinking is done on
                        // the implications (and on the usefulness of the cut operator in general).
                        state.resetAllOutput();
                    }
                }

                // If no rule could grow the seed, exit the loop.
                if (oldChanges.end >= changes.end)
                {
                    changes = oldChanges;
                    break;
                }

                // Non-left recursive rules will not yield longer matches, so no use trying them.
                group = recursiveGroup;
            }
        }

        state.flags = oldFlags;
        changes.mergeInto(state);
        state.popSeed();
        parser.exitPrecedence(minPrecedence, state.start);

        if (state.failed() && report)
        {
            parser.fail(this, state);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("expr(");

        for (ParsingExpression operand: children())
        {
            operand.toString(builder);
            builder.append(", ");
        }

        if (groups.length > 0)
        {
            builder.setLength(builder.length() - 2);
        }

        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return Arrays.stream(groups)
            .flatMap(Arrays::stream)
            .map(o -> o.operand)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        int pos = 0;

        for (Operand[] group : groups)
        {
            if (position < pos + group.length)
            {
                group[position - pos].operand = pe;
                return;
            }

            pos += group.length;
        }

        throw new RuntimeException(
            "Requesting child " + position + " of an expression with only " + pos + "children.");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ExpressionCluster deepCopy()
    {
        ExpressionCluster copy = (ExpressionCluster) super.deepCopy();

        copy.groups = new Operand[groups.length][];
        copy.recursiveGroups = new Operand[groups.length][];

        for (int i = 0; i < groups.length; ++i)
        {
            copy.groups[i] = DeepCopy.of(groups[i], Operand[]::new);
            copy.recursiveGroups[i] = DeepCopy.of(recursiveGroups[i], Operand[]::new);
        }

        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.any(this, firsts());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts()
    {
        return Arrays.stream(groups)
            .flatMap(Arrays::stream)
            .filter(o -> !o.leftRecursive)
            .map(o -> o.operand)
            .toArray(ParsingExpression[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
