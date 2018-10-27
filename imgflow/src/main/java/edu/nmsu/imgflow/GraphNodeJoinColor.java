package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * Joins three RGB color channels into one image. Currently does literally nothing.
 */
public class GraphNodeJoinColor extends GraphNode {

    private NodeSocketInput inR; //input red color channel
    private NodeSocketInput inG; //input green color channel
    private NodeSocketInput inB; //input blue color channel
    private NodeSocketOutput out;
    
    public GraphNodeJoinColor() {
        inR = inputSockets.get(0);
        inG = inputSockets.get(1);
        inB = inputSockets.get(2);
        out = outputSockets.get(0);
    }

    public String getName() { return "Color Join"; }
    
    /**
     * Override processImage to join the colors of the input images
     * and send a composite image to the output.
     */
    public void processImage() {
    // Get input images information
       Image rImgIn = inR.getImage();
       Image gImgIn = inG.getImage();
       Image bImgIn = inB.getImage();
       // error case: one or more of the input images is null
       if(rImgIn == null || gImgIn == null || bImgIn == null) {
          out.setImage(null);
          return;
       }
       int width = (int)rImgIn.getWidth();   //Both assume all images
       int height = (int)rImgIn.getHeight(); //are same size
       PixelReader rReader = rImgIn.getPixelReader(); //read red channel
       PixelReader gReader = gImgIn.getPixelReader(); //read green
       PixelReader bReader = bImgIn.getPixelReader(); //read blue
      
       //create writable image & pixelwriter for composite output
       WritableImage imgOut = new WritableImage(width, height);
       PixelWriter writer = imgOut.getPixelWriter();
      
       // Iterate through pixels and get brightness values
       for(int x = 0; x < width; x++) {
          for(int y = 0; y < height; y++) {
             Color rChannel = rReader.getColor(x, y); //read in red color
             Color gChannel = gReader.getColor(x, y); //read in green
             Color bChannel = bReader.getColor(x, y); //read in blue
          
             double rVal = rChannel.getBrightness(); //get redChannel brightness value
             double gVal = gChannel.getBrightness(); //get greenChannel brightness
             double bVal = bChannel.getBrightness(); //get blueChannel brightness
            
             double rOp = rChannel.getOpacity(); //get red opacity
             double gOp = gChannel.getOpacity(); //get green opacity
             double bOp = bChannel.getOpacity(); //get blue opacity
             double opacity = (rOp + gOp + bOp) / 3.0; //average opacity
            
             //create composite color from RGB values
             Color composite = new Color(rVal, gVal, bVal, opacity);
            
             //output to writer
             writer.setColor(x, y, composite);
          }
       }
       //send to output socket
       out.setImage(imgOut);
    }
    public int getNumInputSockets() { return 3; }
    public int getNumOutputSockets() { return 1; }
}