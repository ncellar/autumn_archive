package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.extensions.cluster.ClusterExtension;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursionExtension;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.ExportedInputs;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.MetaGrammar;
import com.norswap.autumn.parsing.support.dynext.DynExtExtension;
import com.norswap.autumn.parsing.support.dynext.DynExtState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Retained;
import com.norswap.util.graph.GraphVisitor;
import com.norswap.util.graph.Slot;

import java.util.Collection;

/**
 * Builder pattern for {@link Grammar}.
 */
public final class GrammarBuilder implements GrammarBuilderExtensionView
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    private ParsingExpression root;

    private Collection<ParsingExpression> rules;

    private ParsingExpression whitespace;

    private boolean populateRules = false;

    private boolean processLeadingWhitespace = true;

    private boolean defaultExtensions = true;

    private boolean referenceResolution = true;

    private final Array<Extension> extensions = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    GrammarBuilder(ParsingExpression root)
    {
        this.root = root;
        this.populateRules = true;
    }

    // ---------------------------------------------------------------------------------------------

    // TODO use exceptions + document ParseException
    GrammarBuilder(Source source)
    {
        this.source = source;
    }

    // ---------------------------------------------------------------------------------------------

    GrammarBuilder(Grammar grammar)
    {
        this.root = grammar.root;
        this.rules = grammar.rules;
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
        this.defaultExtensions = false;
        this.referenceResolution = false;
        this.populateRules = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GrammarBuilder rules(@Retained Collection<ParsingExpression> rules)
    {
        if (source != null)
            illegal("Trying to set rules to a grammar built from source.");

        if (rules != null)
            illegal("Trying to set rules more than once.");

        this.rules = rules;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder whitespace(ParsingExpression whitespace)
    {
        if (whitespace != null)
            illegal("Trying to set whitespace more than once.");

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

    public GrammarBuilder withExtension(Extension extension)
    {
        extensions.add(extension);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Whether to include the default extensions ({@link ClusterExtension}, {@link
     * LeftRecursionExtension}. Defaults to true.
     */
    public GrammarBuilder defaultExtensions(boolean defaultExtensions)
    {
        this.defaultExtensions = defaultExtensions;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate if references ({@link Reference}) should be resolved. Setting this to false can save
     * on grammar construction time if you know that your grammar contains no references. Defaults
     * to true.
     * <p>
     * Not available from grammars built from source (reference resolution must be performed).
     */
    public GrammarBuilder referenceResolution(boolean referenceResolution)
    {
        if (source != null)
            illegal("Trying to set the referenceResolution option for a grammar built from source.");

        this.referenceResolution = referenceResolution;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate whether we should infer rules from the names of descendants of the root. If so, the
     * root is visited and each named parsing expression becomes a rule. Defaults to false.
     * <p>
     * Not available from grammars built from source (define the rule in your grammar file).
     */
    public GrammarBuilder populateRules(boolean populateRules)
    {
        if (source != null)
            illegal("Trying to set the populateRules option for a grammar built from source.");

        this.populateRules = populateRules;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Extension leftrec = null;
    Extension cluster = null;

    // ---------------------------------------------------------------------------------------------

    public Grammar build()
    {
        if (defaultExtensions)
        {
            leftrec = new LeftRecursionExtension();
            cluster = new ClusterExtension();
        }

        if (source != null)
            buildFromSource();

        if (rules == null)
            rules = new Array<>();

        if (whitespace == null)
            whitespace = Whitespace.DEFAULT();

        if (referenceResolution)
        {
            ReferenceResolver refResolver = new ReferenceResolver();
            transform(refResolver);

            // TODO
            // populateRules == true with new GrammarBuilder(Source) causes nullability visitor crash
            // (note that was a bug, still not very robust)

            if (populateRules)
                rules.addAll(refResolver.named.values());
        }

        if (defaultExtensions)
        {
            // Default extensions must be added first. Especially leftrec, so that other extensions
            // can see the LeftRecursive nodes.
            leftrec.transform(this);
            cluster.transform(this);
        }

        extensions.forEach(ext -> ext.transform(this));

        if (defaultExtensions)
            extensions.addAll(leftrec, cluster);

        return new Grammar(root, rules, whitespace, processLeadingWhitespace, extensions);
    }

    // ---------------------------------------------------------------------------------------------

    private void buildFromSource()
    {
        Array<ExportedInputs> customInputs = new Array<>();

        // TODO make nicer?

        if (defaultExtensions || !extensions.isEmpty())
        {
            DynExtState destate = new DynExtState();

            if (defaultExtensions)
            {
                destate.extensions.add(leftrec);
                destate.extensions.add(cluster);

                // cluster only has expression extensions
                for (SyntaxExtension sext : cluster.syntaxExtensions())
                    destate.exprSyntaxes.put(sext.name, sext);
            }

            for (Extension ext: extensions)
                for (SyntaxExtension sext : ext.syntaxExtensions())
                    destate.exprSyntaxes.put(sext.name, sext);

            customInputs.add(new ExportedInputs(DynExtExtension.class, destate));
        }

        Parser parser = new Parser(MetaGrammar.get, source, ParserConfiguration.DEFAULT);
        ParseResult result = parser.parseRoot(customInputs);

        if (!result.matched)
            throw new ParseException(result.error);

        DynExtState destate = (DynExtState) result.customChanges.get(DynExtExtension.INDEX);
        GrammarCompiler compiler = GrammarCompiler.compile(result.tree, destate);

        root = compiler.root;
        rules = compiler.rules;
        whitespace = compiler.whitespace;
        extensions.addAll(destate.extensions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void illegal(String msg)
    {
        throw new IllegalStateException(msg);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void transform(GraphVisitor<ParsingExpression> visitor)
    {
        Slot<ParsingExpression> root2 = visitor.partialVisit(root);
        Array<Slot<ParsingExpression>> rules2 = visitor.partialVisit(rules);
        Slot<ParsingExpression> whitespace2 = visitor.partialVisit(whitespace);
        visitor.conclude();

        root = root2.latest();
        whitespace = whitespace2.latest();
        rules = rules2.map(Slot::latest);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void compute(GraphVisitor<ParsingExpression> visitor)
    {
        visitor.partialVisit(root);
        visitor.partialVisit(rules);
        visitor.partialVisit(whitespace);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
