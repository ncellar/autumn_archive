package com.norswap.autumn.parsing.debug;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

import static com.norswap.autumn.parsing.debug.Debugger.DEBUGGER;

public class Breakpoint extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        DEBUGGER.pushFrame(operand, state);
        DEBUGGER.suspend(this, state);

        operand.parse(parser, state);
        DEBUGGER.popFrame();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void appendContentTo(StringBuilder builder)
    {
        operand.appendTo(builder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
