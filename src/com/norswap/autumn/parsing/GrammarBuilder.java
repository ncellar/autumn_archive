package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.graph.LeftRecursionHandler;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.util.Array;
import com.norswap.util.graph.GraphVisitor;
import com.norswap.util.graph.Slot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder pattern for {@link Grammar}.
 */
public final class GrammarBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression root;

    private Collection<ParsingExpression> rules;

    private ParsingExpression whitespace;

    private boolean processLeadingWhitespace = true;

    private Map<String, String> options;

    private boolean leftRecursionElimination = true;

    private boolean referenceResolution = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    GrammarBuilder(ParsingExpression root)
    {
        this.root = root;
    }

    // ---------------------------------------------------------------------------------------------

    GrammarBuilder(Grammar grammar)
    {
        this.root = grammar.root;
        this.rules = grammar.rules;
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
        this.options = grammar.options;
        this.leftRecursionElimination = false;
        this.referenceResolution = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GrammarBuilder rules(Collection<ParsingExpression> rules)
    {
        this.rules = rules;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder whitespace(ParsingExpression whitespace)
    {
        this.whitespace = whitespace;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder processLeadingWhitespace(boolean processLeadingWhitespace)
    {
        this.processLeadingWhitespace = processLeadingWhitespace;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder leftRecursionElimination(boolean leftRecursionElimination)
    {
        this.leftRecursionElimination = leftRecursionElimination;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder referenceResolution(boolean referenceResolution)
    {
        this.referenceResolution = referenceResolution;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder options(Map<String, String> options)
    {
        this.options = options;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder addOption(String key, String value)
    {
        if (this.options == null)
        {
            this.options = new HashMap<>();
        }

        this.options.put(key, value);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Grammar build()
    {
        rules = rules != null
            ? rules
            : Collections.emptyList();

        whitespace = whitespace != null
            ? whitespace
            : Whitespace.DEFAULT();

        options = options != null
            ? options
            : Collections.emptyMap();

        if (referenceResolution)
        {
            transform(new ReferenceResolver());
        }

        if (leftRecursionElimination)
        {
            NullabilityCalculator calc = new NullabilityCalculator();
            compute(calc);

            LeftRecursionHandler detector = new LeftRecursionHandler(true, calc);
            transform(detector);
        }

        return new Grammar(root, rules, whitespace, processLeadingWhitespace, options);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GrammarBuilder transform(GraphVisitor<ParsingExpression> visitor)
    {
        Slot<ParsingExpression> root2 = visitor.partialVisit(root);
        Array<Slot<ParsingExpression>> rules2 = visitor.partialVisit(rules);
        Slot<ParsingExpression> whitespace2 = visitor.partialVisit(whitespace);
        visitor.conclude();

        root = root2.latest();
        whitespace = whitespace2.latest();
        rules = rules2.map(Slot::latest);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder compute(GraphVisitor<ParsingExpression> visitor)
    {
        visitor.partialVisit(root);
        visitor.partialVisit(rules);
        visitor.partialVisit(whitespace);
        visitor.conclude();
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
