package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that rotates an image in
 * increments of 90 degrees
 */
public class GraphNodeRotate extends GraphNode {
     
    //Establish dropdown
    private NodePropertyDropDown rotationMenu;
    private String rotationOptions[] = {"Right 90", "Left 90", "Rotate 180"};
     
    private NodeSocketInput in;
    private NodeSocketOutput out;
     
    public GraphNodeRotate() {
        rotationMenu = new NodePropertyDropDown(this, "Rotation Type", rotationOptions);
        properties.add(rotationMenu);
        
        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }
    
    public String getBaseName() { return "Rotate";}
    
    /**
     * Override processImage to rotate the image as specified
     * by the user's dropdown selection
     */
    public void ProcessImage() {
        // Get input image information
        Image inImg = in.getImage();
        // If there is no input image, clear the output image and finish
        if (inImg == null) {
            out.setImage(null);
            return;
        }
        int width   = (int)inImg.getWidth();
        int height  = (int)inImg.getHeight();
        PixelReader reader = inImg.getPixelReader();

        // initialize writable image for the output
        WritableImage outImg;
        PixelWriter   writer;
        
        //get user dropdown selection
        String choice = rotationMenu.getValue();
        switch(choice) {
            case "Right 90":
            //set writable image boundaries with inverted height & width
            outImg = new WritableImage(height, width);
            writer = outImg.getPixelWriter();
            
            //iterate through pixels
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    Color inColor = reader.getColor(x, y);
                    //    .setColor(x Val , y Val,    , color)
                    writer.setColor(y , width - x, inColor);
                }
            break;
                    
            case "Left 90":
            //set writable image boundaries with inverted height & width
            outImg = new WritableImage(height, width);
            writer = outImg.getPixelWriter();
            
            //iterate through pixels
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    Color inColor = reader.getColor(x, y);
                    //    .setColor(x Val , y Val,    , color)
                    writer.setColor(height - y, x, inColor);
                }
            break;
                    
            case "Rotate 180":
            //set writable image boundaries with same height & width
            outImg = new WritableImage(width, height);
            writer = outImg.getPixelWriter();
            
            //iterate through pixels
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    Color inColor = reader.getColor(x, y);
                    //    .setColor(x Val , y Val,    , color)
                    writer.setColor(width - x, height - y, inColor);
                }
            break;
                    
            default:
            //necessary to prevent initialization error
            //set writable image boundaries with same height & width
            outImg = new WritableImage(width, height);
            writer = outImg.getPixelWriter();
            
            //iterate though pixels
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    //do nothing
                    Color inColor = reader.getColor(x, y);
                    writer.setColor(x, y, inColor);
                }
            break;
                
        }//end switch
        // Send to output socket
        out.setImage(outImg);
    }
} 