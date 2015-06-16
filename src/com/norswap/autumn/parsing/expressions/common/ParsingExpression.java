package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.Registry;
import com.norswap.autumn.parsing.graph.nullability.Nullability;
import com.norswap.autumn.util.Caster;
import com.norswap.autumn.util.DeepCopy;
import com.norswap.autumn.util.Exceptions;
import com.norswap.autumn.util.HandleMap;

/**
 * A parsing expression is matched to the source text by recursively invoking the {@link #parse}
 * method of its sub-expressions on the source text; in a manner defined by parsing expression
 * flavour.
 *
 * {@link #parse} takes two parameters: the parser itself which supplies global context and some
 * parse state. In particular the parse state includes the position in the source text at which
 * to attempt the match.
 */
public abstract class ParsingExpression implements DeepCopy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int flags;
    public HandleMap ext = new HandleMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING

    // ---------------------------------------------------------------------------------------------

    public abstract void parse(Parser parser, ParseState state);

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(Parser parser, int position)
    {
        throw new UnsupportedOperationException(
            "Parsing expression class "
            + this.getClass().getSimpleName()
            + " doesn't support dumb parsing.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // STRING REPRESENTATION

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string representation for the expression. If the expression has a name, writes
     * that instead of a string representation of its content. This can hence be used to print
     * recursive expressions (a recursive expression must have a name).
     */
    @Override
    public final String toString()
    {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Similar to {@code builder.append(this.toString())}.
     * {@link #toString} uses this method internally.
     *
     * Implemented by writing the name if there is one or else by calling {@link #appendTo}.
     */
    public final void toString(StringBuilder builder)
    {
        String name = name();

        if (name != null)
        {
            builder.append(name);
        }
        else
        {
            appendTo(builder);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Like {@link #toString()}, but never writes the name of an expression instead of its content.
     * As a result, this *can not* be used to print recursive expressions.
     */
    public final String toStringFull()
    {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Appends a string representation of this expression (but never its name) to the builder.
     */
    public abstract void appendTo(StringBuilder builder);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TREE WALKING

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
    // NAME

    // ---------------------------------------------------------------------------------------------

    public final String name()
    {
        return ext.get(Registry.PEH_NAME);
    }

    // ---------------------------------------------------------------------------------------------

    public final void setName(String name)
    {
        ext.set(Registry.PEH_NAME, name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PROPERTIES

    public Nullability nullability()
    {
        return Nullability.no(this);
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] firsts()
    {
        return new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression clone()
    {
        ParsingExpression clone = Caster.cast(Exceptions.swallow(() -> super.clone()));
        clone.ext = ext.deepCopy();
        return clone;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Note: only run on parsing expression without loop, i.e. before resolving references.
     */
    @Override
    public ParsingExpression deepCopy()
    {
        return clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FLAG MANIPULATION

    // ---------------------------------------------------------------------------------------------

    public final boolean hasAnyFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public final boolean hasFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    // ---------------------------------------------------------------------------------------------

    public final void setFlags(int flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    // ---------------------------------------------------------------------------------------------

    public final void clearFlags(int flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
