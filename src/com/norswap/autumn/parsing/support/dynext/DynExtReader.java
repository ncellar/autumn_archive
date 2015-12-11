package com.norswap.autumn.parsing.support.dynext;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.state.ParseState;

import static com.norswap.util.Caster.cast;

/**
 * See {@link DynExtExtension}.
 * <p>
 * This loads a class whose (fully qualified, after {@link Class#getName}) name is read from the
 * input. The operand to this parsing expression should have a capture that stores the text
 * corresponding to this name. This expression will take the text value of the last created parse
 * tree node and use that as the name.
 */
public class DynExtReader extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DynExtReader(ParsingExpression operand)
    {
        this.operand = operand;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
            return;

        DynExtState destate = cast(state.customStates[DynExtExtension.INDEX]);
        String errMsg = null;
        String name = state.tree.children().last().value;

        try {
            Class<?> klass = Class.forName(name);
            Extension ext = (Extension) klass.newInstance();

            destate.extensions.add(ext);

            for (SyntaxExtension syntaxExt: ext.syntaxExtensions())
            {
                switch (syntaxExt.type)
                {
                    case DECLARATION:
                        destate.declSyntaxes.put(syntaxExt.name, syntaxExt);
                        break;
                    case EXPRESSION:
                        destate.exprSyntaxes.put(syntaxExt.name, syntaxExt);
                        break;
                }
            }

            return;
        }
        catch (ClassNotFoundException e)
        {
            errMsg = "Could not find extension class: " + name;
        }
        catch (ClassCastException e)
        {
            errMsg = String.format("Imported class is not a descendant of %s: %s",
                Extension.class.getName(), name);
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            errMsg = "Imported extension class does not have a public nullary constructor.";
        }

        // TODO this should not backtrack but be a fatal error
        // TODO use errMsg
        state.fail(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}