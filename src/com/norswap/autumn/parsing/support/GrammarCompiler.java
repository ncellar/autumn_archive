package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.Success;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.capture.Decorate;
import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.extensions.cluster.expressions.Filter;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.support.dynext.DynExtState;
import com.norswap.util.Array;
import com.norswap.util.JArrays;

import java.util.HashMap;
import java.util.List;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.util.StringEscape.unescape;

/**
 * This class is used to compile the parse tree resulting from the parse of a grammar file into a
 * list of rules to be used in a grammar.
 * <p>
 * The syntax of grammar files is defined in {@link MetaGrammar}.
 * <p>
 * Compilation is invoked through the {@link #compile} static entry point. This create an instance
 * of this class which forms the context of the compilation. This instance can be accessed by the
 * compiler for syntactic extensions ({@link SyntaxExtension#compile}), which can manipulate the
 * public fields and call the public methods.
 */
public final class GrammarCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The list of all rules defined in the grammar file.
     */
    public final Array<ParsingExpression> rules = new Array<>();

    /**
     * After the compilation finishes, holds the root of the compiled grammar.
     */
    public ParsingExpression root;

    /**
     * After the compilation finishes, holds the whitespace expression of the compiled grammar.
     */
    public ParsingExpression whitespace;

    /**
     * A map where syntactic extension can read/write custom data.
     */
    public final HashMap<String, Object> context = new HashMap<>();

    /**
     * The dynamic extension state, which lists all registered extensions and their syntactic
     * extensions.
     */
    private final DynExtState destate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GrammarCompiler(DynExtState destate)
    {
        this.destate = destate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static GrammarCompiler compile(ParseTree tree, DynExtState destate)
    {
        GrammarCompiler compiler = new GrammarCompiler(destate);
        compiler.run(tree);

        compiler.root = compiler.rules.first();

        compiler.whitespace = compiler.rules
            .first(rule -> "Spacing".equals(rule.name));

        if (compiler.whitespace == null)
            compiler.whitespace = Whitespace.DEFAULT();

        return compiler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     * <p>
     * Note that the references inside these expressions are not resolved.
     */
    private Array<ParsingExpression> run(ParseTree tree)
    {
        // We don't process imports, as they have already been at parse-time.

        tree.group("decls").forEach(this::compileDeclaration);
        return rules;
    }

    // ---------------------------------------------------------------------------------------------

    private void compileDeclaration(ParseTree declaration)
    {
        switch (declaration.kind)
        {
            case "rule":
                compileRule(declaration);
                break;

            case "customDecl":
                compileCustomDecl(declaration);
                break;

            default:
                error("Unknown top-level declaration: %s", declaration);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void compileRule(ParseTree rule)
    {
        ParseTree lhs = rule.get("lhs");
        ParseTree rhs = rule.get("rhs");
        // TODO don't switch on accessor, don't nest captures
        ParsingExpression pe = decorateRule(lhs, compilePE(rhs.child()));
        rules.add(pe);
    }

    // ---------------------------------------------------------------------------------------------

    private void compileCustomDecl(ParseTree customDecl)
    {
        SyntaxExtension ext = destate.declSyntaxes.get(customDecl.value("declType"));
        ext.compile(this, customDecl.get("custom"));
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression decorateRule(ParseTree lhs, ParsingExpression pe)
    {
        if (lhs.has("dumb"))
            pe = dumb(pe);

        if (lhs.has("token"))
            pe = token(pe);

        String ruleName = lhs.value("ruleName");
        List<ParseTree> captureSuffixes = lhs.group("captureSuffixes");

        return captureSuffixes.isEmpty()
            ? named$(ruleName, pe)
            : named$(ruleName, compileCapture(ruleName, pe, captureSuffixes));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String name(String name, ParsingExpression expr, ParseTree suffix)
    {
        if (suffix.has("name"))
            return suffix.value("name");

        // Else there is a dollar instead of a name.
        // Either this qualifies a rule ...

        if (name != null)
            return name;

        // ... or a reference

        if (expr == null)
            error("Dollar ($) capture name used in conjuction with a marker.");

        if (!(expr instanceof Reference))
            error("Dollar ($) capture name is a suffix of something which is not an identifier");

        return ((Reference)expr).target;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The name field is for rule names, if this is a capture over a rule definition.
     * <p>
     * If the child field is null, this is a marker capture.
     */
    private ParsingExpression compileCapture(
        String ruleName,
        ParsingExpression child,
        List<ParseTree> suffixes)
    {
        // A marker implies capture!
        boolean capture = child == null;
        boolean captureText = false;

        int accessors = 0;
        boolean first = true;
        Array<Decorate> decorations = new Array<>();

        for (ParseTree suffix: suffixes)
        {
            suffix = suffix.child();

            switch (suffix.accessor)
            {
                case "capture":
                    if (!first)
                    {
                        error("Capture suffix (:) not appearing as first suffix.");
                    }
                    capture = true;
                    captureText = suffix.has("captureText");
                    break;

                case "accessor":
                    decorations.add(accessor(name(ruleName, child, suffix)));
                    ++accessors;
                    break;

                case "group":
                    decorations.add(group(name(ruleName, child, suffix)));
                    ++accessors;
                    break;

                case "kind":
                    decorations.add(kind(name(ruleName, child, suffix)));
                    break;

                default:
                    error("Unknown capture type: %s", suffix.accessor);
            }

            first = false;
        }

        if (accessors > 1)
            error("More than one accessor or group specification.");

        return new Capture(
            capture,
            captureText,
            child == null ? new Success() : child,
            decorations.toArray(Decorate[]::new));
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression[] compileChildren(ParseTree tree)
    {
        ParseTree[] children = tree.children();
        return JArrays.map(children, new ParsingExpression[children.length], this::compilePE);
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression compilePE(ParseTree tree)
    {
        ParseTree child;
        ParsingExpression childPE;

        switch (tree.accessor)
        {
            case "choice":
                return choice(compileChildren(tree));

            case "longestMatch":
                return longestMatch(compileChildren(tree));

            case "sequence":
                return sequence(compileChildren(tree));

            case "and":
                return lookahead(compilePE(tree.child()));

            case "not":
                return not(compilePE(tree.child()));

            case "token":
                return token(compilePE(tree.child()));

            case "dumb":
                return dumb(compilePE(tree.child()));

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
                child = tree.child(0);
                childPE = child.accessor.equals("marker") ? null : compilePE(child);
                return compileCapture(null, childPE, tree.group("captureSuffixes"));

            case "drop":
                return exprDropPrecedence(compilePE(tree.child()));

            case "ref":
                return reference(tree.value);

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

            case "customExpr":
                SyntaxExtension ext = destate.exprSyntaxes.get(tree.value("exprType"));
                Object out = ext.compile(this, tree.get("custom"));

                if (!(out instanceof ParsingExpression))
                    error("Expression grammar syntax extension did not compile to a %s but to a %s",
                        ParsingExpression.class.getName(),
                        out.getClass().getName());

                return (ParsingExpression) out;

            default:
                error("Parsing expression with unknown name: %s", tree.accessor);
                return null; // unreachable
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void error(String format, Object... items)
    {
        for (int i = 0; i < items.length; ++i)
        {
            if (items[i] instanceof ParseTree)
                items[i] = ((ParseTree) items[i]).nodeToString();
        }

        throw new RuntimeException(String.format(format, items));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
