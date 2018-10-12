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

        // An output socket is drawn along the node's right edge
        position = new Point2D(
            GraphNode.NODE_WIDTH - GraphNode.NODE_SOCKET_SIZE,
            GraphNode.NODE_ROW_PADDING + ((index + 1) * GraphNode.NODE_ROW_HEIGHT)
        );
        // The connecting position for an output node is along the socket's right edge
        connectingPosition = position.add(GraphNode.NODE_SOCKET_SIZE, GraphNode.NODE_SOCKET_SIZE/2.0);
    }

    /**
     * Connect this socket to the given socket.
     * This will also call connect() on the other socket
     */
    public void connect(NodeSocket otherSocket) {
        // If this socket is already connected to something, disconnect it
        if (connectingSocket != null)
            disconnect();
        if (parentNode != otherSocket.getParentNode() && otherSocket instanceof NodeSocketInput) {
            NodeSocketInput inputSocket = (NodeSocketInput)otherSocket;
            connectingSocket = inputSocket;
            // Re-connect the other socket if it hasn't been connected already
            if (inputSocket.getConnectingSocket() != this)
                inputSocket.connect(this);
        }
        // TODO: If unable to connect, generate some kind of error so the user knows why this failed
    }

    /**
     * Disconnect this socket from the other socket if it is connected.
     * This will also call disconnect() on the other socket
     */
    public void disconnect() {
        // If the socket is already not connected to anything, no nothing
        if (connectingSocket == null) return;

        NodeSocketInput inputSocket = connectingSocket;
        connectingSocket = null;
        // Disconnect the other socket if it hasn't been disconnected already
        if (inputSocket.getConnectingSocket() != null)
            inputSocket.disconnect();
    }

    /**
     * Get the input socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    public NodeSocketInput getConnectingSocket() { return connectingSocket; }

}