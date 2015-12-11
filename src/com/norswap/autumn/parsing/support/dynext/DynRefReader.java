package com.norswap.autumn.parsing.support.dynext;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.state.ParseState;
import static com.norswap.util.Caster.cast;

/**
 * See {@link DynExtExtension}.
 * <p>
 * The operand to this parsing expression should have a capture that stores the text corresponding
 * to the name of a syntactic extension to lookup. This expression will take the text value of the
 * last created parse tree node and use that as the name.
 */
public final class DynRefReader extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final SyntaxExtension.Type type;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DynRefReader(SyntaxExtension.Type type, ParsingExpression operand)
    {
        this.operand = operand;
        this.type = type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        DynExtState destate = cast(state.customStates[DynExtExtension.INDEX]);

        operand.parse(parser, state);

        if (state.failed())
            return;

        String name = state.tree.children().last().value;
        SyntaxExtension ext;

        switch (type)
        {
            case DECLARATION:
                if ((ext = destate.declSyntaxes.get(name)) == null)
                {
                    String errMsg = "Unknown declaration syntactic extension: " + name;
                    // TODO this should not backtrack but be a fatal error
                    // TODO use errMsg properly
                    System.err.println(errMsg);
                    state.fail(this);
                }
                else
                    destate.target = ext.syntax;
                break;

            case EXPRESSION:
                if ((ext = destate.exprSyntaxes.get(name)) == null)
                {
                    String errMsg = "Unknown expression syntactic extension: " + name;
                    // TODO this should not backtrack but be a fatal error
                    // TODO use errMsg properly
                    System.err.println(errMsg);
                    state.fail(this);
                }
                else
                    destate.target = ext.syntax;
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return "type: " + type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
