package com.norswap.autumn.test.languages.clike;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;

public class TypeDef extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    TypeDef(ParsingExpression operand)
    {
        this.operand = operand;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            state.fail();
            return;
        }

        CLikeState clstate = (CLikeState) state.customStates[CLikeExtension.INDEX];
        String typeName = state.tree.children().last().value;

        clstate.add(typeName);

        System.err.println(String.format("type definition (%s) at: %s",
            typeName,
            parser.source.position(state.start)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
