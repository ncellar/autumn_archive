package com.norswap.autumn.parsing.support.dynext;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.CustomStateIndex;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.state.CustomState;

/**
 * An extension that enables grammar files to register new grammar extensions while the grammar is
 * being parsed. In particular, this means that the syntactic extensions defined by the extension
 * can be used within the grammar file.
 * <p>
 * To take advantage of this, the meta-grammar (the grammar of grammar files) contains dynamic
 * references, i.e. references to parsing expressions that are not known until parse-time, when
 * first the extensions are loaded, and second the name of the parsing expression to use is read
 * from the input.
 * <p>
 * This extension comes with three custom parsing expression: {@link DynExtReader} which reads a
 * class name from the input and loads the corresponding extension; {@link DynRef}, which reads in a
 * syntactic extension name and resolves to a parsing expression defining the syntax, and {@link
 * DynRefReader}, which calls the last resolved parsing expression.
 * <p>
 * Due to its dynamic nature, a {@link DynRef} cannot report the parsing expression it will invoke;
 * meaning it does not work nice with grammar transformations. Similarly, automatic left-recursion
 * handling (and generally, anything depending on {@link ParsingExpression#firsts} is not possible
 * if the left-recursive cycle crosses over the dynamic reference.
 */
public class DynExtExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new DynExtState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
