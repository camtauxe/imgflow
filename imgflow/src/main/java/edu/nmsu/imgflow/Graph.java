package edu.nmsu.imgflow;

import java.util.ArrayList;

import javafx.geometry.Point2D;

/**
 * Represents a graph (pipeline) of nodes using a list of nodes
 * and the connections between them (not yet implemented)
 */
public class Graph {

    /**
     * The list of nodes in the graph.
     */
    private ArrayList<GraphNode> nodes;

    /**
     * Create a new, empty graph
     */
    public Graph() {
        nodes = new ArrayList<GraphNode>();
    }

    /**
     * Create and return a new graph with some test nodes already on it.
     */
    public static Graph buildTestGraph() {
        Graph graph = new Graph();

        GraphNode node1 = new GraphNodeTest();
        node1.setPosition(new Point2D(-2.0, -1.0));
        GraphNode node2 = new GraphNodeTest();
        node2.setPosition(new Point2D(2.0, -3.0));
        GraphNode node3 = new GraphNodeTest();
        node3.setPosition(new Point2D(2.5, 1.0));

        graph.getNodes().add(node1);
        graph.getNodes().add(node2);
        graph.getNodes().add(node3);

        return graph;
    }

    /**
     * Get the list of nodes in the graph
     */
    public ArrayList<GraphNode> getNodes() { return nodes; }
}