package edu.nmsu.imgflow;

/**
 * A type of graph node that writes the image data
 * from its input and saves the image to disk.
 */
public class GraphNodeFileOut extends GraphNode {

    private NodePropertyFileOut prop;

    public GraphNodeFileOut() {
        super();
        prop = new NodePropertyFileOut(this);
        properties.add(prop);
    }

    public NodeSocketInput getInputSocket() {
        return inputSockets.get(0);
    }

    public String getBaseName() { return "File OUT"; }

    public int getNumOutputSockets() {return 0; }
}