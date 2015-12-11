package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.Decorate;
import com.norswap.autumn.parsing.capture.DecorateWithSubtree;
import com.norswap.autumn.parsing.capture.ParseTreeBuild;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.annotations.NonNull;

/**
 * TODO
 */
public final class SubGrammar extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar subgrammar;
    public final ParserConfiguration config;

    /**
     * For each extension of {@link #subgrammar}, the extension of the current grammar in which to load
     * the current inputs; or null, if the inputs shouldn't be loaded.
     */
    private final Array<Extension> inputMerge;

    /**
     * For each extension of {@link #subgrammar}, the extension of the current grammar in which to
     * merge the outputs; or null, if the inputs shouldn't be merged.
     */
    private final Array<Extension> outputMerge;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final Extension[] EMPTY_EXTENSIONS = new Extension[0];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * TODO
     */
    public SubGrammar(Grammar parent, Grammar subgrammar)
    {
        this(parent, subgrammar, null, EMPTY_EXTENSIONS, EMPTY_EXTENSIONS);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * TODO
     */
    public SubGrammar(
        Grammar parent,
        Grammar subgrammar,
        ParserConfiguration config,
        @NonNull Extension[] inputMerging,
        @NonNull Extension[] outputMerging)
    {
        this.subgrammar = subgrammar;
        this.config = config;

        Array<Extension> parentExtensions = parent.extensions;
        Array<Extension> subExtensions = subgrammar.extensions;

        // TODO the sizes should be in function of custom states, not extensions
        // TODO change this when state indexing is handled by-grammar

        this.inputMerge  = Array.ofSize(subExtensions.size());
        this.outputMerge = Array.ofSize(subExtensions.size());

        if (inputMerging.length == 0 && outputMerging.length == 0)
            return;

        for (Extension parentExt: parentExtensions)
            for (Extension subExt: subExtensions)
                if (parentExt.getClass() == subExt.getClass())
                {
                    if (JArrays.contains(inputMerging, subExt))
                        inputMerge.set(subExt.stateIndex(), parentExt);

                    if (JArrays.contains(outputMerging, subExt))
                        outputMerge.set(subExt.stateIndex(), parentExt);
                }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        Array<ParseInputs.Entry> customInputs = new Array<>();

        for (Extension ext: outputMerge)
            if (ext != null)
            {
                CustomState cstate = state.customStates[ext.stateIndex()];
                customInputs.add(new ParseInputs.Entry(cstate, cstate.inputs(state)));
            }

        ParseInputs inputs = ParseInputs.create(
            subgrammar.root,
            state.start,
            state.start,
            0,
            true,
            customInputs);

        Parser subParser =
            new Parser(subgrammar, parser.source, config == null ? parser.config : config);

        ParseResult result = subParser.parse(inputs);

        if (!result.succeeded)
        {
            state.errors.merge(result.error.locations());
            state.fail(this);
        }
        else
        {
            state.tree.addChild(
                new ParseTreeBuild(false, new Decorate[]{ new DecorateWithSubtree(result.tree)}));

            for (int i = 0; i < outputMerge.size(); i++)
            {
                Extension towards = outputMerge.get(i);

                if (towards != null)
                {
                    Object changes = result.customChanges.get(i);
                    CustomState cstate = state.customStates[towards.stateIndex()];
                    cstate.merge(changes, state);
                }
            }

            state.advance(result.endPosition - state.start);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
