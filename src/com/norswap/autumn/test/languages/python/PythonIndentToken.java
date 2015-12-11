package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.state.ParseState;

public class PythonIndentToken extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int
    NONE    = 0,
    INDENT  = 1,
    DEDENT  = 2,
    NEWLINE = 3;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public PythonIndentToken(String name)
    {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        PythonState pstate = (PythonState) state.customStates[PythonExtension.INDEX];
        int token = pstate.token();

        switch (name)
        {
            case "INDENT":
                if (token == INDENT)
                    ++ pstate.oldIndent;
                    //pstate.oldIndent = pstate.indent;
                else
                    state.fail();
                break;

            case "DEDENT":
                if (token == DEDENT)
                    -- pstate.oldIndent;
                else
                    state.fail();
                break;

            case "NEWLINE":
                if (token == NEWLINE)
                    pstate.newLineEmmitted = false;
                else
                    state.fail();
                break;

            case "START_LINE_JOINING":
                ++ pstate.lineJoining;
                break;

            case "END_LINE_JOINING":
                -- pstate.lineJoining;
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
