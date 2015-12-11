package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.capture.Decorate;
import com.norswap.autumn.parsing.capture.ParseTreeBuild;
import com.norswap.autumn.parsing.config.DefaultMemoHandler;
import com.norswap.autumn.parsing.config.MemoHandler;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.expressions.Not;
import com.norswap.autumn.parsing.expressions.Precedence;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.errors.DefaultErrorState;
import com.norswap.autumn.parsing.errors.ErrorState;
import com.norswap.util.Array;
import net.nicoulaj.compilecommand.annotations.Inline;

/**
 * An instance of this class is passed to every parsing expression invocation {@link
 * ParsingExpression#parse}.
 * <p>
 * The parse state is the sole access point for all state (mutable data) that the expression will
 * manipulate during the parse. It contains things like the input position or the parse tree being
 * built.
 * <p>
 * <p>
 * <strong>Committed and Uncommitted State</strong>
 * <p>
 * The parse state is divided between committed and uncommitted state. A very simple example is
 * state is the input position. When a parsing expression succeeds, it may consume some input and
 * signal so by setting the {@link #end} field. This is an uncommitted state change. Whenever {@link
 * #commit} is called, the value of {@code end} is assigned to {@link #start}. Subsequent expression
 * invocations using this state will parse at the new input position. Calling {@link #discard} will
 * discard all the uncommitted data.
 * <p>
 * An important convention in Autumn is that an expression invocation shouldn't add any committed
 * state to the parse state it receives. At first, this seems incompatible with the very existence
 * of {@link #commit}. The trick is the {@link #uncommit} method, which will be explained later.
 * <p>
 * You can extract uncommitted to a {@link ParseChanges} object through the {@link #extract} method.
 * Usually, this is done after invoking a sub-expression. Because of the convention we just
 * mentioned, this means that the {@code ParseChanges} object contains the "output" of the
 * sub-expression invocation: its net effect on the parse state. Because parse changes are just
 * uncommitted changes to the state, you can re-apply them to any state, using the {@link #merge}
 * method.
 * <p>
 * Finally, note that the separation between committed and uncommitted data is conceptual, in
 * practice, the data is often twined (for performance reasons), and there is some additional data
 * that allows to distinguish the committed and uncommitted parts.
 * <p>
 * <strong>Parse Inputs</strong>
 * <p>
 * <p>
 * The parse inputs are a particularly important subset of the parse state. The parse inputs consist
 * of all the state that influences the result of expression invocations. Obviously this includes
 * the input position, but also things like the precedence level or blocked expressions. Not all
 * state is part of the parse inputs. For instance, some state is closer to what we could call an
 * output (e.g. the parse tree). The set of parse inputs for the current parse state (a {@link
 * ParseInputs} object) can be obtained via the {@link #inputs} method.
 * <p>
 * An expression invocation can be seen as a pure function from a {@link ParseInputs} object to a
 * {@link ParseChanges} object. Of course, these objects aren't usually involved. They are only
 * created on-demand, by calling {@link #inputs} and {@link #extract} respectively. This notion is
 * used for memoization: the default memoization strategy ({@link DefaultMemoHandler} is to maintain
 * a map from {@code ParseInputs} to {@code ParseChanges}.
 * <p>
 * <strong>Memoization</strong>
 * <p>
 * You can also customize the memoization strategy by supplying a custom {@link MemoHandler} to the
 * {@link ParserConfiguration}. In general, a memoization handler memoizes the {@link ParseChanges}
 * that results from invoking an expression with given {@link ParseInputs}. It is the strategy's
 * responsibility to decide which invocations should be memoized, for how long, and the
 * implementation.
 * <p>
 * <strong>Snapshots</strong>
 * <p>
 * As we mentioned earlier, the convention is to not have any committed changes appear after
 * invoking a sub-expression. Yet sub-expressions might need to commit changes. For instance, a
 * sequence expression will need to commit after each successful element in the sequence in order
 * for the next element to parse at the correct position. A solution to this problem is offered by
 * snapshots.
 * <p>
 * A snapshot ({@link ParseStateSnapshot}) captures the information needed to rollback a parse state
 * to a time before changes were committed to it. A snapshot can be created using the {@link
 * #snapshot} method. A parse state can be rolled back by passing a snapshot to the {@link #restore}
 * method.
 * <p>
 * Alternatively, a snapshot can also be passed to the {@link #uncommit} method. In this case,
 * changes made to the state after the snapshot was taken are not discarded, but treated as
 * uncommitted instead.
 * <p>
 * In the sequence example, you would create a snapshot before beginning to invoke the elements of
 * the sequence; then commit the results as these element invocations succeed; and finally either
 * call {@link #restore} (if an element invocation fails) or {@link #uncommit} (if all element
 * invocations succeed). This ensures that no changes made to the state by a sequence expression
 * will appear committed to its parent expressions.
 * <p>
 * Snapshots have important restrictions placed on their use. In a {ParsingExpression#parse} method,
 * you should only call {@code restore} and {@code uncommit} with a snapshot that was created in the
 * same method (and for the same parse state). A snapshot should not be passed to parents or
 * sub-expressions.
 * <p>
 * These restrictions allow snapshots to be relatively lightweight. They make it possible to use our
 * knowledge that the sub-expressions will not discard certain parts of the parse state, or that we
 * will never see some of the changes made by the sub-expressions because these changes are strictly
 * scoped: they are only visible within an expression and (a subset of) its sub-expressions.
 * <p>
 * Despite its name, a snapshot is not a full picture of the parse state, and as such cannot be
 * passed around to recall arbitrary parse states.
 * <p>
 * <p>
 * <strong>Custom Parse State</strong>
 * <p>
 * Users can add their own parse state, as classes implementing the {@link CustomState} interface.
 * These states can be accessed through the {@link #customStates} field. Custom states are
 * registered as part of an Autumn extension ({@link Extension}).
 * <p>
 * <strong>Manipulating End Positions</strong>
 * <p>
 * In addition to the afrementioned methods, the class also has a few methods to manipulate and
 * query the {@link #end} and {@link #blackEnd} positions: {@link #advance}, {@link #fail}, {@link
 * #succeeded} and {@link #failed}.
 * <p>
 * <strong>Error Handling</strong>
 * <p>
 * In this section, we discuss errors, i.e. the failure of a parsing expression to match any input.
 * When the parse fails, it is desirable to display information about the errors that occurred
 * during the parse to the user. Most of these errors are benign, they merely cause an alternative
 * to be tried. It is the role of an {@link ErrorState} instance to take stock of the errors as they
 * occur and retain some information about them to be displayed to the user if necessary.
 * <p>
 * The default implementation of {@link ErrorState} is {@link DefaultErrorState}. Its strategy is to
 * keep track of the errors occuring at the farthest error position. Users can supply their own
 * error handling strategy using {@link ParserConfiguration.Builder#errorState}.
 * <p>
 * While errors are part of the parse state, they escape the "commit paradigm". Since uncommitted
 * changes are usually discarded when an expression fails, we would end up losing all the error
 * information under the commit paradigm. Instead, we consider that changes to the error state
 * cannot be undone.
 * <p>
 * There are two special consideration to take into account. First, it might be desirable to inhibit
 * error recording (e.g. for {@link Not} expressions). The {@link #recordErrors} field fulfills this
 * role.
 * <p>
 * Second, we sometimes need to get the error information for a subset of the whole parse and to
 * include it in a {@link ParseChanges} object (e.g. for memoization). To solve this, a parsing
 * expression can request an "error record point". All errors occuring after an error record point
 * has been requested will contribute to the information associated to the error record point, as
 * well as to the error information associated to all error record points requested before it. For
 * instance, the default error handling strategy keeps, for each error record point, the errors
 * occuring at the farthest error position *after* the record point has been requested.
 * <p>
 * You can request an error record point with {@link ErrorState#requestErrorRecordPoint} and when
 * you're done, discard it with {@link ErrorState#dismissErrorRecordPoint}. The lifetime of error
 * record points must be properly scoped: a call to {@link ErrorState#dismissErrorRecordPoint} will
 * dismiss the last requested record point.
 * <p>
 * The changes corresponding to the last error record point are returned by {@link
 * ErrorState#changes}. Merging such changes is done via {@link ErrorState#merge}. In practice,
 * Autumn's core functionality never sets error record point, nor do they call the two previous
 * methods. But the possibly might be useful for extensions.
 * <p>
 * At the end of the parse (whether it succeeded or not), the parser will call {@link
 * ErrorState#report(Source)} to generate an error report for the user. The passed source can be
 * used to obtain (line, column) file positions from file offsets.
 * <p>
 * <strong>Parse Result</strong>
 * <p>
 * At the end of the parse, the parser will gather the results of the parse in a {@link ParseResult}
 * object. This includes whether the root expression of the grammar matched some input, whether it
 * matched the whole input, the parse tree generated, and an error report. Additionally, for each
 * custom parse state, its final parse changes will also be included in the parse result.
 */
