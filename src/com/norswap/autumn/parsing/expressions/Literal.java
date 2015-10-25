package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

/**
 * Attempt to match a literal string to the input.
 *
 * Succeeds if the begin of the input matches the string.
 *
 * On success, its end position is the start position + the size of the string.
 */
public final class Literal extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String string;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        int index = 0;
        int pos = state.start;
        final int len = string.length();

        while (index < len && parser.text.charAt(pos) == string.charAt(index))
        {
            ++index;
            ++pos;
        }

        if (index == len)
        {
            state.advance(len);
        }
        else
        {
            state.fail(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        int index = 0;
        int pos = position;
        int len = string.length();

        while (index < len && parser.text.charAt(pos) == string.charAt(index))
        {
            ++index;
            ++pos;
        }

        return index != string.length()
            ? -1
            : pos;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return string;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.bool(this, string.isEmpty());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
