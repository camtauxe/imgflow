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

        GraphNode in = new GraphNodeFileIn();
        GraphNode out = new GraphNodeFileOut();

        out.setPosition(new Point2D(2.0, 1.0));

        graph.getNodes().add(in);
        graph.getNodes().add(out);

        return graph;
    }

    /**
     * Get the list of nodes in the graph
     */
    public ArrayList<GraphNode> getNodes() { return nodes; }
}