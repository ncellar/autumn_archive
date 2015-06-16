package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class GrammarGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
        The original Mouse grammar this is based on.

        Grammar   = Space (Rule/Skip)*+ EOF ;
        Rule      = Name EQUAL RuleRhs DiagName? SEMI ;
        Skip      = SEMI / _++ (SEMI/EOF) ;
        RuleRhs   = Sequence Actions (SLASH Sequence Actions)* ;
        Choice    = Sequence (SLASH Sequence)* ;
        Sequence  = Prefixed+ ;
        Prefixed  = PREFIX? Suffixed ;
        Suffixed  = Primary (UNTIL Primary / SUFFIX)? ;
        Primary   = Name
                  / LPAREN Choice RPAREN
                  / ANY
                  / StringLit
                  / Range
                  / CharClass ;
        Actions   = OnSucc OnFail ;
        OnSucc    = (LWING AND? Name? RWING)? ;
        OnFail    = (TILDA LWING Name? RWING)? ;
        Name      = Letter (Letter / Digit)* Space ;
        DiagName  = "<" Char++ ">" Space ;
        StringLit = ["] Char++ ["] Space ;
        CharClass = ("[" / "^[") Char++ "]" Space ;
        Range     = "[" Char "-" Char "]" Space ;
        Char      = Escape / ^[\r\n\\] ;
        Escape    = "\\u" HexDigit HexDigit HexDigit HexDigit
                  / "\\t"
                  / "\\n"
                  / "\\r"
                  / !"\\u""\\"_ ;
        Letter    = [a-z] / [A-Z] ;
        Digit     = [0-9] ;
        HexDigit  = [0-9] / [a-f] / [A-F] ;
        PREFIX    = [&!]  Space ;
        SUFFIX    = [?*+] Space ;
        UNTIL     = ("*+" / "++") Space ;
        EQUAL     = "=" Space ;
        SEMI      = ";" Space ;
        SLASH     = "/" Space ;
        AND       = "&" Space ;
        LPAREN    = "(" Space ;
        RPAREN    = ")" Space ;
        LWING     = "{" Space ;
        RWING     = "}" Space ;
        TILDA     = "~" Space ;
        ANY       = "_" Space ;
        Space     = ([ \r\n\t] / Comment)* ;
        Comment   = "//" _*+ EOL ;
        EOL       = [\r]? [\n] / !_  ;
        EOF       = !_  ;
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO(norswap):
    //   This should eventually use expression clusters, the grammar just precedes
    //   that, and I haven't gotten around changing it.

    public static ParsingExpression

    and         = token(literal("&")),
    bang        = token(literal("!")),
    equal       = token(literal("=")),
    plus        = token(literal("+")),
    qMark       = token(literal("?")),
    semi        = token(literal(";")),
    slash       = token(literal("/")),
    star        = token(literal("*")),
    tilda       = token(literal("_")),
    lBrace      = token(literal("{")),
    rBrace      = token(literal("}")),
    lParen      = token(literal("(")),
    rParen      = token(literal(")")),
    underscore  = token(literal("_")),
    until       = token(literal("*+")),
    aloUntil    = token(literal("++")),
    colEqual    = token(literal(":=")),
    arrow       = token(literal("->")),
    lAnBra      = token(literal("<")),
    rAnBra      = token(literal(">")),
    comma       = token(literal(",")),

    digit         = charRange('0', '9'),
    hexDigit      = choice(digit, charRange('a', 'f'), charRange('A', 'F')),
    letter        = choice(charRange('a', 'z'), charRange('A', 'Z')),
    nameChar      = choice(letter, digit, literal("_")),

    num           = token(oneMore(digit)),

    exprLit       = token(literal("expr"), not(nameChar)),
    dropLit       = token(literal("drop"), not(nameChar)),
    left_assoc    = token(literal("left_assoc"), not(nameChar)),
    left_recur    = token(literal("left_recur"), not(nameChar)),

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

    diagName    = named$("diagName", token(literal("<"), until(character, literal(">")))),
    name        = named$("name", token(choice(
                    sequence(letter, zeroMore(nameChar)),
                    sequence(literal("'"), aloUntil(any(), literal("'")))))),

    primary = recursive$("primary", choice(
        sequence(lParen, reference("choice"), rParen),
        capture("drop", sequence(dropLit, reference("primary"))),
        sequence(capture("ref", sequence(
            captureText("name", name),
            optional(token(literal("allow")),
                lBrace, aloSeparated(captureTextGrouped("allowed", name), comma), rBrace),
            optional(token(literal("forbid")),
                lBrace, aloSeparated(captureTextGrouped("forbidden", name), comma), rBrace)))),
        capture("any", underscore),
        capture("charRange", range),
        captureText("stringLit", stringLit),
        captureText("charSet", charSet),
        captureText("notCharSet", notCharSet))),

    suffixed = named$("suffixed", choice(
        capture("until", sequence(primary, until, primary)),
        capture("aloUntil", sequence(primary, aloUntil, primary)),
        capture("optional", sequence(primary, qMark)),
        capture("zeroMore", sequence(primary, star)),
        capture("oneMore", sequence(primary, plus)),
        primary)),

    prefixed = named$("prefixed", choice(
        capture("and", sequence(and, suffixed)),
        capture("not", sequence(bang, suffixed)),
        suffixed)),

    sequence = named$("sequence", capture("sequence", oneMore(prefixed))),

    exprAnnotation = sequence(literal("@"),
        choice(
            captureText("precedence", num),
            capture("increment", plus),
            capture("same", equal),
            capture("left_assoc", left_assoc),
            capture("left_recur", left_recur),
            captureText("name", name))),

    expr = named$("expr", capture("expr", sequence(
        exprLit,
        oneMore(captureGrouped("alts", sequence(
            arrow, sequence,
            oneMore(captureGrouped("annotations", exprAnnotation)))))))),

    choice = recursive$("choice", capture("choice", aloSeparated(
        choice(expr, sequence),
        slash))),

    ruleRhs = named$("ruleRhs", aloSeparated(
        captureGrouped("alts", choice(expr, sequence)),
        slash)),

    rule = named$("rule", sequence(
        captureText("ruleName", name),
        optional(capture("dumb", literal("!"))),
        choice(equal, capture("token", colEqual)),
        ruleRhs,
        optional(diagName), semi)),

    grammar = named$("grammar", oneMore(captureGrouped("rules", rule)));
}