public final class ParseState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The current input position, i.e. the position where parsing expressions will be invoked.
     */
    public int start;

    /**
     * The last non-whitespace input position preceding {@link #start}. This is useful
     * to avoid including trailing whitespaces in captures.
     */
    public int blackStart;

    /**
     * An uncommitted change to {@link #start}. You can think of it as one past the position of the
     * end of the text matched by the parsing expression, or -1 if no match could be made.
     */
    public int end;

    /**
     * An uncommitted change to {@link #blackStart}: one past the position of the last
     * non-whitespace character preceding {@link #end}.
     */
    public int blackEnd;

    /**
     * The current precedence level for {@link Precedence} expressions.
     */
    public int precedence;

    /**
     * Indicates whether parse errors should be recorded.
     */
    public boolean recordErrors;

    /**
     * The parse tree which is to be the parent of parse trees produced by captures in the parsing
     * expression.
     */
    public ParseTreeBuild tree;

    /**
     * The number of committed children of {@link #tree}. Further children are uncommitted.
     */
    public int treeChildrenCount;

    /**
     * See {@link ParseState}, section "Error Handling".
     */
    public final ErrorState errors;

    /**
     * See {@link ParseState}, section "Memoization".
     */
    public final MemoHandler memo;

    /**
     * A set of additional user-defined parse states.
     */
    public final CustomState[] customStates;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseState(
        ErrorState errorState,
        MemoHandler memoHandler,
        CustomState[] customStates)
    {
        this.tree = new ParseTreeBuild(true, new Decorate[0]);
        this.memo = memoHandler;
        this.errors = errorState;
        this.recordErrors = true;
        this.customStates = customStates;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Advances the end and black end positions by n characters.
     */
    @Inline
    public void advance(int n)
    {
        end += n;
        blackEnd = end;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the end position to indicate that no match could be found.
     */
    @Inline
    public void fail()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates whether the match was successful.
     */
    @Inline
    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates whether the match was unsuccessful.
     */
    @Inline
    public boolean failed()
    {
        return end == -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Signals that {@code pe} failed, and sets the end position to indicate that no match could be found.
     * <p>
     * This state should not contain any committed changes compared to when {@code pe} was invoked.
     * <p>
     * In some cases, an expression may elect not to report a failure, in which case it must call
     * {@link ParseState#fail} directly instead (e.g. left-recursion for blocked recursive calls).
     */
    @Inline
    public void fail(ParsingExpression pe)
    {
        this.end = -1;
        this.blackEnd = -1;

        if (recordErrors)
        {
            errors.handleError(pe, this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Inline
    public ParseInputs inputs(ParsingExpression pe)
    {
        Array<ParseInputs.Entry> custom = new Array<>();

        for (CustomState state: customStates)
            if (state != null)
                custom.add(new ParseInputs.Entry(state, state.inputs(this)));

        return ParseInputs.create(
            pe,
            start,
            blackStart,
            precedence,
            recordErrors,
            custom);
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void load(ParseInputs inputs)
    {
        this.start = inputs.start();
        this.blackStart = inputs.blackStart();
        this.precedence = inputs.precedence();
        this.recordErrors = inputs.recordErrors();

        inputs.customInputs().forEach(e -> e.state.load(e.input));
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public ParseStateSnapshot snapshot()
    {
        Object[] snapshots = new Object[customStates.length];

        for (int i = 0; i < customStates.length; ++i)
            if (customStates[i] != null)
                snapshots[i] = customStates[i].snapshot(this);

        return new ParseStateSnapshot(
            start,
            blackStart,
            end,
            blackEnd,
            treeChildrenCount,
            snapshots);
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void restore(ParseStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        end                 = snapshot.end;
        blackEnd            = snapshot.blackEnd;
        treeChildrenCount   = snapshot.treeChildrenCount;

        tree.truncate(treeChildrenCount);

        for (int i = 0; i < customStates.length; i++) {
            if (customStates[i] != null) customStates[i].restore(snapshot.customSnapshots[i], this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void uncommit(ParseStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        treeChildrenCount   = snapshot.treeChildrenCount;

        for (int i = 0; i < customStates.length; ++i) {
            if (customStates[i] != null) customStates[i].uncommit(snapshot.customSnapshots[i], this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void discard()
    {
        end = start;
        blackEnd = blackStart;
        tree.truncate(treeChildrenCount);

        for (CustomState state: customStates) {
            if (state != null) state.discard(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void commit()
    {
        start = end;
        blackStart = blackEnd;
        treeChildrenCount = tree.childrenCount();

        for (CustomState state: customStates)  {
            if (state != null) state.commit(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public ParseChanges extract()
    {
        return new ParseChanges(
            end,
            blackEnd,
            tree.childrenFromIndex(treeChildrenCount),
            Array.map(customStates, x -> x == null ? null : x.extract(this)));
    }

    // ---------------------------------------------------------------------------------------------

    @Inline
    public void merge(ParseChanges changes)
    {
        end = changes.end;
        blackEnd = changes.blackEnd;

        if (changes.children != null)  {
            tree.addChildren(changes.children);
        }

        int size = changes.customChanges.size();
        for (int i = 0; i < size; ++i)  {
            if (customStates[i] != null) customStates[i].merge(changes.customChanges.get(i), this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("[%d/%d - %d/%d] tree(%d/%d) %s",
            start,
            blackStart,
            end,
            blackEnd,
            treeChildrenCount,
            tree.childrenCount(),
            recordErrors ? "" : "no_errors");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
