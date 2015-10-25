package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.CustomState.Result;
import com.norswap.autumn.parsing.state.CustomStateFactory;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.JArrays;

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
        return parse(rootInputs());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parses the source using the supplied inputs and returns the result.
     */
    public ParseResult parse(ParseInputs inputs)
    {
        ParseState state = new ParseState(
            inputs,
            config.errorState(),
            config.memoHandler(),
            config.customStateFactories());

        if (inputs.start == 0 && processLeadingWhitespace)
        {
            int pos = whitespace.parseDumb(this, 0);
            if (pos > 0)
            {
                state.start = pos;
                state.end = pos;
            }
        }

        inputs.pe.parse(this, state);

        int end = state.end;

        if (end < 0)
        {
            state.discard();
        }

        return new ParseResult(
            end == source.length(),
            end >= 0,
            end,
            state.tree.build(),
            JArrays.map(state.customStates, Result[]::new, CustomState::result),
            state.errors.report(source));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the inputs for parsing the root of the grammar associated to the parser over the whole
     * source.
     */
    public ParseInputs rootInputs()
    {
        return new ParseInputs(
            grammar.root,
            0,
            0,
            0,
            true,
            null,
            new Array<>(),
            new Array<>(),
            config.customStateFactories()
                .mapToArray(CustomStateFactory::rootInputs, CustomState.Inputs[]::new));
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
