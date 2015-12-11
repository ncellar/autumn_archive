package com.norswap.autumn.parsing.extensions.cluster.expressions;

import com.norswap.autumn.parsing.extensions.cluster.ClusterExtension;
import com.norswap.autumn.parsing.extensions.cluster.ClusterState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.util.annotations.NonNull;

import java.util.Arrays;

import static com.norswap.util.Caster.cast;

/**
 * IMPORTANT NOTE
 * <p>
 * Do not use filtering if you can avoid it. This mechanism is very brittle.
 * <p>
 * It invokes its operand, which should be a cluster expression, or something wrapping one, and
 * fails if it detectes that the cluster succeeded by selecting one of the arrows that this filter
 * forbids or does not allow.
 * <p>
 * It detects arrows via their parsing expressions name. The reason that this is brittle is that the
 * expression name of the arrow is almost guaranteed to get lost during grammar transformations.
 * Indeed, if an arrow gets wrapped in another expression, the wrapping expression will not have the
 * same name. To avoid this, special provisions need to be taken to transfer or copy the expression
 * name. This is a bad idea in general, so it should be done for cluster arrows in particular.
 */
public final class Filter extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final @NonNull String[] allowed;
    public final @NonNull String[] forbidden;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Filter(ParsingExpression operand, @NonNull String[] allowed, @NonNull String[] forbidden)
    {
        this.operand = operand;
        this.allowed = allowed;
        this.forbidden = forbidden;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ClusterState cstate = cast(state.customStates[ClusterExtension.INDEX]);

        operand.parse(parser, state);

        if (state.failed())
            return;

        boolean success = allowed.length == 0;
        ParsingExpression clusterAlternate = cstate.getAlternate();

        for (String name: allowed)
        {
            if (name.equals(clusterAlternate.name))
            {
                success = true;
                break;
            }
        }

        for (String name: forbidden)
        {
            if (name.equals(clusterAlternate.name))
            {
                success = false;
                break;
            }
        }

        if (!success)
        {
            state.discard();
            state.fail(this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return String.format("allowed: %s, forbidden: %s",
            Arrays.toString(allowed),
            Arrays.toString(forbidden));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
