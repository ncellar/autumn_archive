package com.norswap.util.graph_visit;

import com.norswap.util.Array;
import com.norswap.util.slot.ListSlot;
import com.norswap.util.slot.SelfSlot;
import com.norswap.util.slot.Slot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.norswap.util.graph_visit.NodeState.*;

/**
 * A graph visit defines a visit of a graph made of nodes with type {@code Node}.
 * <p>
 * The visit is defined via a set of callbacks invoked at various point of the visit, namely {@link
 * #before}, {@link #afterChild}, {@link #after}, {@link #afterRoot} and {@link #conclude}.
 * <p>
 * Lookup the documentation of {@link NodeState} to understand this parameter in the callbacks.
 * <p>
 * Some of the callback's parameter are wrapped in a {@link Slot} object. A slot combines a way to
 * retrieve a node with a consumer of node. In general a slot is meant as an assignable location. It
 * enables modifying the the graph in-place, or building a copy of the graph on the fly.
 * <p>
 * The visit is started by calling {@link #visit(Node)} (single root) or {@link
 * #visit(Collection<Node>)} (multiple roots). It is also possible to perform incremental visits by
 * repeatedly calling the {@link #partialVisit} methods. In the case of an incremental visit, {@link
 * #conclude} must be called manually.
 * <p>
 * A visitor is parameterized by a {@link GraphWalker} which indicates what the children of a node
 * are (i.e. the nodes connected to links outgoing from the node) and optionally how to modify the
 * graph (by supplying appropriate slot objects).
 */
public abstract class GraphVisitor<Node>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final GraphWalker<Node> walker;

    private Map<Node, NodeState> states = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GraphVisitor(GraphWalker<Node> walker)
    {
        this.walker = walker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when a node is visited, before attempting to visit its children.
     * <p>
     * This method has no state parameter, because its value would always be {@code FIRST_VISIT}.
     */
    public void before(Node node) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after attempting to visit each child of {@code parent}.
     * The child is held in {@code slot}.
     * The slot can be used to replace this child within its parent.
     */
    public void afterChild(Node parent, Slot<Node> slot, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after we have attempted to visit all the children of the node.
     * <p>
     * Possible values for {@code state}: {@code FIRST_VISIT}, {@code FIRST_VISIT_CYCLIC}, {@code
     * FIRST_VISIT_CUTOFF}.
     */
    public void after(Node node, List<Slot<Node>> children, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after attempting to visit a root node.
     * The node is held in the slot, which can used to replace the root node.
     * <p>
     * Possible values for {@code state}: all except {@code CUTOFF}.
     */
    public void afterRoot(Slot<Node> slot, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after the walk has concluded (all the roots have been visited). If you are performing
     * an incremental walk, you must call this method yourself.
     */
    public void conclude() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Node visit(Node node)
    {
        Slot<Node> out = partialVisit(node);
        conclude();
        states = null;
        return out.get();
    }

    // ---------------------------------------------------------------------------------------------

    public final Collection<Node> visit(Collection<Node> nodes)
    {
        Collection<Node> out = partialVisit(nodes);
        conclude();
        states = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public final Slot<Node> partialVisit(Node node)
    {
        Slot<Node> slot = new SelfSlot<>(node);
        afterRoot(slot, walk(node));
        return slot;
    }

    // ---------------------------------------------------------------------------------------------

    public final Collection<Node> partialVisit(Collection<Node> nodes)
    {
        Array<Node> container = new Array<>(nodes.size());

        int i = 0;
        for (Node node: nodes)
        {
            afterRoot(new ListSlot<>(container, i++).set(node), walk(node));
        }

        return container;
    }

    // ---------------------------------------------------------------------------------------------

    private NodeState walk(Node node)
    {
        switch (states.getOrDefault(node, FIRST_VISIT))
        {
            case FIRST_VISIT:
                states.put(node, CUTOFF);
                break;

            // Don't enter the node twice.

            case CUTOFF:
                states.put(node, FIRST_VISIT_CUTOFF);
                return CUTOFF;

            case FIRST_VISIT_CYCLIC:
                states.put(node, FIRST_VISIT_CUTOFF);
                return CUTOFF;

            case FIRST_VISIT_CUTOFF:
                return CUTOFF;

            case VISITED:
                return VISITED;
        }

        before(node);

        List<Slot<Node>> children = walker.children(node, this);

        boolean cyclic = false;

        for (Slot<Node> child: children)
        {
            NodeState childState = walk(child.get());
            cyclic = cyclic || childState == FIRST_VISIT_CYCLIC || childState == FIRST_VISIT_CUTOFF;
            afterChild(node, child, childState);
        }

        NodeState out = states.get(node);

        if (out == CUTOFF)
        {
            out = cyclic ? FIRST_VISIT_CYCLIC : FIRST_VISIT;
        }

        after(node, children, out);
        states.put(node, VISITED);

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
