package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.tree.ParseTree;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestRunner;

import java.util.List;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.test.parsing.Common.*;
import static com.norswap.autumn.test.parsing.ParseTreeBuilder.$;

public final class FeatureTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ParsingExpression pe;

    ParseTree tree, expected;

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
        pe = oneMore(token(literal("*")));

        ensureMatch(pe, "*");
        ensureMatch(pe, "* \n\t");
        ensureMatch(pe, "* \n\t*** * * \n\t");
        ensureMatch(pe, "* // hello lol");
        ensureMatch(pe, "* /* is diz real life? */");
        ensureMatch(pe, "* /* nested /* amazing innit? */ lol */");
        ensureFail(pe, " ");
        ensureMatch(pe, " *");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftRecursive()
    {
        pe = named$("expr", choice(
            leftRecursive(reference("expr"), literal("*")),
            num.deepCopy()));

        ensureMatch(pe, "1");
        ensureMatch(pe, "1*");
        ensureMatch(pe, "1***");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        pe = named$("expr", choice(
            leftAssociative(reference("expr"), literal("+"), reference("expr")),
            leftAssociative(reference("expr"), literal("*"), reference("expr")),
            num.deepCopy()));

        ensureMatch(pe, "1");
        ensureMatch(pe, "1+1");
        ensureMatch(pe, "1*1");
        ensureMatch(pe, "1+1+1+1");
        ensureMatch(pe, "1*1+1*1");
    }


    // ---------------------------------------------------------------------------------------------

    public void testCapture()
    {
        tree = tree(captureText("a", oneMore(literal("a"))), "aaa");
        Ensure.equals(tree.get("a").value, "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        tree = tree(sequence(oneMore(captureTextGrouped("a", literal("a")))), "aaa");
        List<ParseTree> aResults = tree.group("a");

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
            Ensure.equals(aResults.get(i).value, "a");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testRightAssociativity()
    {
        ParsingExpression expr1 = named$("expr", choice(
            leftRecursive(plus.deepCopy()),
            num.deepCopy()));

        ParsingExpression expr2 = named$("expr", leftRecursive(choice(
            plus.deepCopy(),
            num.deepCopy())));

        for (ParsingExpression expr: new ParsingExpression[]{expr1, expr2})
        {
            tree = tree(expr, "1+2+3");

            expected = $($("+",
                $("left", $("num", "1")),
                $("right", $("+",
                    $("left", $("num", "2")),
                    $("right", $("num", "3"))))));

            // [[+: [
            //  left: [num: "1"],
            //  right: [[+: [
            //      left: [num: "2"],
            //      right: [num: "3"]]]]]]],
            //
            // expected: [+: [
            // left: [num: "1"],
            // right: [+: [
            //      left: [num: "2"],
            //      right: [num: "3"]]]]]

            Ensure.equals(tree, expected);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        // NOTE(norswap): it also works if leftAssociative is nested inside the capture

        pe = named$("expr", choice(
            leftAssociative(plus.deepCopy()),
            num.deepCopy()));

        tree = tree(pe, "1+2+3");

        expected = $($("+",
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

        pe = named$("expr", choice(
            precedence(1, leftAssociative(plus.deepCopy())),
            precedence(2, leftAssociative(mult.deepCopy())),
            precedence(3, leftAssociative(exp.deepCopy())),
            num.deepCopy()));

        tree = tree(pe, "1+2*3");

        expected = $($("+",
            $("left", $("num", "1")),
            $("right", $("*",
                $("left", $("num", "2")),
                $("right", $("num", "3"))))));

        Ensure.equals(tree, expected);

        tree = tree(pe, "1*2+3");

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
        pe = named$("expr", cluster(
            groupLeftAssoc(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftAssoc(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = tree(pe, "1+2-3+4*5/6*7+8");

        expected = $($("+",
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

        pe = named$("expr", cluster(
            groupLeftRec(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftAssoc(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = tree(pe, "1+2-3+4*5/6*7+8");

        expected = $($("+",
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

        pe = named$("expr", cluster(
            groupLeftAssoc(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftRec(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = tree(pe, "1+2-3+4*5/6*7+8");

        expected = $($("+",
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
