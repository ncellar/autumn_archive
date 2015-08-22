package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.NaryParsingExpression;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.util.DeepCopy;

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
        public String ownPrintableData()
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
        // NOTE(norswap): Clusters can't contain left-recursive sub-expressions that go through
        // the expression to achieve left-recursion. Neither can the cluster recurse through
        // other clusters. Formally, any recursion cycle that a cluster is a part of
        // can't contain any LeftRecursive or other cluster nodes.

        // This variable holds the current seed value throughout the function.
        OutputChanges changes = state.getSeed(this);

        if (changes != null)
        {
            // If this cluster is already in the process of being parsed at this position, use
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

        // Get minimum precedence if we're already parsing this cluster (else it's 0).
        final int minPrecedence = parser.enterPrecedence(this, state.start, 0);

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
                    // have matched (e.g. prefix operator with higher precedence than a postfix
                    // operator).

                    report = false;
                }

                // Because alternates are tried in decreasing order of precedence, we can exit
                // the loop immediately.
                break;
            }

            parser.setMinPrecedence(group.precedence + (group.leftAssociative ? 1 : 0));

            while (true)
            {
                OutputChanges oldChanges = changes;

                for (ParsingExpression operand: group.operands)
                {
                    operand.parse(parser, state);

                    if (state.end > changes.end
                        || group.precedence > changesPrecedence && state.end != -1)
                    {
                        // The seed was grown, try to grow it again starting from the first
                        // recursive rule.

                        parser.clusterAlternate = operand;
                        changes = new OutputChanges(state);
                        changesPrecedence = group.precedence;
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
                if (!group.leftRecursive) {
                    break;
                }
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
    public void appendContentTo(StringBuilder builder)
    {
        builder.append("expr(");

        for (ParsingExpression operand: children())
        {
            operand.appendTo(builder);
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
    public ExpressionCluster clone()
    {
        ExpressionCluster clone = (ExpressionCluster) super.clone();
        clone.groups = DeepCopy.deepClone(groups);
        return clone;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ExpressionCluster deepCopy()
    {
        ExpressionCluster copy = (ExpressionCluster) super.deepCopy();
        copy.groups = DeepCopy.of(groups);
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.any(this, firsts(grammar));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        return Arrays.stream(groups)
            .filter(g -> !g.leftRecursive)
            .flatMap(g -> Arrays.stream(g.operands))
            .toArray(ParsingExpression[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
