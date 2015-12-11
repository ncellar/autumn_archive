package com.norswap.autumn.parsing.extensions.bruteforcetree;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.capture;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.expressions.Dumb;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.graph.Transformer;

/**
 * Wraps every parsing expression in the grammar in a parse tree.
 * <p>
 * Only nominally useful at present, when hacking {@link Capture} to support {@link
 * ParsingExpression#parseDumb}. Making this really useful will entail not wrapping {@link Dumb}
 * nodes and their children, and only wrapping nodes with a name.
 */
public final class BruteForceTreeExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        grammar.transform(new Transformer(pe -> capture(pe.name, pe)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
