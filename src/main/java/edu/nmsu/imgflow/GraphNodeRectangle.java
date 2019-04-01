package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

/**
 * A type of graph node that generates a rectangle of
 * a single color and outputs it
 */
public class GraphNodeRectangle extends GraphNode {

    private NodeSocketOutput out;

    private NodePropertySpinner widthSpinner  = new NodePropertySpinner(this, "Width",  0, 4500, 100);
    private NodePropertySpinner heightSpinner = new NodePropertySpinner(this, "Height", 0, 4500, 100);
    private NodePropertyColor   colorSelect   = new NodePropertyColor(this, "Color", Color.BLACK);

    public GraphNodeRectangle() {
        out = outputSockets.get(0);

        properties.add(colorSelect);
        properties.add(widthSpinner);
        properties.add(heightSpinner);
    }

    public String getBaseName() { return "Rectangle"; }

    public int getNumInputSockets() { return 0; }

    public String getDescription() {
        return "Create a rectangle and send it the to output.";
    }

    /**
     * Override processImage to fill a new image with
     * the selected Color
     */
    public void processImage() {
        int width   = widthSpinner.getValue();
        int height  = heightSpinner.getValue();
        Color color = colorSelect.getValue();

        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(width, height);
        PixelWriter     writer = outImg.getPixelWriter();

        // Iterate through pixels and set color
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                writer.setColor(x, y, color);
            }
        }

        // Send to output socket
        out.setImage(outImg);
    }
}
