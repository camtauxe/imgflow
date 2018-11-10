package edu.nmsu.imgflow;

/**
 * A type of graph node for testing purposes. Currently does literally nothing.
 */
public class GraphNodeTest extends GraphNode {

    private NodePropertySlider slider1, slider2;
    private NodePropertySpinner spinner1;

    public GraphNodeTest() {
        slider1 = new NodePropertySlider(this, "Dummy Slider 1", 0, 10, 0);
        slider2 = new NodePropertySlider(this, "Dummy Slider 2", 0, 100, 0);
        spinner1 = new NodePropertySpinner(this, "Dummy Spinner", 0, 100, 50);

        properties.add(slider1);
        properties.add(slider2);
        properties.add(spinner1);
    }

    public String getName() { return "Test Node"; }

    public int getNumInputSockets() { return 3; }

    public int getNumOutputSockets() { return 5; }
}