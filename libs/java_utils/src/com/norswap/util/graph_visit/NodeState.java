package com.norswap.util.graph_visit;

/**
 * Possible states of a node in a graph being visited, whenever a method of {@link GraphVisitor} is
 * called.
 * <p>
 * Conceptually, {@link #FIRST_VISIT_CUTOFF} implies {@link #FIRST_VISIT_CYCLIC}, which implies
 * {@link #FIRST_VISIT}. The most specific version is always used.
 */
public enum NodeState
{
    /**
     * The node has just been visited for the first time.
     */
    FIRST_VISIT,

    /**
     * The node has just been visited for the first time, and it is part of at least one cycle.
     */
    FIRST_VISIT_CYCLIC,

    /**
     * The node has just been visited for the first time, and it is part of at least one cycle.
     * One or more of these cycles was cutoff at this node during the walk.
     */
    FIRST_VISIT_CUTOFF,

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
