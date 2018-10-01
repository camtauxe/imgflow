package edu.nmsu.imgflow;

/**
 * A type of graph node for testing purposes. Currently does literally nothing.
 */
public class GraphNodeTest extends GraphNode {

    private NodePropertySlider slider1, slider2;

    public GraphNodeTest() {
        super();

        slider1 = new NodePropertySlider("Dummy Slider 1", 0, 10);
        slider2 = new NodePropertySlider("Dummy Slider 2", 0, 100);

        properties.add(slider1);
        properties.add(slider2);
    }

    public String getName() { return "Test Node"; }
}