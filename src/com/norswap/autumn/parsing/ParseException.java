package com.norswap.autumn.parsing;

/**
 * TODO
 */
public final class ParseException extends RuntimeException
{
    public final ParseError error;

    public ParseException(ParseError error)
    {
        this.error = error;
    }

    @Override
    public String getMessage()
    {
        return error.message();
    }
}
