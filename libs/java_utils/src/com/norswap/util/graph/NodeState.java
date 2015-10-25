package com.norswap.util.graph;

/**
 * Parameter to {@link GraphVisitor#afterRoot}.
 */
public enum NodeState
{
    /**
     * The node has just been visited for the first time.
     */
    FIRST_VISIT,

    /**
     * The node has already been visited earlier in the walk.
     */
    VISITED,

    /**
     * The node is currently being visited. This means we have encountered a cycle in the graph
     * and that have fully walked this cycle.
     */
    CUTOFF
}
