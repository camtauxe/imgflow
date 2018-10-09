package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

/**
 * A NodeSocketInput represents a NodeSocket that reads data into a node
 * from an output socket on another node.
 */
public class NodeSocketInput extends NodeSocket {

    /**
     * The output socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    private NodeSocketOutput connectingSocket;

    /**
     * Create a new NodeSocketInput in the given node with the given index
     */
    public NodeSocketInput(GraphNode parent, int index) {
        super(parent, index);

        position = new Point2D(
            0.0,
            GraphNode.NODE_ROW_PADDING + ((index + 1) * GraphNode.NODE_ROW_HEIGHT)
        );
    }

    /**
     * Get the output socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    public NodeSocketOutput getConnectingSocket() { return connectingSocket; }

    /**
     * Connect this socket to the given output socket.
     * TODO: Check to make sure you aren't connecting to a socket on the same node
     */
    public void setConnectingSocket(NodeSocketOutput outputSocket) { connectingSocket = outputSocket; }
}