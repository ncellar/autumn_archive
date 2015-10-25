package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.graph.Copier;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.graph.Printer;
import com.norswap.util.DeepCopy;

import java.util.function.Predicate;

/**
 * A parsing expression is matched to the source text by recursively invoking the {@link #parse}
 * method of its sub-expressions on the source text; in a manner defined by parsing expression
 * flavour.
 * <p>
 * {@link #parse} takes two parameters: the parser itself which supplies global context and some
 * parse state. In particular the parse state includes the position in the source text at which to
 * attempt the match.
 */
public abstract class ParsingExpression implements DeepCopy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;
    public int flags;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING

    // ---------------------------------------------------------------------------------------------

    public abstract void parse(Parser parser, ParseState state);

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(Parser parser, int position)
    {
        throw new UnsupportedOperationException(
            "Parsing expression [" + this + "] of class "
            + this.getClass().getSimpleName()
            + " doesn't support dumb parsing.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // STRING REPRESENTATION

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing information about the expression that isn't contained in its
     * type or its children.
     */
    public String ownDataString()
    {
        return "";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing information about a child expression that isn't contained the
     * child expression itself.
     */
    public String childDataString(int position)
    {
        return "";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @see {@link #toStringShort()}
     */
    @Override
    public String toString()
    {
        return toString(true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prints the shortest possible representation of this expression.
     *
     * If it has the name, use this name; otherwise print its structure cutting off the recursion
     * as soon as named nodes are encountered.
     */
    public final String toStringShort()
    {
        return toString(true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prints the structure of the expression, cutting off the recursion as soon as named nodes are
     * encountered, except for this expression itself.
     */
    public final String toStringUnroll()
    {
        return toString(true, false);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prints the expression in full, with all its descendants. Recursion is indicated as such.
     * No expression is printed more than once, references like "visited(EXPRESSION_NAME)" are
     * used after the first time.
     */
    public final String toStringFull()
    {
        return toString(false, false);
    }

    // ---------------------------------------------------------------------------------------------

    private String toString(boolean cutoffAtNames, boolean cutoffAtOwnName)
    {
        StringBuilder builder = new StringBuilder();
        new Printer(builder::append, cutoffAtNames, cutoffAtOwnName).visit(this);
        // delete trailing newline
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GRAPH WALKING

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] children()
    {
        return new ParsingExpression[0];
    }

    // ---------------------------------------------------------------------------------------------

    public void setChild(int position, ParsingExpression pe)
    {
        throw new UnsupportedOperationException(
            "Parsing expression class "
            + this.getClass().getSimpleName()
            + " doesn't have children or doesn't support setting them.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PROPERTIES

    public Nullability nullability()
    {
        return Nullability.no(this);
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COPYING

    /**
     * Parsing expression must use this method to perform a deep copy of their own data in place.
     * They must not copy their extension objects, nor their children. They must, however, copy any
     * wrapper around their children (e.g. an array), or the copy will overwrite the original
     * parsing expression as well!
     * <p>
     * This is used by {@link #deepCopy()} and called right after a parsing expression was cloned.
     */
    public void copyOwnData() {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Do not use; use {@link #deepCopy()} instead.
     */
    @Override
    public final ParsingExpression clone()
    {
        try {
            return (ParsingExpression) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error(e); // shouldn't happen
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Performs a deep copy of the parsing expression. This is aware of recursion, and parsing
     * expression identity: it will never make two copies of the same descendant of this parsing
     * expression.
     */
    @Override
    public final ParsingExpression deepCopy()
    {
        return new Copier().visit(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
