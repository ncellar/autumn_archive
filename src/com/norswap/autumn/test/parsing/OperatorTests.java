package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestRunner;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class OperatorTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean testDumb = false;

    Source src;

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
        this::testLongestMatch,
        this::testCut
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
        pe = pe(literal("test"));
        src = Source.fromString("test");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testAny()
    {
        pe = pe(any());
        src = Source.fromString("x");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharRange()
    {
        pe = pe(charRange('a', 'c'));

        src = Source.fromString("a");
        Ensure.match(src, pe);

        src = Source.fromString("c");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharSet()
    {
        pe = pe(charSet("abc"));

        src = Source.fromString("a");
        Ensure.match(src, pe);

        src = Source.fromString("c");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testSequence()
    {
        pe = pe(sequence(literal("a"), literal("b"), literal("c")));
        src = Source.fromString("abc");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testChoice()
    {
        pe = pe(choice(literal("a"), literal("b"), literal("c")));

        src = Source.fromString("a");
        Ensure.match(src, pe);

        src = Source.fromString("c");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testOptional()
    {
        pe = pe(optional(literal("a")));

        src = Source.fromString("a");
        Ensure.match(src, pe);

        src = Source.fromString("");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testZeroMore()
    {
        pe = pe(zeroMore(literal("a")));

        src = Source.fromString("aaaa");
        Ensure.match(src, pe);

        src = Source.fromString("");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testOneMore()
    {
        pe = pe(oneMore(literal("a")));

        src = Source.fromString("aaaa");
        Ensure.match(src, pe);

        src = Source.fromString("");
        Ensure.fails(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLookahead()
    {
        pe = pe(lookahead(literal("test")));
        src = Source.fromString("test");
        Ensure.noFail(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testNot()
    {
        pe = pe(not(literal("test")));

        src = Source.fromString("bird");
        Ensure.noFail(src, pe);

        src = Source.fromString("test");
        Ensure.fails(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLongestMatch()
    {
        pe = pe(longestMatch(literal("a"), literal("ab"), literal("z"), literal("abc")));
        src = Source.fromString("abc");
        Ensure.match(src, pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void testCut()
    {
        boolean oldTestDumb = testDumb;
        testDumb = false;
        pe = pe(cuttable("test", sequence(cut("test"), literal("a")), literal("b")));
        testDumb = oldTestDumb;

        src = Source.fromString("a");
        Ensure.match(src, pe);

        src = Source.fromString("b");
        Ensure.fails(src, pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
