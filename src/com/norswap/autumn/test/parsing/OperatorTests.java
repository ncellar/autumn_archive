package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.TestRunner;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.test.parsing.Common.*;

public final class OperatorTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean testDumb = false;

    ParsingExpression pe;

    Runnable[] tests = {
        this::testLiteral,
        this::testAny,
        this::testCharRange,
        this::testCharSet,
        this::testSequence,
        this::testChoice,
        this::testOptional,
        this::testZeroMore,
        this::testOneMore,
        this::testLookahead,
        this::testNot,
        this::testLongestMatch
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        run();
    }

    public static void run()
    {
        new OperatorTests().doRun();
        System.out.println("Operator tests succeeded.");
    }

    void doRun()
    {
        TestRunner runner = new TestRunner(tests);
        runner.run();

        testDumb = true;
        runner.run();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression pe(ParsingExpression pe)
    {
        if (testDumb)
        {
            return dumb(pe);
        }

        return pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void testLiteral()
    {
        ensureMatch(pe(literal("test")), "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testAny()
    {
        ensureMatch(pe(any()), "x");
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharRange()
    {
        pe = pe(charRange('a', 'c'));
        ensureMatch(pe, "a");
        ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharSet()
    {
        pe = pe(charSet("abc"));
        ensureMatch(pe, "a");
        ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testSequence()
    {
        ensureMatch(pe(sequence(literal("a"), literal("b"), literal("c"))), "abc");
    }

    // ---------------------------------------------------------------------------------------------

    public void testChoice()
    {
        pe = pe(choice(literal("a"), literal("b"), literal("c")));
        ensureMatch(pe, "a");
        ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testOptional()
    {
        pe = pe(optional(literal("a")));
        ensureMatch(pe, "a");
        ensureMatch(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testZeroMore()
    {
        pe = pe(zeroMore(literal("a")));
        ensureMatch(pe, "aaaa");
        ensureMatch(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testOneMore()
    {
        pe = pe(oneMore(literal("a")));
        ensureMatch(pe, "aaaa");
        ensureFail(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLookahead()
    {
        ensureSuccess(pe(lookahead(literal("test"))), "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testNot()
    {
        pe = pe(not(literal("test")));
        ensureSuccess(pe, "bird");
        ensureFail(pe, "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLongestMatch()
    {
        pe = pe(longestMatch(literal("a"), literal("ab"), literal("z"), literal("abc")));
        ensureMatch(pe, "abc");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
