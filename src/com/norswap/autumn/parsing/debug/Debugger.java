package com.norswap.autumn.parsing.debug;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.TextPosition;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class Debugger
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class StackFrame
    {
        public ParsingExpression pe;
        public ParseState state;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Debugger DEBUGGER = new Debugger();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Parser parser;

    ParsingExpression[] grammar;

    JSObject jsWindow;

    private Array<StackFrame> stack = new Array<>();

    private boolean blocked = false;

    private Object lock = new Object();

    private boolean step = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void start()
    {
        parser.parse(grammar[0]);
    }

    // ---------------------------------------------------------------------------------------------

    public void pushFrame(ParsingExpression pe, ParseState state)
    {
        StackFrame frame = new StackFrame();
        frame.pe = pe;
        frame.state = state;
        stack.push(frame);
        TextPosition pos = parser.source().position(state.start);
        Platform.runLater(() ->
        {
            jsWindow.call("pushFrame", new Object[]{pe.toString(), pos.line, pos.column, state.start});
        });
    }

    // ---------------------------------------------------------------------------------------------

    public void popFrame()
    {
        stack.pop();
        Platform.runLater(() -> jsWindow.call("popFrame", new Object[0]));
    }

    // ---------------------------------------------------------------------------------------------

    public void suspend(Breakpoint bp, ParseState state)
    {
        if (doSuspend(bp, state))
        {
            blocked = true;

            while (blocked)
            {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {}
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void resume()
    {
        blocked = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    // ---------------------------------------------------------------------------------------------

    protected boolean doSuspend(Breakpoint bp, ParseState state)
    {
        if (step)
        {
            return true;
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    public void doContinue()
    {
        step = false;
        resume();
    }

    // ---------------------------------------------------------------------------------------------

    public void doStep()
    {
        step = true;
        resume();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
