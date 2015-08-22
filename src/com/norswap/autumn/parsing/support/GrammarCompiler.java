package com.norswap.autumn.parsing.support;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.Group;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.util.Array;
import com.norswap.util.Counter;
import com.norswap.util.Streams;

import java.util.List;
import java.util.function.Function;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.util.StringEscape.unescape;

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

    public static Grammar compile(ParseTree tree)
    {
        Array<ParsingExpression> exprs = new GrammarCompiler().run(tree);

        ParsingExpression whitespace = exprs.stream()
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().orElse(Whitespace.DEFAULT());

        // TODO enable setting whitespace & root from grammar file

        return Autumn.grammarFromExpression(
            exprs.get(0), exprs, whitespace, true, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public Array<ParsingExpression> run(ParseTree tree)
    {
        Array<ParsingExpression> out = new Array<>(
            Streams.from(tree.group("rules"))
                .map(this::compileRule).toArray(ParsingExpression[]::new));

        out.addAll(namedClusterAlternates);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        String ruleName = rule.value("ruleName");

        ParsingExpression topChoice = rule.has("cluster")
            ? compileCluster(rule.get("cluster"))
            : compilePE(rule.get("expr").child());

        if (rule.has("dumb"))
        {
            topChoice = dumb(topChoice);
        }

        if (rule.has("token"))
        {
            topChoice = token(topChoice);
        }

        topChoice = compileCapture(topChoice, rule.group("captureSuffixes"));

        return named$(ruleName, topChoice);
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

    private ParsingExpression compileCluster(ParseTree expression)
    {
        final int UNSET = -1;

        Counter currentPrecedence = new Counter(0);
        Array<ParsingExpression> namedAlternates = new Array<>();
        Array<Group> groups = new Array<>();
        Array<Array<ParsingExpression>> alts = new Array<>();

        for (ParseTree alt: expression.group("alts"))
        {
            ParsingExpression pe = compilePE(alt.get("expr").child());

            int precedence = UNSET;
            int psets = 0;
            boolean leftRecursive = false;
            boolean leftAssociative = false;

            for (ParseTree annotation : alt.group("annotations"))
            {
                annotation = annotation.child();

                switch (annotation.accessor)
                {
                    case "precedence":
                        precedence = Integer.parseInt(annotation.value);
                        ++psets;
                        break;

                    case "increment":
                        precedence = currentPrecedence.i + 1;
                        ++psets;
                        break;

                    case "same":
                        precedence = currentPrecedence.i;
                        ++psets;
                        break;

                    case "left_assoc":
                        leftRecursive = true;
                        leftAssociative = true;
                        break;

                    case "left_recur":
                        leftRecursive = true;
                        break;

                    case "name":
                        pe.setName(annotation.value);
                        namedAlternates.push(pe);
                        break;
                }
            }

            if (psets > 1)
            {
                throw new RuntimeException(
                    "Expression specifies precedence more than once.");
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

            if (precedence < currentPrecedence.i)
            {
                throw new RuntimeException(
                    "Alternates must be grouped by precedence in expression cluster.");
            }
            else if (precedence == currentPrecedence.i)
            {
                if (leftRecursive)
                {
                    throw new RuntimeException(
                        "Can't specify left-recursion or left-associativity on non-first "
                            + "alternate of a precedence group in an expression cluster.");
                }

                alts.get(precedence).push(pe);
            }
            else
            {
                groups.put(precedence, group(precedence, leftRecursive, leftAssociative, (Group[]) null));
                alts.put(precedence, new Array<>(pe));
                ++currentPrecedence.i;
            }
        }

        namedAlternates.forEach(namedClusterAlternates::push);

        // Build the groups

        Array<Group> groupsArray = new Array<>();

        for (int i = 0; i < groups.size(); ++i)
        {
            Group group = groups.get(i);

            if (group == null) {
                continue;
            }

            group.operands = alts.get(i).toArray(ParsingExpression[]::new);
            groupsArray.push(group);
        }

        return cluster(groupsArray.toArray(Group[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression compileRef(ParseTree tree)
    {
        Reference ref = reference(tree.value("name"));

        ParseTree allowed = tree.getOrNull("allowed");
        ParseTree forbidden = tree.getOrNull("forbidden");

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
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileCapture(ParsingExpression child, List<ParseTree> suffixes)
    {
        ParsingExpression out = child;

        for (ParseTree suffix: suffixes)
        {
            suffix = suffix.child();

            switch (suffix.accessor)
            {
                case "capture":
                    out = capture(suffix.has("captureText"), out);
                    break;

                case "accessor":
                    out = accessor$(suffix.value("name"), out);
                    break;

                case "group":
                    out = group$(suffix.value("name"), out);
                    break;

                case "tag":
                    out = tag$(suffix.value("name"), out);
                    break;

                default:
                    throw new RuntimeException("Unknown capture type: " + suffix.accessor);
            }
        }

        return out;

    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression[] compileChildren(ParseTree tree)
    {
        return tree.children.stream()
            .map(this::compilePE)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePE(ParseTree tree)
    {
        switch (tree.accessor)
        {
            case "choice":
                return choice(compileChildren(tree));

            case "sequence":
                return sequence(compileChildren(tree));

            case "and":
                return lookahead(compilePE(tree.child()));

            case "not":
                return not(compilePE(tree.child()));

            case "until":
                return until(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloUntil":
                return aloUntil(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "separated":
                return separated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloSeparated":
                return aloSeparated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "optional":
                return optional(compilePE(tree.child()));

            case "zeroMore":
                return zeroMore(compilePE(tree.child()));

            case "oneMore":
                return oneMore(compilePE(tree.child()));

            case "capture":
                return compileCapture(compilePE(tree.child(0)), tree.group("captureSuffixes"));

            case "drop":
                return exprDropPrecedence(compilePE(tree.child()));

            case "ref":
                return compileRef(tree);

            case "any":
                return any();

            case "charRange":
                return charRange(
                    unescape(tree.value("first")).charAt(0),
                    unescape(tree.value("last")).charAt(0));

            case "charSet":
                return charSet(unescape(tree.value("charSet")));

            case "notCharSet":
                return notCharSet(unescape(tree.value("notCharSet")));

            case "stringLit":
                return literal(unescape(tree.value("literal")));

            default:
                throw new RuntimeException("Parsing expression with unknown name: " + tree.accessor);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
