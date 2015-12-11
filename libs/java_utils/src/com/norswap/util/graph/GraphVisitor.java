package com.norswap.util.graph;

import com.norswap.util.Array;
import com.norswap.util.Int;

import java.util.Collection;
import java.util.HashMap;

import static com.norswap.util.graph.NodeState.*;

/**
 * A graph visit defines a visit of a graph made of nodes with type {@code Node}.
 * <p>
 * The visit is defined via a set of callbacks invoked at various point of the visit, namely {@link
 * #before}, {@link #afterChild}, {@link #after}, {@link #afterRoot} and {@link #conclude}.
 * <p>
 * Instead of passing the nodes directly, we pass a {@link Slot} object. These make available the
 * original value of the node ({@link Slot#initial}), and make it possible to indicate that we wish
 * to replace the node with another (by assigning the {@link Slot#assigned} slot. These changes will
 * not be effected until the end of the walk, and their semantics is determined by the
 * implementation of the {@link #applyChanges} method.
 * <p>
 * The visit is started by calling {@link #visit(Node)} (single root) or {@link #visit(Collection)}
 * (multiple roots). It is also possible to perform incremental visits by repeatedly calling the
 * {@link #partialVisit} methods. In the case of an incremental visit, {@link #conclude} must be
 * called manually.
 * <p>
 * Implementation of this class must override the {@link #children(Node))} method, which is
 * responsible to indicate what the children of the passed node are. You can play with this method
 * to change the behaviour of the walk (e.g. only walk over nodes of interest).
 * <p>
 * Visitors are meant to be single-use: they can maintain state within the instance. To perform
 * another visit, create another instance.
 */
public abstract class GraphVisitor<Node>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<Node, NodeState> states = new HashMap<>();

    private Array<Slot<Node>> modified = new Array<>();

    private boolean cutoff;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when a node is visited, before attempting to visit its children. Guaranteed to be
     * called only once per node.
     */
    public void before(Slot<Node> node) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after attempting to visit each child of {@code parent} (after calling {@link #after}
     * with the child itself, if that hasn't been done already).
     */
    public void afterChild(Slot<Node> parent, Slot<Node> child, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after we have attempted to visit all the children of the node. Guaranteed to be called
     * only once per node.
     */
    public void after(Slot<Node> node, Array<Slot<Node>> children) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after attempting to visit a root node. {@code state} can be {@link
     * NodeState#FIRST_VISIT} or {@link NodeState#VISITED}.
     */
    public void afterRoot(Slot<Node> root, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after the walk has concluded (all the roots have been visited). If you are performing
     * an incremental walk, you must call this method yourself. If you override it, you must
     * call the super-method.
     */
    public void conclude()
    {
        states = null;
        applyChanges(modified);
        modified = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Node visit(Node node)
    {
        Slot<Node> out = partialVisit(node);
        conclude();
        return out.latest();
    }

    // ---------------------------------------------------------------------------------------------

    public final Array<Node> visit(Collection<Node> nodes)
    {
        Array<Slot<Node>> out = partialVisit(nodes);
        conclude();
        return out.map(Slot::latest);
    }

    // ---------------------------------------------------------------------------------------------

    public final Slot<Node> partialVisit(Node node)
    {
        Slot<Node> slot = new Slot<>(node);
        partialVisit(slot);
        return slot;
    }

    // ---------------------------------------------------------------------------------------------

    public final Array<Slot<Node>> partialVisit(Collection<Node> nodes)
    {
        Array<Slot<Node>> array = Array.map(nodes, Slot::new);
        array.forEach(this::partialVisit);
        return array;
    }

    // ---------------------------------------------------------------------------------------------

    private void partialVisit(Slot<Node> slot)
    {
        afterRoot(slot, walk(slot));

        if (slot.assigned != null)
            modified.add(slot);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * This can be called from {@link #before} in order to specify that the children of the node
     * shouldn't be visited.
     */
    public final void cutoff()
    {
        cutoff = true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Call this to mark some nodes as visited, no as not to visit their descendants (at least not
     * through them).
     */
    public final void markVisited(Node node)
    {
        states.put(node, VISITED);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private NodeState walk(Slot<Node> node)
    {
        Node initial = node.initial;

        switch (states.getOrDefault(initial, FIRST_VISIT))
        {
            case FIRST_VISIT:
                states.put(initial, CUTOFF);
                break;

            // Don't enter the node twice.

            case CUTOFF:
                return CUTOFF;

            case VISITED:
                return VISITED;
        }

        before(node);

        Array<Slot<Node>> children = getChildren(initial);

        for (Slot<Node> child: children)
        {
            NodeState childState = walk(child);
            afterChild(node, child, childState);

            if (child.assigned != null)
            {
                modified.add(child);
            }
        }

        after(node, children);
        states.put(node.initial, VISITED);

        return FIRST_VISIT;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Array<Slot<Node>> getChildren(Node node)
    {
        if (cutoff)
        {
            cutoff = false;
            return Array.empty();
        }

        Int c = new Int();
        return Array.map(children(node), x -> new Slot<>(x, node, c.i++));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the children of the passed node to visit.
     */
    protected abstract Iterable<Node> children(Node node);

    // ---------------------------------------------------------------------------------------------

    /**
     * Given an array of slots which have been assigned, perform these assignments. For root slots,
     * no further changes is usually necessary (since the client will possess a reference to those
     * slot, he can query them directly).
     */
    protected void applyChanges(Array<Slot<Node>> modified) {}

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
