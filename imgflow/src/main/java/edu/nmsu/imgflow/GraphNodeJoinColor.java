package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * Joins three RGB color channels into one image. Takes in three images and outputs
 * a single image using the RGB values from each input
 */
public class GraphNodeJoinColor extends GraphNode {

    // References to sockets
    private NodeSocketInput inR;
    private NodeSocketInput inG;
    private NodeSocketInput inB;
    private NodeSocketOutput out;
    
    public GraphNodeJoinColor() {
        inR = inputSockets.get(0);
        inG = inputSockets.get(1);
        inB = inputSockets.get(2);
        out = outputSockets.get(0);
    }

    public String getName() { return "Join RGB"; }
    
    /**
     * Override processImage to join the colors of the input images
     * and send a composite image to the output.
     */
    public void processImage() {
        // Get input images information
        Image rImgIn = inR.getImage();
        Image gImgIn = inG.getImage();
        Image bImgIn = inB.getImage();
        // if any input image is null, output will be null as well
        if(rImgIn == null || gImgIn == null || bImgIn == null) {
            out.setImage(null);
            return;
        }
        int width = (int)rImgIn.getWidth();
        int height = (int)rImgIn.getHeight();
        PixelReader rReader = rImgIn.getPixelReader();
        PixelReader gReader = gImgIn.getPixelReader();
        PixelReader bReader = bImgIn.getPixelReader();

        // TODO: Fail gently if input images are different sizes
        
        //create writable image & pixelwriter for composite output
        WritableImage imgOut = new WritableImage(width, height);
        PixelWriter writer = imgOut.getPixelWriter();
        
        // Iterate through pixels and get brightness values
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Color rChannel = rReader.getColor(x, y);
                Color gChannel = gReader.getColor(x, y);
                Color bChannel = bReader.getColor(x, y);
            
                double rVal = rChannel.getBrightness();
                double gVal = gChannel.getBrightness();
                double bVal = bChannel.getBrightness();

                // output opacity is averaged from the three inputs
                double rOp = rChannel.getOpacity();
                double gOp = gChannel.getOpacity();
                double bOp = bChannel.getOpacity();
                double opacity = (rOp + gOp + bOp) / 3.0;
                
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