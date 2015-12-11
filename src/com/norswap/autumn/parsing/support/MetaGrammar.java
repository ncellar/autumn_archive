package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.parsing.extensions.SyntaxExtension.Type.DECLARATION;
import static com.norswap.autumn.parsing.extensions.SyntaxExtension.Type.EXPRESSION;

import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.support.dynext.DynExtExtension;
import com.norswap.autumn.parsing.support.dynext.DynExtReader;
import com.norswap.autumn.parsing.support.dynext.DynRef;
import com.norswap.autumn.parsing.support.dynext.DynRefReader;

/**
 * This class holds the meta-grammar, i.e. the grammar of grammars (or more specifically of grammar
 * as specified inside grammar files like "Java8.autumn").
 * <p>
 * In addition to the grammar proper ({@link #get}, this class also defines the rules that make up
 * the grammar. Those can be incorporated in the syntax of syntactic extension (see {@link
 * SyntaxExtension}). However, it is not recommended to do so, except for references to {@link
 * #expr}.
 */
public final class MetaGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int i = 0;

    public final static ParsingExpression

    // TOKENS

    and         = ntoken("&"),
    bang        = ntoken("!"),
    equal       = ntoken("="),
    plus        = ntoken("+"),
    qMark       = ntoken("?"),
    colon       = ntoken(":"),
    semi        = ntoken(";"),
    slash       = ntoken("/"),
    pipe        = ntoken("|"),
    star        = ntoken("*"),
    tilda       = ntoken("~"),
    lBrace      = ntoken("{"),
    rBrace      = ntoken("}"),
    lParen      = ntoken("("),
    rParen      = ntoken(")"),
    underscore  = ntoken("_"),
    starPlus    = ntoken("*+"),
    plusPlus    = ntoken("++"),
    lAnBra      = ntoken("<"),
    rAnBra      = ntoken(">"),
    comma       = ntoken(","),
    commaPlus   = ntoken(",+"),
    minus       = ntoken("-"),
    hash        = ntoken("#"),
    dollar      = ntoken("$"),
    dot         = ntoken("."),
    percent     = ntoken("%"),
    hat         = ntoken("^"),
    backquote   = ntoken("`"),

    // NAMES AND LITERALS

    digit
        = charRange('0', '9'),

    hexDigit
        = choice(digit, charRange('a', 'f'), charRange('A', 'F')),

    letter
        = choice(charRange('a', 'z'), charRange('A', 'Z')),

    nameChar
        = choice(letter, digit, literal("_")),

    num
        = named$("num", token(oneMore(digit))),

    dropLit     = nKeyword("drop"),
    left_assoc  = nKeyword("left_assoc"),
    left_recur  = nKeyword("left_recur"),
    importLit   = nKeyword("import"),
    declLit     = nKeyword("decl"),

    reserved
        = choice(dropLit, left_assoc, left_recur, importLit, declLit),

    unicodeEscape
        = sequence(literal("\\u"), hexDigit, hexDigit, hexDigit, hexDigit),

    tabOrLineEscape
        = sequence(literal("\\"), charSet("tn")),

    anyEscape
        = sequence(literal("\\"), not(literal("u")), any()),

    escape
        = named$("escape", choice(unicodeEscape, tabOrLineEscape, anyEscape)),

    character
        = named$("character", choice(escape, notCharSet("\n\\"))),

    identifier
        = sequence(not(reserved), letter, zeroMore(nameChar)),

    escapedIdentifier
        = sequence(literal("'"), aloUntil(any(), literal("'"))),

    name
        = named$("name", token(choice(identifier, escapedIdentifier))),

    qualifiedName
        = named$("qualifiedName", aloSeparated(name, dot)),

    nameOrDollar
        = choice(captureText("name", name), capture("dollar", dollar)),

    // PARSING EXPRESSIONS

    range
        = named$("range", token(
            literal("["),
            captureText("first", character),
            literal("-"),
            captureText("last", character),
            literal("]"))),

    charSet
        = named$("charSet", token(
            literal("["),
            captureText("charSet", oneMore(
                not(literal("]")),
                character)),
            literal("]"))),

    notCharSet
        = named$("notCharSet", token(
            literal("^["),
            captureText("notCharSet", oneMore(
                not(literal("]")),
                character)),
            literal("]"))),

    stringLit
        = named$("stringLit", token(
            literal("\""),
            captureText("literal", zeroMore(not(literal("\"")), character)),
            literal("\""))),

    captureSuffix
        = capture($(group("captureSuffixes")), choice(
        capture("capture",
            token(sequence(literal(":"), optional(capture("captureText", literal("+")))))),
        capture("accessor",
            sequence(minus, nameOrDollar)),
        capture("group",
            sequence(hash, nameOrDollar)),
        capture("kind",
            sequence(tilda, nameOrDollar)))),

    expr = reference("parsingExpression"),

    // EXPRESSIONS

    parsingExpression
        = named$("parsingExpression", cluster(

        // NOTE(norswap)
        // Using left associativity for choice and sequence ensures that sub-expressions
        // must have higher precedence. This way, we avoid pesky choice of choices or
        // sequence of sequences.

        groupLeftAssoc(++i,
            named$("choice", capture("choice", aloSeparated(expr, slash)))),

        groupLeftAssoc(++i,
            named$("longestMatch", capture("longestMatch", aloSeparated(expr, pipe)))),

        groupLeftAssoc(++i,
            capture("sequence", sequence(expr, oneMore(expr)))),

        groupLeftRec(++i, // binary & capture
            capture("until", sequence(expr, starPlus, expr)),
            capture("aloUntil", sequence(expr, plusPlus, expr)),
            capture("separated", sequence(expr, comma, expr)),
            capture("aloSeparated", sequence(expr, commaPlus, expr)),
            capture("capture", sequence(
                choice(expr, capture("marker", dot)),
                oneMore(captureSuffix)))),

        group(++i, // prefix
            capture("and", sequence(and, expr)),
            capture("not", sequence(bang, expr)),
            capture("token", sequence(percent, expr)),
            capture("dumb", sequence(hat, expr))),

        group(++i, // suffix
            capture("optional", sequence(expr, qMark)),
            capture("zeroMore", sequence(expr, star, not(plus))),
            capture("oneMore", sequence(expr, plus, not(plus)))),

        group(++i, // primary
            sequence(lParen, exprDropPrecedence(expr), rParen),
            capture("drop", sequence(dropLit, expr)),
            captureText("ref", name),
            capture("any", underscore),
            capture("charRange", range),
            captureText("stringLit", stringLit),
            captureText("charSet", charSet),
            captureText("notCharSet", notCharSet),
            capture("customExpr", sequence(
                backquote,
                new DynRefReader(EXPRESSION, captureText("exprType", name)),
                lBrace,
                capture("custom", exprDropPrecedence(new DynRef())),
                rBrace))))),

        // TOP LEVEL DECLARATIONS

        lhs =
            named$("lhs", capture("lhs", sequence(
                captureText("ruleName", name),
                zeroMore(captureSuffix),
                optional(capture("dumb", hat)),
                optional(capture("token", percent)),
                equal))),

        rule =
            namekind("rule", sequence(
                lhs, capture("rhs", parsingExpression), semi)),

        customDecl =
            namekind("customDecl", sequence(
                declLit,
                new DynRefReader(DECLARATION, captureText("declType", name)),
                capture("custom", new DynRef()),
                semi)),

        decl =
            named$("decl", choice(rule, customDecl)),

        innport =
            named$("import", sequence(
                importLit,
                new DynExtReader(captureText(qualifiedName)),
                semi)),

        root =
            named$("grammar", sequence(
                group("imports", zeroMore(innport)),
                group("decls", oneMore(decl))));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression nKeyword(String string)
    {
        return named$(string, token(literal(string), not(nameChar)));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression keyword(String string)
    {
        return token(literal(string), not(nameChar));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a token matching the string literal, named after the literal.
     */
    public static ParsingExpression ntoken(String string)
    {
        return named$(string, token(string));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final Grammar get = Grammar
        .fromRoot(root)
        .withExtension(new DynExtExtension())
        .build();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
