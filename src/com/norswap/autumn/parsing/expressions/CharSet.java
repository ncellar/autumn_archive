package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.StringEscape;

/**
 * Attempts to match the next input character to a range of characters.
 *
 * Succeeds if the next input character is the set.
 *
 * On success, the end position is start position + 1.
 */
public final class CharSet extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char[] chars;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        char c = parser.text.charAt(state.start);

        for (char d : chars)
        {
            if (c == d)
            {
                state.advance(1);
                return;
            }
        }

        parser.fail(this, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        char c = parser.text.charAt(position);

        for (char d : chars)
        {
            if (c == d)
            {
                return position + 1;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("charSet(\"");
        builder.append(StringEscape.escape(new String(chars)));
        builder.append("\")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
