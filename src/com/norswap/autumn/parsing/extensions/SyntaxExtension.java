package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursionVisitor;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.MetaGrammar;
import com.norswap.util.annotations.Immutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enables extensions to extend the syntax of grammar files, as part of an {@link Extension}.
 * <p>
 * Currently two types of extensions are supported. First, custom top-level declarations, which take
 * the following form:
 * <pre>{@code
 * decl <name> <custom_syntax> ;
 * }</pre>
 * Note that the custom syntax cannot contain semicolons.
 * <p>
 * Second, custom expressions, which take one the following two forms:
 * <pre>{@code
 * 1) `name
 * 2) `<name> { <custom_syntax> }
 * }</pre>
 * Note that the custom syntax cannot contain closing braces.
 * <p>
 * A syntactic extension must supply the type of extension, the name, a parsing expression defining
 * the custom syntax part, and a compiler that is called whenever these custom constructs are
 * encountered. For custom expression, it returns a {@link ParsingExpression}.
 * <p>
 * The syntax of an extension will get its references resolved (both internal and to rules within
 * {@link MetaGrammar}. By default, it won't get automatic left-recursion handling as the
 * operation is potentially costly. This handling can however be requested by calling the 4-args
 * constructor.
 * <p>
 * Syntax extensions are immutable and creating them can be expensive, so be sure to cache them
 * within your extensions!
 */
@Immutable
public abstract class SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * See {@link SyntaxExtension}.
     */
    public enum Type { DECLARATION, EXPRESSION }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The type of syntactic extension described. See {@link SyntaxExtension}.
     */
    public final Type type;

    /**
     * The syntactic extension's name, which is used to mark its occurences in grammar files (see
     * {@link SyntaxExtension}.
     */
    public final String name;

    /**
     * A parsing expression describing the syntax of extension.
     */
    public final ParsingExpression syntax;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SyntaxExtension(Type type, String name, ParsingExpression syntax)
    {
        this(type, name, syntax, false);
    }

    // ---------------------------------------------------------------------------------------------

    public SyntaxExtension(
        Type type,
        String name,
        ParsingExpression syntax,
        boolean leftRecursionHandling)
    {
        // Resolve references in the syntax parsing expression.

        // This check is necessary to avoid a cyclic dependency problem between the meta-grammar
        // and the default syntax extensions. So default extensions must not reference meta-grammar
        // rules by name (they use direct references anyway!).
        Map<String, ParsingExpression> rules = MetaGrammar.get != null
            ? MetaGrammar.get.getRules()
            : new HashMap<>();

        syntax = new ReferenceResolver(rules).visit(syntax);

        if (leftRecursionHandling)
        {
            // Automatically break left-recursive cycles.

            NullabilityCalculator calc = new NullabilityCalculator();
            calc.visit(syntax);
            LeftRecursionVisitor lrHandler = new LeftRecursionVisitor(true, calc, rules.values());
            syntax = lrHandler.visit(syntax);
        }

        this.type = type;
        this.name = name;
        this.syntax = syntax;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Compiles the syntactic extension. The result depends on the type of extension: for custom
     * parsing expressions it must be a {@link ParsingExpression}, for declarations it is ignored.
     * <p>
     * To context object can be used to read/write information local to the parse. It is shared
     * with other objects, so keys should be uniquely prefixed.
     */
    public abstract Object compile(
        GrammarCompiler compiler,
        ParseTree tree);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
