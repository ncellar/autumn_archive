package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;

import java.util.HashMap;
import java.util.Set;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.leftRecursive;

/**
 * This break left-recursive cycles detected by a {@link LeftRecursionDetector} by wrapping every
 * occurrence of expressions that the detector has recorded in a {@link LeftRecursive} expression.
 *
 * To do so, the breaker starts by creating the LeftRecursive replacement for each node recorded
 * by the detector. It then walks the graph and records the location (a {@link Slot}) where each
 * recorded node occur. We can't replace nodes during the walk as that would break the walking
 * algorithm. Finally, it replaces each recorded location by its proper replacement.
 */
public final class LeftRecursionBreaker extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class Mod
    {
        Slot slot;
        LeftRecursive replacement;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, LeftRecursive> replacements;
    private Array<Mod> mods;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Breaks left-recursive cycles, in the manner specified by {@link LeftRecursionDetector}.
     * Modifies the {@code rules} array in place and returns it.
     */
    public static ParsingExpression[] breakCycles(ParsingExpression[] rules)
    {
        return new LeftRecursionBreaker().run(rules);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Breaks left-recursive cycles, in the manner specified by {@link LeftRecursionDetector}.
     * Modifies the {@code rules} array in place and returns it.
     */
    public ParsingExpression[] run(ParsingExpression[] rules)
    {
        Set<ParsingExpression> leftRecursives = LeftRecursionDetector.detect(rules);

        replacements = new HashMap<>();

        for (ParsingExpression pe : leftRecursives)
        {
            replacements.put(pe, leftRecursive(pe));
        }

        mods = new Array<>();
        walk(rules);

        for (Mod mod: mods)
        {
            mod.slot.set(mod.replacement);
        }

        for (int i = 0; i < rules.length; ++i)
        {
            LeftRecursive replacement = replacements.get(rules[i]);

            if (replacement != null)
            {
                rules[i] = replacement;
            }
        }

        return rules;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void afterChild(ParsingExpression pe, ParsingExpression child, int index, State state)
    {
        LeftRecursive replacement = replacements.get(child);

        if (replacement != null)
        {
            Mod mod = new Mod();
            mod.slot = new Slot(pe, index);
            mod.replacement = replacement;
            mods.add(mod);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
