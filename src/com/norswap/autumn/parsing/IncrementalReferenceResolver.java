package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.Caster;

import java.util.HashSet;

/**
 * Instantiate this class then run its {@link #walk} method to resolve references to a single
 * named expression.
 *
 * This is meant to be called by the {@link ParsingExpressionFactory#recursive$}. The point of
 * this resolution method is that it is combinator friendly: you can use the aforementioned
 * method to build a recursive expression in code. For automatically generated expression graphs,
 * use {@link ReferenceResolver}.
 *
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression graph and replaced with edges towards the expression they referenced.
 *
 * When the walk encounters a reference to another expression, it saves the current target in {@link
 * Reference#nestedReferences}. The resolution process for current target will resume
 * once that foreign reference is itself resolved. This guarantees that by running this resolver on
 * every referenced expressions, all references end up resolved.
 */
public final class IncrementalReferenceResolver
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression recursive;
    private HashSet<ParsingExpression> visited = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public IncrementalReferenceResolver(ParsingExpression target)
    {
        assert(target.name() != null);
        this.recursive = target;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int walk(ParsingExpression pe)
    {
        if (pe.hasFlagsSet(Registry.PEF_RESOLVED) || visited.contains(pe))
        {
            return 0;
        }

        visited.add(pe);

        int nUnresolved = 0;
        ParsingExpression[] children = pe.children();

        for (int i = 0; i < children.length; ++i)
        {
            if (children[i] instanceof Reference)
            {
                Reference ref = Caster.cast(children[i]);

                if (recursive.name().equals(ref.target))
                {
                    pe.setChild(i, recursive);

                    if (ref.nestedReferences != null)
                    for (ParsingExpression nr: ref.nestedReferences)
                    {
                        new IncrementalReferenceResolver(nr).walk(recursive);
                    }
                }
                else
                {
                    if (ref.nestedReferences == null)
                    {
                        ref.nestedReferences = new Array<>();
                    }

                    ref.nestedReferences.add(recursive);
                    ++nUnresolved;
                }
            }
            else
            {
                nUnresolved += walk(children[i]);
            }
        }

        if (nUnresolved == 0)
        {
            pe.setFlags(Registry.PEF_RESOLVED);
        }

        return nUnresolved;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
