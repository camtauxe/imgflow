package edu.nmsu.imgflow;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;

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

            propagateUpdate();
        }
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

        propagateUpdate();
    }

    /**
     * Indicate that this socket's data has updated (either by connecting, disconnecting,
     * or having a connected output socket update)
     */
    public void propagateUpdate() {
        parentNode.onInputUpdate(this);
    }

    /**
     * Get whether or not this input socket's data is out-of-date with the
     * state of the graph. This checks the needsUpdate flag of the connected
     * output socket. If this socket is disconnected, then it is considered
     * up-to-date as the incoming data will always be null
     */
    public boolean needsUpdate() {
        if (connectingSocket == null)
            return false;
        return connectingSocket.needsUpdate();
    }

    /**
     * Get the socket's image data up-to-date by causing all up-stream
     * nodes to update and re-process their data.
     */
    public void requestUpdate() {
        if (needsUpdate()) {
            connectingSocket.getParentNode().update();
        }
    }

    /**
     * Get the image data from this socket's connected output socket.
     * Or null if this socket is disconnected. Note that even if this socket
     * is disconnected, the image data from the output socket may still be null
     */
    public WritableImage getImage() {
        if (connectingSocket == null)
            return null;
        return connectingSocket.getImage();
    }

    /**
     * Get the output socket that this socket is connected to.
     * Or null if this socket is not connected to anything.
     */
    public NodeSocketOutput getConnectingSocket() { return connectingSocket; }

}