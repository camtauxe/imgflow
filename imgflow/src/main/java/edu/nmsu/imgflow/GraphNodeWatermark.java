package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that overlays a watermark on an image
 */
public class GraphNodeWatermark extends GraphNode {
    
    private NodeSocketInput in;
    private NodeSocketInput watermark;
    private NodeSocketOutput out;
    
    private NodePropertySpinner xOffsetSpinner, yOffsetSpinner;
    
    public GraphNodeWatermark() {
        in        = inputSockets.get(0);
        watermark = inputSockets.get(1);
        out       = outputSockets.get(0);
        
        xOffsetSpinner = new NodePropertySpinner(this, "Horizontal Offset (%)", 0, 100, 100);
        yOffsetSpinner = new NodePropertySpinner(this, "Vertical Offset (%)", 0, 100, 100);
        
        properties.add(xOffsetSpinner);
        properties.add(yOffsetSpinner);
    }
    
    public String getBaseName() { return "Watermark"; }
    
    /**
     * Override processImage to add the watermark to the input image
     * and send it to the output.
     */
    public void processImage() {
        // Get input images information
        Image inImg   = in.getImage();
        Image overlay = watermark.getImage();
        // If there is no input image, clear the output image and finish
        if (inImg == null) {
            out.setImage(null);
            return;
        }
        
        // Input image info
        int inWidth  = (int)inImg.getWidth();
        int inHeight = (int)inImg.getHeight();
        PixelReader inReader = inImg.getPixelReader();
        
        // Overlay image info
        int oWidth  = (int)overlay.getWidth();
        int oHeight = (int)overlay.getHeight();
        PixelReader oReader = overlay.getPixelReader();

        // Create a new writable image for the output
        WritableImage outImg = new WritableImage(inWidth, inHeight);
        PixelWriter writer   = outImg.getPixelWriter();
        
        // Record user offset values, convert to pixel location
        // width * percent = pixel position
        int xOffset = (int)(inWidth  * xOffsetSpinner.getValue());
        int yOffset = (int)(inHeight * yOffsetSpinner.getValue());
        
        // Itterate through pixels
        for(int x = 0; x < inWidth; x++) {
            for(int y = 0; y < inHeight; y++) {
                Color inColor = inReader.getColor(x, y);
                Color oColor  = oReader.getColor(x, y);
                
                //if pixel is within the bounds of the watermark
                if(x >= xOffset && x < oWidth) {
                    Color newColor = oColor.interpolate(inColor, oColor.getOpacity());
                    writer.setColor(x, y, newColor);
                }
                //if the pixel is outside the bounds of the watermark
                else {
                    writer.setColor(x, y, inColor);
                }
            
            }
        }
        // Send to output socket
        out.setImage(outImg);
    }
    public int getNumInputSockets()  { return 2; }
    public int getNumOutputSockets() { return 1; }
}