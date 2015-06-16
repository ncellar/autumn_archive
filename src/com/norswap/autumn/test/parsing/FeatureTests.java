package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestRunner;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.test.TestConfiguration.parser;
import static com.norswap.autumn.test.parsing.ParseTreeBuilder.$;

public final class FeatureTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Runnable[] tests = {
        this::testToken,
        this::testLeftRecursive,
        this::testLeftAssociative,
        this::testCapture,
        this::testMultipleCapture,
        this::testRightAssociativity,
        this::testLeftAssociativity,
        this::testPrecedence,
        this::testExpression,
        this::testExpression2,
        this::testExpression3
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // ---------------------------------------------------------------------------------------------

    ParsingExpression

    plus = capture("+", sequence(
        capture("left", reference("expr")),
        literal("+"),
        capture("right", reference("expr")))),

    minus = capture("-", sequence(
        capture("left", reference("expr")),
        literal("-"),
        capture("right", reference("expr")))),

    mult = capture("*", sequence(
        capture("left", reference("expr")),
        literal("*"),
        capture("right", reference("expr")))),

    div = capture("/", sequence(
        capture("left", reference("expr")),
        literal("/"),
        capture("right", reference("expr")))),

    times = capture("*", sequence(
        capture("left", reference("expr")),
        literal("*"),
        capture("right", reference("expr")))),

    exp = capture("^", sequence(
        capture("left", reference("expr")),
        literal("^"),
        capture("right", reference("expr")))),

