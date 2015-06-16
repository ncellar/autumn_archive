package com.norswap.autumn.test.mouse;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Streams;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.util.StringEscape.unescape;

/**
 * Compiles Mouse grammars into parsing expressions.
 *  Not used for anything, but keeping it around just in case.
 *
 * Currently, vast overlap with {@link com.norswap.autumn.parsing.support.GrammarCompiler}.
 */
public final class MouseCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public ParsingExpression[] compile(ParseTree tree)
    {
        ParseTree rules = tree.group("rules");

        return Streams.from(rules)
            .map(this::compileRule)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        String ruleName = rule.value("ruleName");
        ParsingExpression topChoice = compileTopChoice(rule.group("alts"));

        return named$(ruleName, topChoice);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileTopChoice(ParseTree alts)
    {
        if (alts.childrenCount() == 1)
        {
            return compileSequence(alts.child().get("sequence"));
        }
        else
        {
            return choice(Streams.from(alts)
                .map(alt -> compileSequence(alt.get("sequence")))
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileChoice(ParseTree choice)
    {
        if (choice.childrenCount() == 1)
        {
            return compileSequence(choice.child());
        }
        else
        {
            return choice(Streams.from(choice)
                .map(alt -> compileSequence(alt))
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileSequence(ParseTree sequence)
    {
        if (sequence.childrenCount() == 1)
        {
            return compilePrefixed(sequence.child());
        }
        else
        {
            return sequence(Streams.from(sequence)
                .map(item -> compilePrefixed(item))
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePrefixed(ParseTree prefixed)
    {
        switch (prefixed.name)
        {
            case "and":
                return lookahead(compileSuffixed(prefixed.child()));

            case "not":
                return not(compileSuffixed((prefixed.child())));

            default:
                return compileSuffixed(prefixed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileSuffixed(ParseTree suffixed)
    {
        switch (suffixed.name)
        {
            case "until":
                return until(
                    compilePrimary(suffixed.child(0)),
                    compilePrimary(suffixed.child(1)));

            case "aloUntil":
                return aloUntil(
                    compilePrimary(suffixed.child(0)),
                    compilePrimary(suffixed.child(1)));

            case "optional":
                return optional(compilePrimary(suffixed.child()));

            case "zeroMore":
                return zeroMore(compilePrimary(suffixed.child()));

            case "oneMore":
                return oneMore(compilePrimary(suffixed.child()));

            default:
                return compilePrimary(suffixed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePrimary(ParseTree primary)
    {
        switch (primary.name)
        {
            case "choice":
                return compileChoice(primary);

            case "ref":
                return reference(primary.value);

            case "any":
                return any();

            case "charRange":
                return charRange(
                    unescape(primary.value("first")).charAt(0),
                    unescape(primary.value("last")).charAt(0));

            case "charSet":
                return charSet(unescape(primary.value("charSet")));

            case "notCharSet":
                return notCharSet(unescape(primary.value("notCharSet")));

            case "stringLit":
                return literal(unescape(primary.value("literal")));

            default:
                throw new RuntimeException("Primary expression with no name.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

