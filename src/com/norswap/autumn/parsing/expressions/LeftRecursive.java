package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.state.Seed;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

public final class LeftRecursive extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean leftAssociative;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseChanges changes = Seed.get(state, this);

        if (changes != null)
        {
            state.merge(changes);
            return;
        }
        else if (leftAssociative && state.blocked.containsID(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            // We bypass error handling: it is not expected that the input matches this expression.

            state.fail();
            return;
        }

        changes = ParseChanges.failure();
        Seed.push(state, this, changes);

        if (leftAssociative)
        {
            state.blocked.push(this);
        }

        // Keep parsing the operand, as long as long as the seed keeps growing.

        while (true)
        {
            operand.parse(parser, state);

            if (changes.end >= state.end)
            {
                // If no rule could grow the seed, exit the loop.
                state.discard();
                break;
            }
            else
            {
                // Update the seed and retry the rule.

                changes = state.extract();
                Seed.set(state, changes);
                state.discard();
            }
        }

        state.merge(changes);
        Seed.pop(state);

        if (state.failed())
        {
            state.fail(this);
        }

        if (leftAssociative)
        {
            state.blocked.pop();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return leftAssociative ? "left-associative" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
