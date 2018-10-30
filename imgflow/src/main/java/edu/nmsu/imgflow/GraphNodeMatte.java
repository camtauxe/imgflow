package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that takes an opacity template as the first image
 * and uses the brightness values to determine the opacity of the second 
 */
public class GraphNodeMatte extends GraphNode {
 
    // References to sockets
    private NodeSocketInput template;
    private NodeSocketInput base;
    private NodeSocketOutput out;
    
    public GraphNodeMatte() {
       template = inputSockets.get(0);
       base     = inputSockets.get(1);
       out     = outputSockets.get(0);
    }
    
    public String getBaseName() { return "Matte"; }
    
    /**
     * Override processImage to find the brightness to change the opacity of the base image
     * and send the edited image to the output.
     */
     public void processImage() {
         // Get input images information
         Image temp     = template.getImage();
         Image original = base.getImage();
         //error cases
         if(original == null) {
             out.setImage(null);
             return;
         }
         /*
         //no template -> no effect
         if(temp == null) {
             out.setImage(original);
             return;
         }
         */
         int width   = (int)original.getWidth();
         int height  = (int)original.getHeight();
         PixelReader tempReader       = temp.getPixelReader();
         PixelReader originReader = original.getPixelReader();
         
         // Create a new writable image for the output
         WritableImage   outImg = new WritableImage(width, height);
         PixelWriter     writer = outImg.getPixelWriter();
         
         // Iterate through pixels and assign brightness to opacity
         for(int x = 0; x < width; x++) {
             for(int y = 0; y < height; y++) {
                 Color tColor   = tempReader.getColor(x, y);
                 Color oColor = originReader.getColor(x, y);
                 
                 //get color values from original and template
                 double opacity = tColor.getBrightness();
                 double rVal    = oColor.getRed();
                 double gVal    = oColor.getGreen();
                 double bVal    = oColor.getBlue();
                 
                 writer.setColor(x, y, new Color(rVal, gVal, bVal, opacity));
             }
         }
         
         // Send to output socket
         out.setImage(outImg);
    }
}   