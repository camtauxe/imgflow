package edu.nmsu.imgflow;

public class NodeSocketInput extends NodeSocket {

    private NodeSocketOutput connectingSocket;

    public NodeSocketInput(GraphNode parent) {
        super(parent);
    }

    public NodeSocketOutput getConnectingSocket() { return connectingSocket; }

    public void setConnectingSocket(NodeSocketOutput outputSocket) { connectingSocket = outputSocket; }
}