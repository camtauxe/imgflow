package edu.nmsu.imgflow;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

public abstract class GraphSaveLoad {

    public static boolean saveGraph(Graph graph, String path) {
        try {
            // Open writer for file
            Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(path),
                    "UTF-8"
                ));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file!\n"+e.getMessage());
            return false;
        }
        return true;
    }

    private static class NodeSummary {

        private int         index;
        private String      typeName;
        private String      customName;
        private double      xPosition;
        private double      yPosition;
        private int[]       connections;
        private String[]    propertyValues;

        private NodeSummary() {}

        public static NodeSummary fromNode(int index) {
            NodeSummary summary = new NodeSummary();
            summary.index = index;

            return summary;
        }
    }
}