package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.SnapshotParameters;

/**
 * A type of graph node that generates an ellipse of
 * a single color and outputs it
 */
public class GraphNodeEllipse extends GraphNode {

    private NodeSocketOutput out;

    private NodePropertySpinner widthSpinner  = new NodePropertySpinner(this, "Width",  0, 4500, 100);
    private NodePropertySpinner heightSpinner = new NodePropertySpinner(this, "Height", 0, 4500, 100);
    private NodePropertyColor   colorSelect   = new NodePropertyColor(this, "Color", Color.BLACK);

    private SnapshotParameters params;

    public GraphNodeEllipse() {
        out = outputSockets.get(0);

        properties.add(colorSelect);
        properties.add(widthSpinner);
        properties.add(heightSpinner);

        params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
    }

    public String getBaseName() { return "Ellipse"; }

    public int getNumInputSockets() { return 0; }

    public String getDescription() {
        return "Create an ellipse and send it the output.";
    }

    /**
     * Override processImage to create the ellipse
     */
    public void processImage() {
        int width   = widthSpinner.getValue();
        int height  = heightSpinner.getValue();
        Color color = colorSelect.getValue();

        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(width, height);

        // Create a canvas and draw the ellipse on it
        Canvas canvas = new Canvas(width, height);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.setFill(color);
        ctx.fillOval(0, 0, width, height);
        // Snapshot canvas
        canvas.snapshot(params, outImg);

        // Send to output socket
        out.setImage(outImg);
    }
}
