package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * Attempts to match the next input character to a range of characters.
 *
 * Succeeds if the next input character is the range.
 *
 * On success, the end position is start position + 1.
 *
 */
public final class CharRange extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char start;
    public char end;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        char c = parser.text.charAt(state.start);

        if (start <= c && c <= end)
        {
            state.advance(1);
        }
        else
        {
            parser.fail(this, state);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        char c = parser.text.charAt(position);

        return start <= c && c <= end
            ? position + 1
            : -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("charRange(");
        builder.append(start);
        builder.append(", ");
        builder.append(end);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
