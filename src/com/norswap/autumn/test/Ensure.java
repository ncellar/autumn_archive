package com.norswap.autumn.test;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.Source;

/**
 * A bunch of static assertion method to express expectations on test outcomes.
 *
 * In case an expectation is violated, a {@link TestFailed} exception is thrown.
 */
public final class Ensure
{
    public static void ensure(boolean test)
    {
        if (!test)
        {
            throw new TestFailed();
        }
    }

    public static <T> void equals(T x, T y)
    {
        if (!x.equals(y))
        {
            throw new TestFailed("got: " + x + ", expected: " + y);
        }
    }

    public static <T> void different(T x, T y)
    {
        if (x.equals(y))
        {
            throw new TestFailed("got: " + x + ", expected it to be different");
        }
    }

    public static <T extends Comparable<T>> void greaterThan(T x, T y)
    {
        if (x.compareTo(y) <= 0)
        {
            throw new TestFailed("got: " + x + ", not greater than: " + y);
        }
    }

    public static <T extends Comparable<T>> void lessThan(T x, T y)
    {
        if (x.compareTo(y) >= 0)
        {
            throw new TestFailed("got: " + x + ", not less than: " + y);
        }
    }

    public static void  match(Source src, ParsingExpression pe)
    {
        Parser parser = TestConfiguration.parser(src);
        parser.parse(pe);
        equals(parser.endPosition(), src.length());
    }

    public static void match(String src, ParsingExpression pe)
    {
        match(Source.fromString(src), pe);
    }

    public static void succeeds(Source src, ParsingExpression pe)
    {
        Parser parser = TestConfiguration.parser(src);
        parser.parse(pe);
        ensure(parser.succeeded());
    }

    public static void succeeds(String src, ParsingExpression pe)
    {
        succeeds(Source.fromString(src), pe);
    }

    public static void noFail(Source src, ParsingExpression pe)
    {
        Parser parser = TestConfiguration.parser(src);
        parser.parse(pe);
        ensure(parser.endPosition() != -1);
    }

    public static void noFail(String src, ParsingExpression pe)
    {
        noFail(Source.fromString(src), pe);
    }

    public static void fails(Source src, ParsingExpression pe)
    {
        Parser parser = TestConfiguration.parser(src);
        parser.parse(pe);
        int pos = parser.endPosition();

        if (pos != -1)
        {
            throw new TestFailed("expecting failure, succeeded with pos: " + pos);
        }
    }

    public static void fails(String src, ParsingExpression pe)
    {
        fails(Source.fromString(src), pe);
    }
}
