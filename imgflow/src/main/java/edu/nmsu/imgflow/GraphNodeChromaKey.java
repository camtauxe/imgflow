package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that uses an RGB color value and a threshold to
 * set similar colors to clear in an input image
 */
public class GraphNodeChromaKey extends GraphNode {
    
    // Reference to the sockets
    private NodeSocketInput in;
    private NodeSocketOutput out;
    
    private NodePropertySlider thresholdSlider;
    
    public GraphNodeChromaKey() {
        in  = inputSockets.get(0);
        out = outputSockets.get(0);
        
        thresholdSlider = new NodePropertySlider(this, "Threshold (%)", 0, 100, 0);
        properties.add(thresholdSlider);
        //TODO: add color picker variable
    }
    
    public String getBaseName() { return "Chroma Key"; }
    
    /**
     * Overright processImage to set all pixels within the threshold of
     * the user's RGB to clear
     */
    public void processImage() {
        // Get input images information
        Image inImg = in.getImage();
        
        // Output is null if input is null
        if(inImg == null) {
            out.setImage(null);
            return;
        }
        
        // Temporary color value; default green
        Color target = Color.LIME;
        
        int width  = (int)inImg.getWidth();
        int height = (int)inImg.getHeight();
        
        PixelReader reader = inImg.getPixelReader();
        
        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(width, height);
        PixelWriter     writer = outImg.getPixelWriter();
        
        // Iterate through pixels
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Color inColor = reader.getColor(x,y);
                
                // Separate target color into RGB
                double targetR = target.getRed();
                double targetG = target.getGreen();
                double targetB = target.getBlue();
                
                // Separate image color into RGB
                double red   = inColor.getRed();
                double green = inColor.getGreen();
                double blue  = inColor.getBlue();
                
                // Get threshold value from slider
                double threshold = thresholdSlider.getValue();
                
                // When color is within the threshold clear it
                if(Math.abs(red - targetR) < threshold &&  Math.abs(green - targetG) < threshold && Math.abs(blue - targetB) < threshold) {
                    writer.setColor(x, y, new Color(red, green, blue, 0.0));
                }
                else //leave color unchanged
                    writer.setColor(x, y, inColor);                    
            }
        }
        
        // Send to output socket
        out.setImage(outImg);
    }   
    public int getNumInputSockets()  { return 1; }
    public int getNumOutputSockets() { return 1; }
}
    