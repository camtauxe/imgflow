package edu.nmsu.imgflow;

import java.util.ArrayList;

import javafx.geometry.Point2D;

/**
 * Represents a graph (pipeline) of nodes using a list of nodes.
 * The connections between the nodes are stored as properties
 * within the nodes themselves (or rather, their sockets)
 */
public class Graph {

    /**
     * The list of nodes in the graph.
     */
    private ArrayList<GraphNode> nodes;

    /**
     * The node that is currently selected, or null if no node is selected
     */
    private GraphNode selectedNode;

    /**
     * The list of registered node selection listeners, triggered
     * when the selection changes.
     */
    private ArrayList<NodeSelectListener> nodeSelectListeners;

    /**
     * Create a new, empty graph
     */
    public Graph() {
        nodes = new ArrayList<GraphNode>();
        nodeSelectListeners = new ArrayList<NodeSelectListener>();
    }

    /**
     * Create and return a new graph with some basic nodes already on it
     */
    public static Graph buildTestGraph() {
        Graph graph = new Graph();

        GraphNode in = new GraphNodeFileIn();
        GraphNode out = new GraphNodeFileOut();

        GraphNode colorEffects = new GraphNodeColorEffects();
        GraphNode invert = new GraphNodeInvert();
        GraphNode opacity = new GraphNodeOpacity();

        in.setPosition(new Point2D(-2.3, -0.3));
        out.setPosition(new Point2D(2.2, 0.3));
        invert.setPosition(new Point2D(0.0, 1.5));
        opacity.setPosition(new Point2D(0.0,-1.5));

        graph.getNodes().add(in);
        graph.getNodes().add(out);
        graph.getNodes().add(colorEffects);
        graph.getNodes().add(invert);
        graph.getNodes().add(opacity);

        return graph;
    }

    /**
     * Determine whether or connecting the two given sockets would be safe (as in
     * not creating a loop in the graph)
     */
    public boolean isConnectionSafe(NodeSocketOutput output, NodeSocketInput input) {
        GraphNode fromNode = output.getParentNode();
        GraphNode toNode = input.getParentNode();

        // Search for the first node from the second. If it is found,
        // there is a loop
        return !(searchForNode(toNode, fromNode));
    }

    /**
     * Recursively walk through the graph (downstream) from the given starting
     * node to find the given target node. Returns whether or not the target node
     * was found
     */
    private boolean searchForNode(GraphNode start, GraphNode target) {
        boolean found = false;
        for (NodeSocketOutput output : start.getOutputSockets()) {
            NodeSocketInput otherSocket = output.getConnectingSocket();
            if (otherSocket != null) {
                GraphNode otherNode = otherSocket.getParentNode();
                if (otherNode == target) {
                    found = true;
                    break;
                }
                found = found || searchForNode(otherNode, target);
            }
        }
        return found;
    }

    /**
     * Change the graph's selected node to the given node.
     * Give 'null' to deselect the currently selected node.
     * If the given node is already selected or is not in the
     * graph, this does nothing.
     */
    public void selectNode(GraphNode node) {
        if (selectedNode == node) { return; }

        if (node == null) {
            selectedNode = null;
            for (NodeSelectListener listener : nodeSelectListeners)
                listener.handle(selectedNode);
        }
        else if (nodes.contains(node)) {
            selectedNode = node;
            for (NodeSelectListener listener : nodeSelectListeners)
                listener.handle(selectedNode);
        }
    }

    /**
     * Add a node select listener to this viewport. It will be invoked
     * whenever the selected node changes (this includes when a node
     * is deselected)
     */
    public void addNodeSelectListener(NodeSelectListener listener) {
        nodeSelectListeners.add(listener);
    }

    /**
     * Get the currently selected node, or null if no node
     * in the graph is selected
     */
    public GraphNode getSelectedNode() { return selectedNode;}

    /**
     * Get the list of nodes in the graph
     */
    public ArrayList<GraphNode> getNodes() { return nodes; }
}