package edu.nmsu.imgflow;

public class NodeSocketOutput extends NodeSocket {

    private NodeSocketInput connectingSocket;

    public NodeSocketOutput(GraphNode parent) {
        super(parent);
    }

    public NodeSocketInput getConnectingSocket() { return connectingSocket; }

    public void setConnectingSocket(NodeSocketInput inputSocket) { connectingSocket = inputSocket; }
}