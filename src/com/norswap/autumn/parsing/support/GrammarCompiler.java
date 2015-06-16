package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.expressions.DropPrecedence;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.Operand;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.Streams;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.util.StringEscape.unescape;

public final class GrammarCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    private interface Compiler extends Function<ParseTree, ParsingExpression> {}

    @FunctionalInterface
    private interface Grouper extends Function<ParsingExpression[], ParsingExpression> {}

    private static ParsingExpressionFactory F = new ParsingExpressionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Array<ParsingExpression> namedClusterAlternates = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression[] compile(ParseTree tree)
    {
        return new GrammarCompiler().run(tree);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public ParsingExpression[] run(ParseTree tree)
    {
        ParseTree rules = tree.group("rules");

        return Stream.concat(
                Streams.from(rules).map(this::compileRule),
                Streams.from(namedClusterAlternates))
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        String ruleName = rule.value("ruleName");
        ParsingExpression topChoice = compileTopChoice(rule.group("alts"));

        if (rule.has("dumb"))
        {
            topChoice = dumb(topChoice);
        }

        if (rule.has("token"))
        {
            topChoice = token(topChoice);
        }

        return named$(ruleName, topChoice);
    }


    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileChoiceAlt(ParseTree alt)
    {
        return alt.name.equals("sequence")
            ? compileSequence(alt)
            : compileExpression(alt);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileOneOrGroup(
        Compiler itemCompiler, Grouper grouper, ParseTree tree)
    {
        if (tree.childrenCount() == 1)
        {
            return itemCompiler.apply(tree.child());
        }
        else
        {
            return grouper.apply(Streams.from(tree)
                .map(itemCompiler)
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileTopChoice(ParseTree alts)
    {
        return compileOneOrGroup(
            t -> compileChoiceAlt(t.child(0)),
            exprs -> choice(exprs),
            alts);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileChoice(ParseTree choice)
    {
        return compileOneOrGroup(
            t -> compileChoiceAlt(t),
            exprs -> choice(exprs),
            choice);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileExpression(ParseTree expression)
    {
        final int UNSET = -1, MULTISET = -2, SET = 1;

        class $ {
            int precedence = 0;
            boolean leftRecur = false;
            boolean leftAssoc = false;
        }
        $ last = new $();

        Array<ParsingExpression> namedAlternates = new Array<>();

        ParsingExpression cluster = cluster(Streams.from(expression.group("alts"))
            .map(alt ->
            {
                ParsingExpression pe = compileSequence(alt.get("sequence"));
                Operand operand = new Operand();
                int precedence = UNSET;

                for (ParseTree annotation : alt.group("annotations"))
                {
                    annotation = annotation.child(0);

                    switch (annotation.name)
                    {
                        case "precedence":
                            precedence = precedence == -1
                                ? Integer.parseInt(annotation.value)
                                : MULTISET;
                            break;

                        case "increment":
                            precedence = precedence == -1
                                ? last.precedence + 1
                                : MULTISET;
                            break;

                        case "same":
                            precedence = precedence == -1
                                ? last.precedence
                                : MULTISET;
                            break;

                        case "left_assoc":
                            operand.leftRecursive = true;
                            operand.leftAssociative = true;
                            break;

                        case "left_recur":
                            operand.leftRecursive = true;
                            break;

                        case "name":
                            pe.setName(annotation.value);
                            namedAlternates.push(pe);
                            break;
                    }
                }

                if (precedence == 0)
                {
                    throw new RuntimeException(
                        "Precedence can't be 0. Don't use @0; or use @= in first position.");
                }

                if (precedence == UNSET)
                {
                    throw new RuntimeException(
                        "Expression alternate does not specify precedence.");
                }

                if (precedence == MULTISET)
                {
                    throw new RuntimeException(
                        "Expression specifies precedence more than once.");
                }

                operand.operand = pe;
                operand.precedence = precedence;

                if (precedence != last.precedence)
                {
                    last.leftAssoc = operand.leftAssociative;
                    last.leftRecur = operand.leftRecursive;
                    last.precedence = precedence;
                }
                else
                {
                    if (operand.leftAssociative)
                    {
                        throw new RuntimeException(
                            "@left_assoc annotation not on the item with its precedence.");
                    }

                    if (operand.leftRecursive)
                    {
                        throw new RuntimeException(
                            "@left_recur annotation not on the item with its precedence.");
                    }

                    operand.leftAssociative = last.leftAssoc;
                    operand.leftRecursive = last.leftRecur;
                }

                return operand;

            }).toArray(Operand[]::new));

        for (int i = 0; i < namedAlternates.size(); ++i)
        {
            namedClusterAlternates.push(namedAlternates.get(i));
        }

        return cluster;
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileSequence(ParseTree sequence)
    {
        return compileOneOrGroup(
            tree -> compilePrefixed(tree),
            exprs -> sequence(exprs),
            sequence);
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
                Reference ref = reference(primary.value("name"));

                ParseTree allowed = primary.getOrNull("allowed");
                ParseTree forbidden = primary.getOrNull("forbidden");

                if (allowed != null || forbidden != null)
                {
                    return filter(
                        allowed == null
                            ? new ParsingExpression[0]
                            : Streams.from(allowed)
                                .map(pe -> reference(pe.value))
                                .toArray(ParsingExpression[]::new),

                        forbidden == null
                            ? new ParsingExpression[0]
                            : Streams.from(forbidden)
                                .map(pe -> reference(pe.value))
                                .toArray(ParsingExpression[]::new),

                        ref
                    );
                }
                else {
                    return ref;
                }

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

            case "drop":
                DropPrecedence out = new DropPrecedence();
                out.operand = compilePrimary(primary.child(0));
                return out;

            default:
                throw new RuntimeException("Primary expression with no name.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
