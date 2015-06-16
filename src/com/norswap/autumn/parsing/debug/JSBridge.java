package com.norswap.autumn.parsing.debug;

import com.norswap.autumn.parsing.TextPosition;

import static com.norswap.autumn.parsing.debug.Debugger.DEBUGGER;

public class JSBridge
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String _text; // can't be named "text"

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void log(Object object)
    {
        System.out.println("js: " + object);
    }

    // ---------------------------------------------------------------------------------------------

    public String text()
    {
        return _text;
    }

    // ---------------------------------------------------------------------------------------------

    public String subText(int from, int to)
    {
        return _text.substring(from, to)
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

    // ---------------------------------------------------------------------------------------------

    public String subTextFrom(int from)
    {
        return subText(from, _text.length());
    }

    // ---------------------------------------------------------------------------------------------

    public String lineAndColumn(int position)
    {
        TextPosition tpos = DEBUGGER.parser.source().position(position);
        return tpos.line + "," + tpos.column;
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(int line, int column)
    {
        return DEBUGGER.parser.source().fileOffset(line, column);
    }

    // ---------------------------------------------------------------------------------------------

    public void doContinue()
    {
        DEBUGGER.doContinue();
    }

    // ---------------------------------------------------------------------------------------------

    public void doStep()
    {
        DEBUGGER.doStep();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
