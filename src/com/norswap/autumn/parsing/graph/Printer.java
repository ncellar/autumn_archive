package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Strings;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.List;
import java.util.function.Consumer;

/**
 * Prints a parsing expression as a tree. Useful for debugging.
 */
public class Printer extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Consumer<String> sink;

    private int depth = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Printer(Consumer<String> sink)
    {
        super(Walks.inPlace);
        this.sink = sink;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        String name = pe.name();
        String data = pe.ownPrintableData();

        sink.accept(Strings.times(depth, "-|"));
        sink.accept(pe.getClass().getSimpleName());
        sink.accept(" (");

        if (name != null)
        {
            sink.accept(name);
            sink.accept(" - defined");
        }
        else
        {
            sink.accept(String.format("%X", pe.hashCode()));
        }

        sink.accept(")");

        if (!data.isEmpty())
            sink.accept(" [" + data + "]");

        sink.accept("\n");

        ++ depth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(ParsingExpression parent, Slot<ParsingExpression> slot, NodeState state)
    {
        switch (state)
        {
            case CUTOFF:
                sink.accept(Strings.times(depth, "-|"));
                sink.accept("recursive (" + slot.get().name());
                sink.accept(")\n");
                break;

            case VISITED:
                String name = slot.get().name();
                sink.accept(Strings.times(depth, "-|"));
                sink.accept("visited (");
                sink.accept(name != null
                    ? name
                    : String.format("%X", slot.get().hashCode()));
                sink.accept(")\n");
                break;

            default:
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(ParsingExpression parsingExpression, List<Slot<ParsingExpression>> children, NodeState state)
    {
        -- depth;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
