package edu.nmsu.imgflow;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Abstract class for managing the saving/loading of graphs
 * to/from files. Graphs are saved in plaintext files with the
 * .imgflow extension. The files are formatted with the information
 * for every node in the graph listed with a blank line between each node.
 * The information for a single node is formatted like so:
 * 
 * [UID for the node (usually an integer index)]
 * [type of node ('filein','coloreffects', etc.)]
 * [X-position]
 * [Y-position]
 * [1st output socket connection]
 * [2nd output socket connection]
 * [...]
 * [1st property value]
 * [2nd property value]
 * [...]
 * 
 * The connection for an output socket consists of two integers
 * (space-separated) where the first integer is the UID of the node that
 * the socket is connected to (a node with this UID must be present elsewhere in the file)
 * and the second integer is the index of the other node's input socket that the socket
 * is connected to (index within the result of the other node's getInputSockets() call).
 * If an output socket is not connected to anything, the first integer will be -1 and the
 * second integer will be 0
 */
public abstract class GraphSaveLoad {

    /**
     * The FileChooser that saving/loading uses.
     * This must be initialized with initChooser() before
     * it can be used
     */
    private static FileChooser chooser;

    /**
     * Initialize the FileChooser and set to only
     * load .imgflow files
     */
    private static void initChooser() {
        chooser = new FileChooser();
        chooser.setTitle("Imgflow Graph");
        chooser.getExtensionFilters().add(
            new ExtensionFilter("Imgflow Graphs", "*.imgflow")
        );
    }

    /**
     * Open a save dialog and save the given graph to the selected file.
     * Returns a boolean representing whether or not saving succeeded.
     */
    public static boolean chooseFileAndSaveGraph(Graph graph) {
        // Init chooser if it hasn't been already
        if (chooser == null) initChooser();

        // Select file and save to it
        File file = chooser.showSaveDialog(Main.getInstance().getStage());
        if (file == null) return false;
        String path = file.getAbsolutePath();
        return saveGraph(graph, path);
    }

    /**
     * Save the given graph to the file at the given path.
     * Returns a boolean representing whether or not saving succeeded.
     */
    public static boolean saveGraph(Graph graph, String path) {
        // Create a list of node summaries for each node in the graph
        NodeSummary[] summaries = new NodeSummary[graph.getNodes().size()];
        for (int i = 0; i < summaries.length; i++)
            summaries[i] = NodeSummary.fromNode(graph.getNodes().get(i), i);
        // Set the connections in the node summaries
        NodeSummary.summarizeConnections(summaries);

        // Concat all the node summaries together to get the text
        // that will be saved to the file
        String serializedGraph = "";
        for (NodeSummary summary : summaries)
            serializedGraph += summary.toString() + "\n";
        try {
            // Open writer for file
            Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(path),
                    "UTF-8"
                ));
            // Write to file
            writer.write(serializedGraph);
            writer.close();
            System.out.println("Successfully saved graph!");
            return true;
        } catch (IOException e) {
            // If an IOException occurs, print and report that saving failed
            System.out.println("Error saving file!\n"+e.getMessage());
            return false;
        }
    }

    /**
     * An internal class used to summarize all of the information
     * for a GraphNode that can be written to or parsed from a string.
     */
    private static class NodeSummary {

        /**
         * The graph node that this summary refers to
         */
        private GraphNode           node;
        /**
         * The UID of this summary (unique within a list of summaries representing a graph)
         */
        private int                 index;
        /**
         * The type of the node. 'filein','coloreffects','matte',etc. This can be obtained
         * by calling NodeFactory.typeFromBaseName(myNode.getBaseName())
         */
        private String              typeName;
        /**
         * The x-coordinate of the node's position
         */
        private double              xPosition;
        /**
         * The y-coordinate of the node's position
         */
        private double              yPosition;
        /**
         * A NodeConnection object for each of the node's output sockets
         */
        private NodeConnection[]    connections;
        /**
         * A string representing the value of each of the node's properties
         */
        private String[]            propertyValues;

        /**
         * Private constructor. Use either the fromNode() or
         * fromString() factory methods
         */
        private NodeSummary() {}

        /**
         * Create a new NodeSummary summarizing the given node and with
         * the given index. Note that when created, the connections in the 
         * summary will all be as if the none of the node's output sockets
         * are connected to anything. The connections must be set for a whole
         * list of summaries using the summarizeConnections() method
         */
        public static NodeSummary fromNode(GraphNode node, int index) {
            // Create summary and set fields
            NodeSummary summary = new NodeSummary();
            summary.node        = node;
            summary.index       = index;
            summary.typeName    = NodeFactory.typeFromBaseName(node.getBaseName());
            summary.xPosition   = node.getPosition().getX();
            summary.yPosition   = node.getPosition().getY();

            // Create a new NodeConnection for each of the node's output sockets
            // These connections will be disconnected (nodeIndex of -1)
            summary.connections = new NodeConnection[node.getNumOutputSockets()];
            for (int i = 0; i < summary.connections.length; i++)
                summary.connections[i] = new NodeConnection();

            // Create a string for the value of each of the node's properties
            summary.propertyValues = new String[node.getProperties().size()];
            for (int i = 0; i < summary.propertyValues.length; i++)
                summary.propertyValues[i] = node.getProperties().get(i).serializeValue();

            return summary;
        }

        /**
         * For a list of node summaries, set the connections in each summary
         * so that each of the nodes' connections are represented in the summaries
         * according to the way their sockets are connected.
         */
        public static void summarizeConnections(NodeSummary[] summaries) {
            // Iterate through given summaries
            for (NodeSummary summary : summaries) {
                GraphNode thisNode = summary.node;
                // For each connection in the summary, find the socket and node
                // that it should be connected to
                for (int i = 0; i < summary.connections.length; i++) {
                    NodeSocketInput otherSocket = thisNode.getOutputSockets().get(i).getConnectingSocket();
                    if (otherSocket == null) continue;
                    GraphNode otherNode     = otherSocket.getParentNode();
                    int otherSocketIndex    = otherNode.getInputSockkets().indexOf(otherSocket);
                    // Find the other socket/node in the list of summaries and set the connection
                    // accordingly if it is found
                    for (NodeSummary search : summaries) {
                        if (search.node != thisNode && search.node == otherNode) {
                            summary.connections[i].nodeIndex =   search.index;
                            summary.connections[i].socketIndex = otherSocketIndex;
                        }
                    } // end for (search : summaries)
                } // end for (i( : summary.connections)
            } // end for (summary : summaries)
        }

        /**
         * Get the string representation of this node summary.
         * This is what will be written into files
         */
        public String toString() {
            String string = index + "\n" + typeName + "\n" + xPosition + "\n" + yPosition + "\n";
            for (NodeConnection connection : connections)
                string += connection.nodeIndex + " " + connection.socketIndex + "\n";
            for (String value : propertyValues)
                string += value + "\n";
            return string;
        }

        /**
         * Represents a socket's connection to another socket using the index
         * of another node (the UID of the node's summary) and the index
         * of the socket within that node.
         */
        private static class NodeConnection {
            public int nodeIndex    = -1;
            public int socketIndex  = 0;
        }
    }
}