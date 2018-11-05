package edu.nmsu.imgflow;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class GraphSaveLoad {

    private static FileChooser chooser;

    private static void initChooser() {
        chooser = new FileChooser();
        chooser.setTitle("Imgflow Graph");
        chooser.getExtensionFilters().add(
            new ExtensionFilter("Imgflow Graphs", "*.imgflow")
        );
    }

    public static boolean chooseFileAndSaveGraph(Graph graph) {
        if (chooser == null) initChooser();
        File file = chooser.showSaveDialog(Main.getInstance().getStage());
        if (file == null) return false;
        String path = file.getAbsolutePath();
        return saveGraph(graph, path);
    }

    public static boolean saveGraph(Graph graph, String path) {
        NodeSummary[] summaries = new NodeSummary[graph.getNodes().size()];
        for (int i = 0; i < summaries.length; i++)
            summaries[i] = NodeSummary.fromNode(graph.getNodes().get(i), i);
        NodeSummary.summarizeConnections(summaries);
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
            writer.write(serializedGraph);
            writer.close();
            System.out.println("Successfully saved graph!");
        } catch (IOException e) {
            System.out.println("Error saving file!\n"+e.getMessage());
            return false;
        }
        return true;
    }

    private static class NodeSummary {

        private GraphNode           node;
        private int                 index;
        private String              typeName;
        private String              customName;
        private double              xPosition;
        private double              yPosition;
        private NodeConnection[]    connections;
        private String[]            propertyValues;

        private NodeSummary() {}

        public static NodeSummary fromNode(GraphNode node, int index) {
            NodeSummary summary = new NodeSummary();

            summary.node        = node;
            summary.index       = index;
            summary.typeName    = NodeFactory.typeFromBaseName(node.getBaseName());
            summary.customName  = node.getName();
            summary.xPosition   = node.getPosition().getX();
            summary.yPosition   = node.getPosition().getY();

            summary.connections = new NodeConnection[node.getNumOutputSockets()];
            for (int i = 0; i < summary.connections.length; i++)
                summary.connections[i] = new NodeConnection();

            summary.propertyValues = new String[node.getProperties().size()];
            for (int i = 0; i < summary.propertyValues.length; i++)
                summary.propertyValues[i] = node.getProperties().get(i).serializeValue();

            return summary;
        }

        public static void summarizeConnections(NodeSummary[] summaries) {
            for (NodeSummary summary : summaries) {
                GraphNode thisNode = summary.node;
                for (int i = 0; i < summary.connections.length; i++) {
                    NodeSocketInput otherSocket = thisNode.getOutputSockets().get(i).getConnectingSocket();
                    if (otherSocket == null) continue;
                    GraphNode otherNode     = otherSocket.getParentNode();
                    int otherSocketIndex    = otherNode.getInputSockkets().indexOf(otherSocket);
                    for (NodeSummary search : summaries) {
                        if (search.node != thisNode && search.node == otherNode) {
                            summary.connections[i].nodeIndex =   search.index;
                            summary.connections[i].socketIndex = otherSocketIndex;
                        }
                    } 
                }
            }
        }

        public String toString() {
            String string = index + "\n" + typeName + "\n" + customName + "\n" + xPosition + "\n" + yPosition + "\n";
            for (NodeConnection connection : connections)
                string += connection.nodeIndex + " " + connection.socketIndex + "\n";
            for (String value : propertyValues)
                string += value + "\n";
            return string;
        }

        private static class NodeConnection {
            public int nodeIndex    = -1;
            public int socketIndex  = 0;
        }
    }
}