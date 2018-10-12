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

        // An input socket is drawn along the node's left edge
        position = new Point2D(
            0.0,
            GraphNode.NODE_ROW_PADDING + ((index + 1) * GraphNode.NODE_ROW_HEIGHT)
        );
        // The connectig position for a input node is along the socket's right edge
        connectingPosition = position.add(0.0, GraphNode.NODE_SOCKET_SIZE/2.0);
    }

    /**
     * Connect this socket to the given socket.
     * This will also call connect() on the other socket
     */
    public void connect(NodeSocket otherSocket) {
        // If this socket is already connected to something, disconnect it
        if (connectingSocket != null)
            disconnect();
        if (parentNode != otherSocket.getParentNode() && otherSocket instanceof NodeSocketOutput) {
            NodeSocketOutput outputSocket = (NodeSocketOutput)otherSocket;
            connectingSocket = outputSocket;
            // Re-connect the other socket if it hasn't been connected already
            if (outputSocket.getConnectingSocket() != this)
                outputSocket.connect(this);
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

        NodeSocketOutput outputSocket = connectingSocket;
        connectingSocket = null;
        // Disconnect the other socket if it hasn't been disconnected already
        if (outputSocket.getConnectingSocket() != null)
            outputSocket.disconnect();
    }

    /**
     * Get the output socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    public NodeSocketOutput getConnectingSocket() { return connectingSocket; }

}