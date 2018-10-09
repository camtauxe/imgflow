package edu.nmsu.imgflow;

public class NodeSocket {

    private GraphNode parentNode;

    public NodeSocket(GraphNode parent) {
        parentNode = parent;
    }

    public GraphNode getParentNode() { return parentNode; }
}