    num = captureText("num", charRange('1', '9'));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        run();
    }

    public static void run()
    {
        new FeatureTests().doRun();
        System.out.println("Feature tests succeeded.");
    }

    void doRun()
    {
        TestRunner runner = new TestRunner(tests);
        runner.run();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void testToken()
    {
        ParsingExpression expr = oneMore(token(literal("*")));

        Ensure.match("*", expr);
        Ensure.match("* \n\t", expr);
        Ensure.match("* \n\t*** * * \n\t", expr);
        Ensure.match("* // hello lol", expr);
        Ensure.match("* /* is diz real life? */", expr);
        Ensure.match("* /* nested /* amazing innit? */ lol */", expr);
        Ensure.fails(" ", expr);
        Ensure.match(" *", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftRecursive()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftRecursive(reference("expr"), literal("*")),
            num.deepCopy()));

        Ensure.match("1", expr);
        Ensure.match("1*", expr);
        Ensure.match("1***", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftAssociative(reference("expr"), literal("+"), reference("expr")),
            leftAssociative(reference("expr"), literal("*"), reference("expr")),
            num.deepCopy()));

        Ensure.match("1", expr);
        Ensure.match("1+1", expr);
        Ensure.match("1*1", expr);
        Ensure.match("1+1+1+1", expr);
        Ensure.match("1*1+1*1", expr);
    }


    // ---------------------------------------------------------------------------------------------

    public void testCapture()
    {
        ParsingExpression expr = captureText("a", oneMore(literal("a")));

        Parser parser = parser("aaa");
        parser.parse(expr);

        ParseTree tree = parser.tree();
        ParseTree aTree = tree.get("a");

        Ensure.equals(aTree.value, "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        ParsingExpression expr = sequence(
            oneMore(captureTextGrouped("a", literal("a"))));

        Parser parser = parser("aaa");
        parser.parse(expr);

        ParseTree tree = parser.tree();
        Array<ParseTree> aResults = tree.get("a").children;

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
            Ensure.equals(aResults.get(i).value, "a");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testRightAssociativity()
    {
        ParsingExpression expr1 = recursive$("expr", choice(
            leftRecursive(plus.deepCopy()),
            num.deepCopy()));

        ParsingExpression expr2 = recursive$("expr", leftRecursive(choice(
            plus.deepCopy(),
            num.deepCopy())));

        for (ParsingExpression expr: new ParsingExpression[]{expr1, expr2})
        {
            Parser parser = parser("1+2+3");
            parser.parse(expr);
            Ensure.equals(parser.endPosition(), 5);
            ParseTree tree = parser.tree();

            ParseTree expected = $($("+",
                $("left", $("num", "1")),
                $("right", $("+",
                    $("left", $("num", "2")),
                    $("right", $("num", "3"))))));

            Ensure.equals(tree, expected);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        // NOTE(norswap): it also works if leftAssociative is nested inside the capture

        ParsingExpression expr = recursive$("expr", choice(
            leftAssociative(plus.deepCopy()),
            num.deepCopy()));

        Parser parser = parser("1+2+3");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree expected = $($("+",
            $("left", $("+",
                $("left", $("num", "1")),
                $("right", $("num", "2")))),
            $("right", $("num", "3"))));

        Ensure.equals(tree, expected);
    }

    // ---------------------------------------------------------------------------------------------

    public void testPrecedence()
    {
        // NOTE(norswap): it also works if leftAssociative is nested inside the capture

        ParsingExpression expr = recursive$("expr", choice(
            precedence(1, leftAssociative(plus.deepCopy())),
            precedence(2, leftAssociative(mult.deepCopy())),
            precedence(3, leftAssociative(exp.deepCopy())),
            num.deepCopy()));

        Parser parser = parser("1+2*3");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree expected = $($("+",
            $("left", $("num", "1")),
            $("right", $("*",
                $("left", $("num", "2")),
                $("right", $("num", "3"))))));

        Ensure.equals(tree, expected);

        parser = parser("1*2+3");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        tree = parser.tree();

        expected = $($("+",
            $("left", $("*",
                $("left", $("num", "1")),
                $("right", $("num", "2")))),
            $("right", $("num", "3"))));

        Ensure.equals(tree, expected);
    }

    // ---------------------------------------------------------------------------------------------

    public void testExpression()
    {
        ParsingExpression expr = recursive$("expr", cluster(
            exprLeftAssoc(1, plus.deepCopy()),
            exprLeftAssoc(1, minus.deepCopy()),
            exprLeftAssoc(2, mult.deepCopy()),
            exprLeftAssoc(2, div.deepCopy()),
            exprAlt(3, num.deepCopy())));

        Parser parser = parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree expected = $($("+",
            $("left", $("+",
                    $("left", $("-",
                        $("left", $("+",
                            $("left", $("num", "1")),
                            $("right", $("num", "2")))),
                        $("right", $("num", "3")))),
                    $("right", $("*",
                        $("left", $("/",
                            $("left", $("*",
                                $("left", $("num", "4")),
                                $("right", $("num", "5")))),
                            $("right", $("num", "6")))),
                        $("right", $("num", "7")))))),
            $("right", $("num", "8"))));

        Ensure.equals(tree, expected);
    }

    public void testExpression2()
    {
        // NOTE(norswap): Same as testExpression() but + and - are now right-associative.

        ParsingExpression expr = recursive$("expr", cluster(
            exprLeftRecur(1, plus.deepCopy()),
            exprLeftRecur(1, minus.deepCopy()),
            exprLeftAssoc(2, mult.deepCopy()),
            exprLeftAssoc(2, div.deepCopy()),
            exprAlt(3, num.deepCopy())));

        Parser parser = parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree expected = $($("+",
            $("left", $("num", "1")),
            $("right", $("-",
                $("left", $("num", "2")),
                $("right", $("+",
                    $("left", $("num", "3")),
                    $("right", $("+",
                        $("left", $("*",
                            $("left", $("/",
                                $("left", $("*",
                                    $("left", $("num", "4")),
                                    $("right", $("num", "5")))),
                                $("right", $("num", "6")))),
                            $("right", $("num", "7")))),
                        $("right", $("num", "8"))))))))));

        Ensure.equals(tree, expected);
    }

    public void testExpression3()
    {
        // NOTE(norswap): Same as testExpression() but * and / are now right-associative.

        ParsingExpression expr = recursive$("expr", cluster(
            exprLeftAssoc(1, plus.deepCopy()),
            exprLeftAssoc(1, minus.deepCopy()),
            exprLeftRecur(2, mult.deepCopy()),
            exprLeftRecur(2, div.deepCopy()),
            exprAlt(3, num.deepCopy())));

        Parser parser = parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree expected = $($("+",
            $("left", $("+",
                $("left", $("-",
                    $("left", $("+",
                        $("left", $("num", "1")),
                        $("right", $("num", "2")))),
                    $("right", $("num", "3")))),
                $("right", $("*",
                    $("left", $("num", "4")),
                    $("right", $("/",
                        $("left", $("num", "5")),
                        $("right", $("*",
                            $("left", $("num", "6")),
                            $("right", $("num", "7")))))))))),
            $("right", $("num", "8"))));

        Ensure.equals(tree, expected);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
