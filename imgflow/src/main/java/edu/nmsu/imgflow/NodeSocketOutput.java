package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

/**
 * A NodeSocketOutput is a NodeSocket that sends data out of a node
 * to the NodeSocketInput of another node.
 */
public class NodeSocketOutput extends NodeSocket {

    /**
     * The input socket that this socket is connected to.
     * Or null if this socket is not connected
     */
    private NodeSocketInput connectingSocket;

    /**
     * Create a new NodeSocketOutput for the given node at the given index.
     */
    public NodeSocketOutput(GraphNode parent, int index) {
        super(parent, index);

        position = new Point2D(
            GraphNode.NODE_WIDTH - GraphNode.NODE_SOCKET_SIZE,
            GraphNode.NODE_ROW_PADDING + ((index + 1) * GraphNode.NODE_ROW_HEIGHT)
        );
    }

    /**
     * Get the input socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    public NodeSocketInput getConnectingSocket() { return connectingSocket; }

    /**
     * Connect this socket to the given input socket.
     * TODO: Check to make sure you aren't connecting to a socket on the same node
     */
    public void connect(NodeSocketInput inputSocket) { connectingSocket = inputSocket; }
}