package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    public CharSequence text;

    private ParseTree tree;

    private Array<LeftRecursive> blocked;

    private Array<ExpressionCluster.PrecedenceEntry> minPrecedence;

    public ParsingExpression clusterAlternate;

    private int endPosition;
    
    public HandleMap ext = new HandleMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration Fields

    public final ErrorHandler errorHandler;

    public final ParsingExpression whitespace;

    public final MemoizationStrategy memoizationStrategy;

    public final boolean processLeadingWhitespace;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Source source, ParserConfiguration config)
    {
        this.source = source;
        this.text = source.text();

        this.errorHandler = config.errorHandler.get();
        this.whitespace = config.whitespace.get();
        this.memoizationStrategy = config.memoizationStrategy.get();
        this.processLeadingWhitespace = config.processLeadingWhitespace;
    }

    public Parser(Source source)
    {
        this(source, ParserConfiguration.DEFAULT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Use the parser to match its source text to the given parsing expression.
     *
     * After calling this method, the parse tree resulting from the parse can be retrieved via
     * {@link #tree()}.
     *
     * If the parse failed ({@code failed() == true}) or a partial match ({@code
     * matchedWholeSource() == false}), errors can be reported with {@link #report()}.
     */
    public void parse(ParsingExpression pe)
    {
        this.blocked = new Array<>();
        this.minPrecedence = new Array<>();

        ParseState rootState = ParseState.root();
        rootState.tree = tree = new ParseTree();

        if (processLeadingWhitespace)
        {
            int pos = whitespace.parseDumb(this, 0);
            if (pos > 0)
            {
                rootState.start = pos;
                rootState.end = pos;
            }
        }

        pe.parse(this, rootState);

        if ((this.endPosition = rootState.end) < 0)
        {
            rootState.resetAllOutput();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Report the outcome of the parse (success or failure) on System.err and in case of failure,
     * the errors recorded during the parse. The reporting method for the errors is up to the
     * {@link ErrorHandler}.
     */
    public void report()
    {
        if (succeeded())
        {
            System.err.println("The parse succeeded.");
        }
        else
        {
            System.err.println("The parse failed.");
            errorHandler.reportErrors(this);
        }

    }

    //----------------------------------------------------------------------------------------------

    public Source source()
    {
        return source;
    }

    //----------------------------------------------------------------------------------------------

    public ParseTree tree()
    {
        return tree;
    }

    //----------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return endPosition == source.length();
    }

    //----------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return endPosition != source.length();
    }

    //----------------------------------------------------------------------------------------------

    public int endPosition()
    {
        return endPosition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method should be called whenever a parsing expression fails. It calls {@link
     * ParseState#fail} and passes the error to the error handler.
     *
     * {@code state} should be in the same state as when the expression was invoked, modulo any
     * changes that persists across failures (e.g. cuts). This means {@link ParseState#resetOutput}
     * should have been called on the state if necessary.
     *
     * In some cases, an expression may elect not to report a failure, in which case it must
     * call {@link ParseState#fail} directly instead (e.g. left-recursion for blocked recursive
     * calls).
     */
    public void fail(ParsingExpression pe, ParseState state)
    {
        state.fail();

        if (!state.isErrorRecordingForbidden())
        {
            errorHandler.handle(pe, state);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isBlocked(LeftRecursive lr)
    {
        for (LeftRecursive la: blocked)
        {
            if (lr == la)
            {
                return true;
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------

    public void pushBlocked(LeftRecursive lr)
    {
        blocked.push(lr);
    }

    //----------------------------------------------------------------------------------------------

    public void popBlocked()
    {
        blocked.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PRECEDENCE

    //----------------------------------------------------------------------------------------------

    /**
     * If {@code expr} is not yet in the process of being parsed, or if its current precedence is
     * below the given precedence, registers the given precedence for this expression. Returns
     * the old the precedence if there was one, else the given precedence.
     */
    public int enterPrecedence(ExpressionCluster expr, int position, int precedence)
    {
        ExpressionCluster.PrecedenceEntry entry = minPrecedence.peekOrNull();

        if (entry == null || entry.cluster != expr)
        {
            entry = new ExpressionCluster.PrecedenceEntry();
            entry.cluster = expr;
            entry.initialPosition = position;
            entry.minPrecedence = precedence;

            minPrecedence.push(entry);
            return precedence;
        }
        else if (entry.minPrecedence < precedence)
        {
            int result = entry.minPrecedence;
            entry.minPrecedence = precedence;
            return result;
        }
        else
        {
            return entry.minPrecedence;
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Returns the current precedence value for the expression being parsed most recently.
     * This is safe because inter-expression recursion is forbidden.
     */
    public int minPrecedence()
    {
        return minPrecedence.peek().minPrecedence;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Sets the current precedence value for the expression being parsed most recently.
     * This is safe because inter-expression recursion is forbidden.
     */
    public void setMinPrecedence(int precedence)
    {
        minPrecedence.peek().minPrecedence = precedence;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * If the expression being parsed most recently was entered at {@code position}, unregister
     * the expression; otherwise sets its current precedence to {@code precedence}.
     *
     * This method is necessary because non-left recursion of expressions is done by invoking the
     * expression at another position. When this call exits, it needs to restore the precedence
     * that was in effect when it was entered. The initial call needs to unregister the expression.
     */
    public void exitPrecedence(int precedence, int position)
    {
        ExpressionCluster.PrecedenceEntry entry = minPrecedence.peek();

        if (entry.initialPosition == position)
        {
            minPrecedence.pop();
        }
        else
        {
            entry.minPrecedence = precedence;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
