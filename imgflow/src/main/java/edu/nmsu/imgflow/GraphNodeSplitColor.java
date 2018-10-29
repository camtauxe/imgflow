package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * Splits image into RGB color channels. Takes a single input image and outputs
 * three grayscale images each representing the values of one of the input's RGB channels.
 */
public class GraphNodeSplitColor extends GraphNode {

    // References to sockets
    private NodeSocketInput in;
    private NodeSocketOutput outR;
    private NodeSocketOutput outG;
    private NodeSocketOutput outB;
    
    public GraphNodeSplitColor() {
        in  = inputSockets.get(0);
        outR = outputSockets.get(0);
        outG = outputSockets.get(1);
        outB = outputSockets.get(2);
    }
    
    public String getName() { return "Split RGB"; }
    
    /**
     * Override processImage to split the colors of the input image
     * and send it to the corresponding outputs.
     */
    public void processImage() {
      // Get input image information
      Image inImg = in.getImage();
      // if there is no input image, clear output and finish
      if(inImg == null) {
         outR.setImage(null);
         outG.setImage(null);
         outB.setImage(null);
         return;
      }
      int width =  (int)inImg.getWidth();
      int height = (int)inImg.getHeight();
      PixelReader reader = inImg.getPixelReader();

      // Create new writable images for each output
      WritableImage rImg = new WritableImage(width, height);
      WritableImage gImg = new WritableImage(width, height);
      WritableImage bImg = new WritableImage(width, height);
      
      PixelWriter rWriter = rImg.getPixelWriter();
      PixelWriter gWriter = gImg.getPixelWriter();
      PixelWriter bWriter = bImg.getPixelWriter();

      // Iterate through pixels and get RGB color values
      for(int x = 0; x < width; x++) {
         for(int y = 0; y < height; y++) {
            Color inColor = reader.getColor(x, y); //read in color
            
            double redVal   = inColor.getRed();
            double greenVal = inColor.getGreen();
            double blueVal  = inColor.getBlue();
            double opacity  = inColor.getOpacity();
            
            //create grayscale equivalent of each color value
            Color redOut   = new Color(redVal,      redVal,     redVal,     opacity);  
            Color greenOut = new Color(greenVal,    greenVal,   greenVal,   opacity);
            Color blueOut  = new Color(blueVal,     blueVal,    blueVal,    opacity);
            
            //output to each pixelwriter
            rWriter.setColor(x, y, redOut);
            gWriter.setColor(x, y, greenOut);
            bWriter.setColor(x, y, blueOut);
         }
      }
      
      //send to respective output sockets
      outR.setImage(rImg);
      outG.setImage(gImg);
      outB.setImage(bImg);    
   }
   
    public int getNumInputSockets() { return 1; }
    public int getNumOutputSockets() { return 3; }
}