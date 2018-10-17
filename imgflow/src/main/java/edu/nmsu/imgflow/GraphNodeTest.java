package edu.nmsu.imgflow;

/**
 * A type of graph node for testing purposes. Currently does literally nothing.
 */
public class GraphNodeTest extends GraphNode {

    private NodePropertySlider slider1, slider2;

    public GraphNodeTest() {
        slider1 = new NodePropertySlider(this, "Dummy Slider 1", 0, 10);
        slider2 = new NodePropertySlider(this, "Dummy Slider 2", 0, 100);

        properties.add(slider1);
        properties.add(slider2);
    }

    public String getName() { return "Test Node"; }

    public int getNumInputSockets() { return 3; }

    public int getNumOutputSockets() { return 5; }
}