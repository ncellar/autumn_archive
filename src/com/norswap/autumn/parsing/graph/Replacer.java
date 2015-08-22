package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.graph_visit.GraphTransformer;

import java.util.Map;

/**
 * Given a partial or total mapping between parsing expressions, this visitor replaces parsing
 * expressions in the graph according to the mapping.
 */
public class Replacer extends GraphTransformer<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<ParsingExpression, ParsingExpression> replacements;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Replacer(Map<ParsingExpression, ParsingExpression> replacements)
    {
        super(Walks.inPlace);
        this.replacements = replacements;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression transform(ParsingExpression pe)
    {
        ParsingExpression replacement = replacements.get(pe);
        return replacement != null ? replacement : pe;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        super.conclude();
        replacements = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
