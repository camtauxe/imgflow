package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that takes two images and uses the top one as a
 * luma matte to mask out parts of the bottom image.
 */
public class GraphNodeMatte extends GraphNode {
 
    // References to sockets
    private NodeSocketInput     matte;
    private NodeSocketInput     base;
    private NodeSocketOutput    out;
    
    public GraphNodeMatte() {
       matte    = inputSockets.get(0);
       base     = inputSockets.get(1);
       out      = outputSockets.get(0);
    }
    
    public String getBaseName() { return "Matte"; }

    public int getNumInputSockets()  { return 2; }
    public int getNumOutputSockets() { return 1; }    
    /**
     * Override processImage to find the brightness to change the opacity of the base image
     * and send the edited image to the output.
     */
     public void processImage() {
        // Get input images information
        Image matteImage   = matte.getImage();
        Image baseImage    = base.getImage();
        // Output is null if any inputs are null
        if(baseImage == null || matteImage == null) {
            out.setImage(null);
            return;
        }

        int width   = (int)baseImage.getWidth();
        int height  = (int)baseImage.getHeight();
        int matteWidth  = (int)matteImage.getWidth();
        int matteHeight = (int)matteImage.getHeight();

        PixelReader matteReader = matteImage.getPixelReader();
        PixelReader baseReader  = baseImage.getPixelReader();
        
        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(width, height);
        PixelWriter     writer = outImg.getPixelWriter();
        
        // Iterate through pixels
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                // If we extend outside of the matte, mask this part of the image
                if (x >= matteWidth || y >= matteHeight)
                    writer.setColor(x, y, Color.TRANSPARENT);
                // Otherwise, use the brightness of the matte to determine opacity
                else {
                    Color matteColor    = matteReader.getColor(x, y);
                    Color baseColor     = baseReader.getColor(x, y);

                    //get color values from base and matte
                    double opacity = 1.0 - matteColor.getBrightness();
                    double rVal    = baseColor.getRed();
                    double gVal    = baseColor.getGreen();
                    double bVal    = baseColor.getBlue();

                    writer.setColor(x, y, new Color(rVal, gVal, bVal, opacity));
                }
            }
        }
        
        // Send to output socket
        out.setImage(outImg);
    }
}   