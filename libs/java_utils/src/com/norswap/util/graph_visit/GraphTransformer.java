package com.norswap.util.graph_visit;

import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.function.Function;

/**
 * A graph transformer is a graph visitor which transforms its graph (using the slots mechanism). To
 * that effect it defines one abstract method ({@link #transform}) which is called from {@link
 * #afterChild} and {@link #afterRoot} to effect the change.
 * <p>
 * You can override all the graph visitor callbacks, but you should always call the super method
 * within your overrides.
 * <p>
 * The {@link GraphTransformer.Unique} nested class adds an additional feature: the transformation
 * of each node is computed exactly once and then cached, meaning that only one transformation will
 * be performed, even if the node has multiple parents.
 */
public abstract class GraphTransformer<Node> extends GraphVisitor<Node>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static <Node> GraphTransformer<Node> from(Function<Node, Node> f, GraphWalker<Node> walker)
    {
        return new GraphTransformer<Node>(walker)
        {
            @Override
            public Node transform(Node node)
            {
                return f.apply(node);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GraphTransformer(GraphWalker<Node> walker)
    {
        super(walker);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static abstract class Unique<Node> extends GraphTransformer<Node>
    {
        private HashMap<Node, Node> transformations = new HashMap<>();

        public Unique(GraphWalker<Node> walker)
        {
            super(walker);
        }

        @Override
        public final Node transform(Node node)
        {
            return transformations.computeIfAbsent(node, this::doTransform);
        }

        protected abstract Node doTransform(Node node);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract Node transform(Node node);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void afterChild(Node parent, Slot<Node> slot, NodeState state)
    {
        slot.set(transform(slot.get()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<Node> slot, NodeState state)
    {
        slot.set(transform(slot.get()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
