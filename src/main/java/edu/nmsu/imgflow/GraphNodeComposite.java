package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that overlays one image on top of another
 */
public class GraphNodeComposite extends GraphNode {

    private NodeSocketInput top;
    private NodeSocketInput bottom;
    private NodeSocketOutput out;
    
    private NodePropertySpinner xOffsetSpinner, yOffsetSpinner;
    private NodePropertySlider opacitySlider;

    public GraphNodeComposite() {
        top     = inputSockets.get(0);
        bottom  = inputSockets.get(1);
        out     = outputSockets.get(0);
        
        opacitySlider = new NodePropertySlider(this, "Blend (%)", 0, 100, 100);
        xOffsetSpinner = new NodePropertySpinner(this, "Horizontal Offset (pixels)", 0, 10000, 10);
        yOffsetSpinner = new NodePropertySpinner(this, "Vertical Offset (pixels)", 0, 10000, 10);
        
        properties.add(opacitySlider);
        properties.add(xOffsetSpinner);
        properties.add(yOffsetSpinner);
    }
    
    public String getBaseName() { return "Composite"; }
    
    public String getDescription() {
        return "Superimpose one image on top of another. Use the offset to adjust the position of the top image.";
    }

    /**
     * Override processImage to composite the two images.
     */
    public void processImage() {
        // Get input images information
        Image topImg    = top.getImage();
        Image bottomImg = bottom.getImage();
        // If either input is null, clear the output and finish
        if (topImg == null || bottomImg == null) {
            out.setImage(null);
            return;
        }
        
        // Top image info
        int topWidth  = (int)topImg.getWidth();
        int topHeight = (int)topImg.getHeight();
        PixelReader topReader = topImg.getPixelReader();
        
        // Bottom image info
        int bottomWidth  = (int)bottomImg.getWidth();
        int bottomHeight = (int)bottomImg.getHeight();
        PixelReader bottomReader = bottomImg.getPixelReader();

        // Create a new writable image for the output
        WritableImage outImg = new WritableImage(bottomWidth, bottomHeight);
        PixelWriter writer   = outImg.getPixelWriter();
        
        // get offset values
        int xOffset = xOffsetSpinner.getValue();
        int yOffset = yOffsetSpinner.getValue();

        // Get opacity
        double opacity = opacitySlider.getValue() / 100.0;
        
        // Iterate through pixels
        for(int x = 0; x < bottomWidth; x++) {
            for(int y = 0; y < bottomHeight; y++) {
                Color bottomColor  = bottomReader.getColor(x, y);

                // Determine if the two images overlap at this point
                boolean overlapping = (
                    x >= xOffset && x < xOffset + topWidth &&
                    y >= yOffset && y < yOffset + topHeight
                );

                // Blend the colors if there is overlap
                if (overlapping) {
                    Color topColor = topReader.getColor(x - xOffset, y - yOffset);
                    Color newColor = bottomColor.interpolate(topColor, opacity * topColor.getOpacity());
                    writer.setColor(x, y, newColor);
                } else {
                    // Otherwise, just copy bottom color
                    writer.setColor(x, y, bottomColor);
                }
            }
        }
        // Send to output socket
        out.setImage(outImg);
    }
    public int getNumInputSockets()  { return 2; }
    public int getNumOutputSockets() { return 1; }
}