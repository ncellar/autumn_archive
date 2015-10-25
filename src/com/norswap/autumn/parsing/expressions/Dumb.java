package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

/**
 * Parses its operand in dumb mode, like a non-memoizing PEG parser. In this
 * mode, most features cannot be used, including:
 *
 * - error reporting
 * - memoization
 * - left-recursion
 * - associativity
 * - precedence
 * - captures
 *
 * Dumb mode incurs less memory and run-time overhead than the regular mode, but does not
 * have any of its advanced features.
 *
 * It is not necessarily more efficient than the regular mode, because a non-optimized
 * expression cannot benefit from the advanced features such as memoization to fix its
 * performance issues.
 *
 * It is most useful to parse fairly basic combinations of terminal expressions, where the
 * overhead could otherwise be consequent.
 *
 * It is not possible for a child of a dumb expression to switch back to regular mode.
 *
 * Succeeds if its operand succeeds.
 *
 *  On success, its end position is that of its operand.
 */
public final class Dumb extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        int pos = operand.parseDumb(parser, state.start);

        if (pos >= 0)
        {
            state.advance(pos - state.start);
        }
        else
        {
            state.fail(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int pos)
    {
        return operand.parseDumb(parser, pos);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
