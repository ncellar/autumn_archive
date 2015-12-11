package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.parsing.extensions.CustomStateIndex;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.state.CustomState;

public class PythonExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    private static final SyntaxExtension[] syntaxExtensions = new SyntaxExtension[] {
        new PythonSyntaxExtension("INDENT"),
        new PythonSyntaxExtension("DEDENT"),
        new PythonSyntaxExtension("NEWLINE"),
        new PythonSyntaxExtension("TOKEN"),
        new PythonSyntaxExtension("START_LINE_JOINING"),
        new PythonSyntaxExtension("END_LINE_JOINING")};

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new PythonState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public SyntaxExtension[] syntaxExtensions()
    {
        return syntaxExtensions;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
