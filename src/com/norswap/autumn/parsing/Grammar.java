package com.norswap.autumn.parsing;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.MetaGrammar;
import com.norswap.autumn.parsing.support.dynext.DynExtExtension;
import com.norswap.autumn.parsing.support.dynext.DynExtState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Immutable;
import com.norswap.util.graph.GraphVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A user-facing representation of the grammar, which is the union of a parsing expression and some
 * options.
 * <p>
 * Convenient factory methods are available in class {@link Autumn}.
 */
public final class Grammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The parsing expression that defines the grammar.
     */
    public final ParsingExpression root;

    /**
     * The rules contained within the grammar. These usually have a name, and usually include the
     * root (and sometimes the whitespace); but those are not absolute requirements. This is never
     * null but can be empty. The iteration order must be consistent.
     * <p>
     * For grammar created from grammar files, these are all the rules defined within the grammar
     * file (so there won't be a rule for the whitespace if the default whitespace specification is
     * used).
     */
    public final @Immutable Collection<ParsingExpression> rules;

    /**
     * The parsing expression to use as whitespace (used for whitespace expressions, and after
     * token expressions).
     */
    public final ParsingExpression whitespace;

    /**
     * Whether leading whitespace should be skipped when parsing.
     */
    public final boolean processLeadingWhitespace;

    /**
     * Extensions used by the grammar.
     */
    public final @Immutable Array<Extension> extensions;

    /**
     * Build on-demand for {@link #getRule}.
     */
    private Map<String, ParsingExpression> rulesByName;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Grammar(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace,
        Array<Extension> extensions)
    {
        this.root = root;
        this.rules = rules;
        this.whitespace = whitespace;
        this.processLeadingWhitespace = processLeadingWhitespace;
        this.extensions = extensions;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static GrammarBuilder fromSource(Source source)
    {
        return new GrammarBuilder(source);
    }

    // ---------------------------------------------------------------------------------------------

    public static GrammarBuilder fromRoot(ParsingExpression root)
    {
        return new GrammarBuilder(root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the rule with the given name, if any, null otherwise.
     */
    public ParsingExpression getRule(String name)
    {
        if (rulesByName == null)
            getRules();

        return rulesByName.get(name);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a map from rule name to rules. The returned map backs the {@link #getRule} and should
     * not be modified.
     */
    public Map<String, ParsingExpression> getRules()
    {
        if (rulesByName == null)
        {
            rulesByName = new HashMap<>();

            rules.forEach(
                pe -> {
                    String key = pe.name;

                    if (key != null)
                    {
                        rulesByName.put(key, pe);
                    }
                });
        }

        return rulesByName;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Runs the given visitor over the rules, root and whitespace (in that order) of the grammar.
     * Note that all of these expressions are visited as part of the same visit.
     */
    public void compute(GraphVisitor<ParsingExpression> visitor)
    {
        visitor.partialVisit(root);
        visitor.partialVisit(rules);
        visitor.partialVisit(whitespace);
        visitor.conclude();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
