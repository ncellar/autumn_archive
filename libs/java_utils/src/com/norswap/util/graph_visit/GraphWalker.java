package com.norswap.util.graph_visit;

import com.norswap.util.slot.Slot;

import java.util.List;

/**
 * Indicates how to acquire the children of a node; i.e. the nodes connected to links outgoing from
 * the node.
 * <p>
 * It is also responsible of indicating how graph transformations are to be done (if any) by
 * wrapping the nodes in adequate slot objects.
 */
@FunctionalInterface
public interface GraphWalker<Node>
{
    List<Slot<Node>> children(Node node, GraphVisitor<Node> visitor);
}
