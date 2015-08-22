package com.norswap.autumn.parsing;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.slot.Slot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A user-facing representation of the grammar, which is the union of a parsing
 * expression and some options.
 *
 * Convenient factory methods are available in class {@link Autumn}.
 */
public final class Grammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression root;

    private ParsingExpression whitespace;

    private boolean processLeadingWhitespace;

    private Collection<ParsingExpression> rules;

    private Map<String, ParsingExpression> rulesByName;

    private NullabilityCalculator nullabilityCalculator;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * See the documentation of getters.
     *
     * {@code rules} is the set of rules in the grammar (including the root), can be null if
     * only the root is to be considered.
     */
    public Grammar(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace)
    {
        this.root = root;
        this.rules = rules == null ? Collections.emptyList() : rules;
        this.whitespace = whitespace;
        this.processLeadingWhitespace = processLeadingWhitespace;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The parsing expression that defines the grammar.
     */
    public ParsingExpression root()
    {
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The parsing expression to use as whitespace (used for whitespace expressions, and after
     * token expressions).
     */
    public ParsingExpression whitespace()
    {
        return whitespace;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Whether leading whitespace should be skipped when parsing.
     */
    public boolean processLeadingWhitespace()
    {
        return processLeadingWhitespace;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The rules contained within the grammar. These usually have a name, and usually include the
     * root (and sometimes the whitespace); but those are not absolute requirements. This is never
     * null be can be empty.
     *
     * For grammar created from grammar files, these are all the rules defined within the grammar
     * file (so there won't be a rule for the whitespace if the default whitespace specification is
     * used).
     */
    public Collection<ParsingExpression> rules()
    {
        return rules;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the rule with the given name, if any, null otherwise.
     */
    public ParsingExpression getRule(String name)
    {
        if (rules == null)
        {
            return null;
        }

        if (rulesByName == null)
        {
            rulesByName = new HashMap<>();

            rules.forEach(
            pe -> {
                String key = pe.name();

                if (key != null)
                {
                    rulesByName.put(pe.name(), pe);
                }
            });
        }

        return rulesByName.get(name);
    }

    // ---------------------------------------------------------------------------------------------

    public Grammar walk(GraphVisitor<ParsingExpression> visitor)
    {
        Slot<ParsingExpression> newRoot = visitor.partialVisit(root);
        Collection<ParsingExpression> newRules = visitor.partialVisit(rules);
        Slot<ParsingExpression> newWhitespace = visitor.partialVisit(whitespace);

        visitor.conclude();

        root = newRoot.get();
        rules = newRules;
        whitespace = newWhitespace.get();
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isNullable(ParsingExpression pe)
    {
        if (nullabilityCalculator == null)
        {
            nullabilityCalculator = new NullabilityCalculator(this);
            walk(nullabilityCalculator);
        }

        return nullabilityCalculator.isNullable(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
