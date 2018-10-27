package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * Splits image into RGB color channels. Currently splits the input image into three
 * based which contain the individual R, G, or B color values of each respectively.
 */
public class GraphNodeSplitColor extends GraphNode {

    private NodeSocketInput in;
    private NodeSocketOutput outR; //output for red color channel
    private NodeSocketOutput outG; //output for green color channel
    private NodeSocketOutput outB; //output for blue color channel
    
    public GraphNodeSplitColor() {
        in  = inputSockets.get(0);
        outR = outputSockets.get(0);
        outG = outputSockets.get(1);
        outB = outputSockets.get(2);
    }
    
    public String getName() { return "Color Split"; }
    
    /**
     * Override processImage to split the colors of the input image
     * and send it to the corrisponding outputs.
     */
    public void processImage() {
      // Get input image information
      Image inImg = in.getImage();
      // error case: no input image, clear output and finish
      if(inImg == null) {
         outR.setImage(null);
         outG.setImage(null);
         outB.setImage(null);
         return;
      }
      int width = (int)inImg.getWidth();
      int height = (int)inImg.getHeight();
      PixelReader reader = inImg.getPixelReader();

      // Create new writable images for the output
      WritableImage rImg = new WritableImage(width, height);
      WritableImage gImg = new WritableImage(width, height);
      WritableImage bImg = new WritableImage(width, height);
      
      PixelWriter rWriter = rImg.getPixelWriter(); //red pixelwriter
      PixelWriter gWriter = gImg.getPixelWriter(); //green pixelwriter
      PixelWriter bWriter = bImg.getPixelWriter(); //blue pixelwriter

      // Iterate through pixels and get RGB color values
      for(int x = 0; x < width; x++) {
         for(int y = 0; y < height; y++) {
            Color inColor = reader.getColor(x, y); //read in color
            
            double redVal   = inColor.getRed();     //get red value of color
            double greenVal = inColor.getGreen();   //get green value
            double blueVal  = inColor.getBlue();    //get blue value
            double opacity  = inColor.getOpacity(); //get opacity
            
            //create grayscale equivalent of each color concentration
            Color redOut   = new Color(redVal,     redVal,   redVal, opacity);  
            Color greenOut = new Color(greenVal, greenVal, greenVal, opacity);
            Color blueOut  = new Color(blueVal,   blueVal,  blueVal, opacity);
            
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