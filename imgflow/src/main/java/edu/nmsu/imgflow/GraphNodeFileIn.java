package edu.nmsu.imgflow;

/**
 * A type of graph node that reads a file from disk and
 * sends it to its one output
 */
public class GraphNodeFileIn extends GraphNode {

    private NodePropertyFileIn prop;
    private NodeSocketOutput socket;

    public GraphNodeFileIn() {
        super();
        prop = new NodePropertyFileIn(this);
        properties.add(prop);
        socket = outputSockets.get(0);
    }

    public String getBaseName() { return "File IN"; }

    public int getNumInputSockets() {return 0; }

    public void processImage() {
        socket.setImage(prop.getValue());
    }
}