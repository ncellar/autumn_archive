package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import com.norswap.autumn.parsing.expressions.Literal;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;

public class PythonToken extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final ParsingExpression

        notNewline = zeroMore(choice(
            oneMore(charSet(" \t")),
            sequence(literal("#"), zeroMore(notCharSet("\n"))),
            literal("\\\n"))),

        allWhitespace = zeroMore(choice(
            oneMore(charSet(" \t\n")),
            sequence(literal("#"), until(any(), literal("\n"))),
            literal("\\\n")));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    PythonToken(ParsingExpression operand)
    {
        this.operand = operand;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        /*
        PythonState
            int oldIndent
            int indent
            int lineJoining
            bool newlineEmitted

        local
            boolean inNewline = false

        if lineJoining > 0
            skip over all whitespace (including newlines)
            return
        1: skip whitespace (but not newlines unless escaped by \)
        if next char is a newline
            inNewline = false
            goto 1
        else
            if inNewline
                newlineEmitted = true
                oldIndent = indent
                updateIndent

       NEWLINE
            if newlineEmitted
                newlineEmitted = false
                succeed
            else fail

        INDENT:
            if !newlineEmitted && indent > oldIndent && invocation position matches indent
                succeed
                oldIndent = indent
            else fail

        DEDENT:
            if !newline_emitted && indent < oldIndent && invocation position matches indent
                succeed
                oldIndent -= 1
            else fail

        Problem 1: Implicit Line Joining
            Solution: insert new tokens in the gramamr that start/end the line joining context.
            Since these contexts can be nested, we should keep a counter.

        Problem 2: The algorithm might emit a newline at the start of the file.
            Solution: implicitly start a line-joining context, insert a line-joining context end
            at the start of the grammar.

        TODO
            Indentation levels assume multiple of tabSize. This is not necessarily so in the
            Python lexical structure (https://docs.python.org/2/reference/lexical_analysis.html).
            Move to a stack-based implementation.

        */

        PythonState pstate = (PythonState) state.customStates[PythonExtension.INDEX];

        if (pstate.token() != PythonIndentToken.NONE)
        {
            state.fail(this);
            return;
        }

        operand.parse(parser, state);

        if (state.failed())
        {
            state.fail(this);
            return;
        }

        if (pstate.lineJoining > 0)
        {
            int pos = allWhitespace.parseDumb(parser, state.end);
            if (pos > 0)
                state.end = pos;

            return;
        }

        int pos = state.end;
        boolean inNewline = false;
        int newLinePos = 0;

        while (true)
        {
            int tmp = notNewline.parseDumb(parser, pos);
            if (tmp > 0) pos = tmp;

            if (parser.text.charAt(pos) == '\n')
            {
                inNewline = true;
                ++ pos;
                newLinePos = pos;
                continue;
            }
            else if (inNewline)
            {
                pstate.newLineEmmitted = true;
                pstate.oldIndent = pstate.indent;
                pstate.indent = (pos - newLinePos) / parser.source.tabSize;
            }
            break;
        }

        state.end = pos;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
