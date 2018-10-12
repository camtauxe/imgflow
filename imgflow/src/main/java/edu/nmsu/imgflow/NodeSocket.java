package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

/**
 * A NodeSocket represents a point on a node that can be connected to
 * a socket on another node. This class is abstract and must be subclassed
 * as either a NodeSocketInput or NodeSocketOutput to be instantiated.
 */
public abstract class NodeSocket {

    // ################################
    // # INSTANCE VARIABLES
    // ################################

    /**
     * The GraphNode that this socket belongs to
     */
    protected GraphNode parentNode;
    /**
     * The index of this socket within its parent node'

    public int getIndex() { return index; }s inputSocket or
     * outputSocket list. (This is NOT it's index within the parent node's
     * allSockets list!!)
     */
    protected int index;
    /**
     * The position of the upper-left corner of this node when drawn on the viewport
     * (relative to the parent node's position, in graph units)
     */
    protected Point2D position;

    /**
     * The position a connector line touches on the socket when drawn on the viewport.
     * (relative the the parent node's position, in graph units)
     */
    protected Point2D connectingPosition;

    // ################################
    // # CONSTRUCTOR
    // ################################

    /**
     * Create a new NodeSocket with the given parent and index
     */
    public NodeSocket(GraphNode parent, int index) {
        parentNode = parent;
        this.index = index;
    }

    // ################################
    // # PUBLIC METHODS
    // ################################

    public void connect(NodeSocket otherSocket) {
        /** This should be overriden by a derived class */
    }

    public void disconnect() {
        /** This should be overriden by a derived class */
    }

    // ################################
    // # GETTERS/SETTERS
    // ################################

    /**
     * Get the node that this socket belongs to
     */
    public GraphNode getParentNode() { return parentNode; }

    /**
     * Get the position of the upper-left corner of this node when drawn on
     * the viewport (relative to the parent node's position, in graph units)
     */
    public Point2D getPosition() { return position; }

    /**
     * Get the position a connector line touches on the socket when drawn on the viewport.
     * (relative the the parent node's position, in graph units)
     */
    public Point2D getConnectingPosition() { return connectingPosition; }
}