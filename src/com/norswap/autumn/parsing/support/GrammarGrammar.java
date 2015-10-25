package com.norswap.autumn.parsing.support;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class GrammarGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int i = 0;

    public static ParsingExpression

    // TOKENS

    and         = token("&"),
    bang        = token("!"),
    equal       = token("="),
    plus        = token("+"),
    qMark       = token("?"),
    colon       = token(":"),
    semi        = token(";"),
    slash       = token("/"),
    star        = token("*"),
    tilda       = token("~"),
    lBrace      = token("{"),
    rBrace      = token("}"),
    lParen      = token("("),
    rParen      = token(")"),
    underscore  = token("_"),
    starPlus    = token("*+"),
    plusPlus    = token("++"),
    arrow       = token("->"),
    lAnBra      = token("<"),
    rAnBra      = token(">"),
    comma       = token(","),
    commaPlus   = token(",+"),
    minus       = token("-"),
    hash        = token("#"),
    dollar      = token("$"),
    dot         = token("."),
    percent     = token("%"),
    hat         = token("^"),

    // NAMES AND LITERALS

    digit       = charRange('0', '9'),

    hexDigit    = choice(
                    digit,
                    charRange('a', 'f'),
                    charRange('A', 'F')),

    letter      = choice(
                    charRange('a', 'z'),
                    charRange('A', 'Z')),

    nameChar    = choice(
                    letter,
                    digit,
                    literal("_")),

    num         = token(oneMore(digit)),

    exprLit     = keyword("expr"),

    dropLit     = keyword("drop"),

    left_assoc  = keyword("left_assoc"),

    left_recur  = keyword("left_recur"),

    reserved    = choice(
                    exprLit,
                    dropLit,
                    left_assoc,
                    left_recur),

    escape      = named$("escape", choice(
                    sequence(
                        literal("\\u"),
                        hexDigit,
                        hexDigit,
                        hexDigit,
                        hexDigit),
                    sequence(
                        literal("\\"),
                        charSet("tn")),
                    sequence(
                        not(literal("\\u")),
                        literal("\\"),
                        any()))),

    character   = named$("character", choice(
                    escape,
                    notCharSet("\n\\"))),

    name        = named$("name", token(choice(
                    sequence(
                        not(reserved),
                        letter,
                        zeroMore(nameChar)),
                    sequence(
                        literal("'"),
                        aloUntil(any(), literal("'")))))),

    nameOrDollar = choice(
                    captureText("name", name),
                    capture("dollar", dollar)),

    // PARSING EXPRESSIONS

    range       = named$("range", token(
                    literal("["),
                    captureText("first", character),
                    literal("-"),
                    captureText("last", character),
                    literal("]"))),

    charSet     = named$("charSet", token(
                    literal("["),
                    captureText("charSet", oneMore(
                        not(literal("]")),
                        character)),
                    literal("]"))),

    notCharSet  = named$("notCharSet", token(
                    literal("^["),
                    captureText("notCharSet", oneMore(not(literal("]"), character))),
                    literal("]"))),

    stringLit   = named$("stringLit", token(
                    literal("\""),
                    captureText("literal", zeroMore(not(literal("\"")), character)),
                    literal("\""))),

    reference   = sequence(
                    captureText("name", name),
                    optional(
                        token("allow"),
                        lBrace,
                        aloSeparated(captureTextGrouped("allowed", name), comma),
                        rBrace),
                    optional(
                        token("forbid"),
                        lBrace,
                        aloSeparated(captureTextGrouped("forbidden", name), comma),
                        rBrace)),

    captureSuffix
                = group$("captureSuffixes", capture(choice(
                    capture("capture",
                        token(literal(":"), optional(capture("captureText", literal("+"))))),
                    capture("accessor",
                        sequence(minus, nameOrDollar)),
                    capture("group",
                        sequence(hash, nameOrDollar)),
                    capture("tag",
                        sequence(tilda, nameOrDollar))))),

    expr = reference("expr"),

    parsingExpression
                = named$("expr", cluster(

                    // NOTE(norswap)
                    // Using left associativity for choice and sequence ensures that sub-expressions
                    // must have higher precedence. This way, we avoid pesky choice of choices or
                    // sequence of sequences.

                    groupLeftAssoc(++i,
                        named$("choice", capture("choice", aloSeparated(expr, slash)))),

                    groupLeftAssoc(++i,
                        capture("sequence", sequence(expr, oneMore(expr)))),

                    groupLeftRec(++i, // binary & capture
                        capture("until",        sequence(expr, starPlus, expr)),
                        capture("aloUntil",     sequence(expr, plusPlus, expr)),
                        capture("separated",    sequence(expr, comma, expr)),
                        capture("aloSeparated", sequence(expr, commaPlus, expr)),
                        capture("capture",      sequence(
                                                    choice(expr, capture("marker", dot)),
                                                    oneMore(captureSuffix)))),

                    group(++i, // prefix
                        capture("and",   sequence(and, expr)),
                        capture("not",   sequence(bang, expr)),
                        capture("token", sequence(percent, expr)),
                        capture("dumb",  sequence(hat, expr))),

                    group(++i, // suffix
                        capture("optional", sequence(expr, qMark)),
                        capture("zeroMore", sequence(expr, star, not(plus))),
                        capture("oneMore",  sequence(expr, plus, not(plus)))),

                    group(++i, // primary
                        sequence(lParen, exprDropPrecedence(expr), rParen),
                        capture("drop", sequence(dropLit, expr)),
                        capture("ref", reference),
                        capture("any", underscore),
                        capture("charRange", range),
                        captureText("stringLit", stringLit),
                        captureText("charSet", charSet),
                        captureText("notCharSet", notCharSet)))),

        exprAnnotation
                = sequence(
                    literal("@"),
                    choice(
                        captureText("precedence", num),
                        capture("increment", plus),
                        capture("same", equal),
                        capture("left_assoc", left_assoc),
                        capture("left_recur", left_recur),
                        captureText("name", name))),

        // TOP LEVEL

        exprCluster
                = named$("cluster", capture("cluster", sequence(
                    exprLit,
                    oneMore(captureGrouped("alts", sequence(
                        arrow,
                        capture("expr", filter(null, $(reference("choice")), parsingExpression)),
                        zeroMore(captureGrouped("annotations", exprAnnotation)))))))),

        rule    = named$("rule", sequence(
                    captureText("ruleName", name),
                    zeroMore(captureSuffix),
                    optional(capture("dumb", hat)),
                    optional(capture("token", percent)),
                    equal,
                    choice(
                        exprCluster,
                        capture("expr", parsingExpression)),
                    semi)),

        root    = named$("grammar", oneMore(captureGrouped("rules", rule)));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static ParsingExpression keyword(String string)
    {
        return token(literal(string), not(nameChar));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final Grammar grammar = Grammar.fromRoot(root).build();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
