package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ExportedInputs;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import java.util.HashMap;

public final class Parser implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar grammar;

    public final Source source;

    public final CharSequence text;

    public final ParsingExpression whitespace;

    public final ParserConfiguration config;

    public final boolean processLeadingWhitespace;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParseState state;
    private HashMap<Class, Extension> extensions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Grammar grammar, Source source, ParserConfiguration config)
    {
        this.grammar = grammar;
        this.source = source;
        this.text = source.text;
        this.config = config;
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invokes the root of the grammar at the start of the input and returns the result.
     */
    public ParseResult parseRoot()
    {
        makeState();
        return parse2(ParseInputs.create(grammar.root, 0, 0, 0, true, Array.empty()));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Invokes the root of the grammar at the start of the input and returns the result.
     * Before the parse, load the supplied custom inputs.
     */
    public ParseResult parseRoot(Array<ExportedInputs> exportedInputs)
    {
        makeState();

        Array<ParseInputs.Entry> array = exportedInputs.map(
            exp -> new ParseInputs.Entry(
                state.customStates[extensions.get(exp.extension).stateIndex()],
                exp.actualInputs));

        return parse2(ParseInputs.create(grammar.root, 0, 0, 0, true, array));
    }

    // ---------------------------------------------------------------------------------------------

    public ParseResult parse(ParseInputs inputs)
    {
        makeState();
        return parse2(inputs);
    }

    // ---------------------------------------------------------------------------------------------

    private void makeState()
    {
        Array<CustomState> indexedStates = new Array<>();
        extensions = new HashMap<>();

        for (Extension extension: grammar.extensions)
        {
            int index = extension.stateIndex();
            if (index != -1)
            {
                indexedStates.put(index, extension.customParseState());
                extensions.put(extension.getClass(), extension);
            }
        }

        state = new ParseState(
            config.errorState(),
            config.memoHandler(),
            indexedStates.toArray(CustomState[]::new));
    }

    // ---------------------------------------------------------------------------------------------

    private ParseResult parse2(ParseInputs inputs)
    {
        state.load(inputs);
        if (inputs.start() == 0) processLeadingWhitespace(state);
        inputs.pe().parse(this, state);

        ParseResult out = new ParseResult(
            state.end == source.length(),
            state.end >= 0,
            state.end,
            state.tree.build()[0],
            Array.map(state.customStates, x -> x == null ? null : x.extract(state)),
            state.errors.report(source));

        if (state.end < 0)
            state.discard();

        state = null;
        extensions = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private void processLeadingWhitespace(ParseState state)
    {
        if (processLeadingWhitespace)
        {
            int pos = whitespace.parseDumb(this, 0);
            if (pos > 0)
            {
                state.start = pos;
                state.end = pos;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Parser clone()
    {
        try {
            return (Parser) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error(); // shouldn't happen
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
