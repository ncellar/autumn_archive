package com.norswap.autumn.parsing.support;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class GrammarGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int i = 0;

    public static ParsingExpression

    and         = token(literal("&")),
    bang        = token(literal("!")),
    equal       = token(literal("=")),
    plus        = token(literal("+")),
    qMark       = token(literal("?")),
    colon       = token(literal(":")),
    semi        = token(literal(";")),
    slash       = token(literal("/")),
    star        = token(literal("*")),
    tilda       = token(literal("~")),
    lBrace      = token(literal("{")),
    rBrace      = token(literal("}")),
    lParen      = token(literal("(")),
    rParen      = token(literal(")")),
    underscore  = token(literal("_")),
    starPlus    = token(literal("*+")),
    plusPlus    = token(literal("++")),
    arrow       = token(literal("->")),
    lAnBra      = token(literal("<")),
    rAnBra      = token(literal(">")),
    comma       = token(literal(",")),
    commaPlus   = token(literal(",+")),
    minus       = token(literal("-")),
    hash        = token(literal("#")),

    digit         = charRange('0', '9'),
    hexDigit      = choice(digit, charRange('a', 'f'), charRange('A', 'F')),
    letter        = choice(charRange('a', 'z'), charRange('A', 'Z')),
    nameChar      = choice(letter, digit, literal("_")),

    num           = token(oneMore(digit)),

    exprLit       = token(literal("expr"), not(nameChar)),
    dropLit       = token(literal("drop"), not(nameChar)),
    left_assoc    = token(literal("left_assoc"), not(nameChar)),
    left_recur    = token(literal("left_recur"), not(nameChar)),

    reserved      = choice(exprLit, dropLit, left_assoc, left_recur),

    escape = named$("escape", choice(
        sequence(literal("\\u"), hexDigit, hexDigit, hexDigit, hexDigit),
        sequence(literal("\\"), charSet("tn")),
        sequence(not(literal("\\u")), literal("\\"), any()))),

    character = named$("character", choice(escape, notCharSet("\n\\"))),

    range = named$("range", token(
        literal("["),
        captureText("first", character),
        literal("-"),
        captureText("last", character),
        literal("]"))),

    charSet = named$("charSet", token(
        literal("["),
        captureText("charSet", oneMore(not(literal("]")), character)),
        literal("]"))),

    notCharSet = named$("notCharSet", token(
        literal("^["),
        captureText("notCharSet", oneMore(not(literal("]"), character))),
        literal("]"))),

    stringLit = named$("stringLit", token(
        literal("\""),
        captureText("literal", zeroMore(not(literal("\"")), character)),
        literal("\""))),

    name = named$("name", token(choice(
        sequence(not(reserved), letter, zeroMore(nameChar)),
        sequence(literal("'"), aloUntil(any(), literal("'")))))),

    nameOrDollar = choice(captureText("name", name), capture("dollar", literal("$"))),

    reference = sequence(
        captureText("name", name),
        optional(token(literal("allow")),
            lBrace, aloSeparated(captureTextGrouped("allowed", name), comma), rBrace),
        optional(token(literal("forbid")),
            lBrace, aloSeparated(captureTextGrouped("forbidden", name), comma), rBrace)),

    captureSuffix = group$("captureSuffixes", capture(choice(
        capture("capture",
            sequence(token(literal(":"), optional(capture("captureText", literal("+")))))),
        capture("accessor",
            sequence(minus, nameOrDollar)),
        capture("group",
            sequence(hash, nameOrDollar)),
        capture("tag",
            sequence(tilda, nameOrDollar))))),

    expr = reference("expr"),

    parsingExpression = recursive$("expr", cluster(

        // NOTE(norswap)
        // Using left associativity for choice and sequence ensures that sub-expressions
        // have higher precedence. So we don't get pesky choice of choices or sequence of sequences.

        groupLeftAssoc(++i,
            named$("choice", capture("choice", aloSeparated(expr, slash)))),

        groupLeftAssoc(++i,
            capture("sequence", sequence(expr, oneMore(expr)))),

        group(++i,
            capture("and", sequence(and, expr)),
            capture("not", sequence(bang, expr))),

        group(++i,
            capture("until", sequence(expr, starPlus, expr)),
            capture("aloUntil", sequence(expr, plusPlus, expr)),
            capture("separated", sequence(expr, comma, expr)),
            capture("aloSeparated", sequence(expr, commaPlus, expr)),
            capture("optional", sequence(expr, qMark)),
            capture("zeroMore", sequence(expr, star)),
            capture("oneMore", sequence(expr, plus))),

        groupLeftRec(++i,
            capture("capture", sequence(expr, oneMore(captureSuffix)))),

        group(++i,
            sequence(lParen, exprDropPrecedence(expr), rParen),
            capture("drop", sequence(dropLit, expr)),
            capture("ref", reference),
            capture("any", underscore),
            capture("charRange", range),
            captureText("stringLit", stringLit),
            captureText("charSet", charSet),
            captureText("notCharSet", notCharSet)))),

    exprAnnotation = sequence(
        literal("@"),
        choice(
            captureText("precedence", num),
            capture("increment", plus),
            capture("same", equal),
            capture("left_assoc", left_assoc),
            capture("left_recur", left_recur),
            captureText("name", name))),

    exprCluster = named$("cluster", capture("cluster", sequence(
        exprLit,
        oneMore(captureGrouped("alts", sequence(
            arrow,
            capture("expr", filter(null, $(reference("choice")), parsingExpression)),
            oneMore(captureGrouped("annotations", exprAnnotation)))))))),

    rule = named$("rule", sequence(
        captureText("ruleName", name),
        zeroMore(captureSuffix),
        optional(capture("dumb", literal("!"))),
        optional(capture("token", literal("%"))),
        equal,
        choice(exprCluster, capture("expr", parsingExpression)),
        semi)),

    root = named$("grammar", oneMore(captureGrouped("rules", rule)));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final Grammar grammar = Autumn.grammarFromExpression(root);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
