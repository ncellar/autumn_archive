package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.util.graph.GraphVisitor;

/**
 * This interface exposes a restricted view of {@link GrammarBuilder} to the {@link
 * Extension#transform} method. Namely only visiting the grammar and running transformations over it
 * are allowed.
 */
public interface GrammarBuilderExtensionView
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs the given visitor over the rules, root and whitespace (in that order) of the grammar.
     * Note that all of these expressions are visited as part of the same visit.
     * <p>
     * Unlike {@link #compute}, the result of the visit (i.e. the transformation!) is retained and
     * replaces the original parsing expressions.
     */
    void transform(GraphVisitor<ParsingExpression> visitor);

    // ---------------------------------------------------------------------------------------------

    /**
     * Runs the given visitor over the rules, root and whitespace (in that order) of the grammar.
     * Note that all of these expressions are visited as part of the same visit.
     * <p>
     * Unlike {@link #transform} the result of the visit (i.e. a potential transformation) is not
     * retained.
     */
    void compute(GraphVisitor<ParsingExpression> visitor);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
