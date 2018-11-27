package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that reflects an image horizontally,
 * vertically or both horizontally and vertically
 */
public class GraphNodeFlip extends GraphNode {
     
    //Establish dropdown
    private NodePropertyDropDown flipMenu;
    private String flipOptions[] = {"Horizontal", "Vertical", "Both"};

    private NodeSocketInput in;
    private NodeSocketOutput out;

    public GraphNodeFlip() {
        flipMenu = new NodePropertyDropDown(this, "Flip Type", flipOptions);
        properties.add(flipMenu);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Flip";}

    public String getDescription() {
        return "Mirror an image horizontally, vertically or both";
    }

    /**
     * Override processImage to reflect the image as specified
     * by the user's dropdown selection
     */
    public void processImage() {
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

        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(width, height);
        PixelWriter     writer = outImg.getPixelWriter();

        //get user dropdown selection
        String choice = flipMenu.getValue();
        switch(choice) {
            case "Horizontal":
                //iterate through pixels
                for (int x = 0; x < width; x++)
                    for (int y = 0; y < height; y++) {
                        //flip horizontal
                        Color inColor = reader.getColor(x, y);
                        writer.setColor(width - x-1, y, inColor);
                    }
                break;
            case "Vertical":
                for (int x = 0; x < width; x++)
                    for (int y = 0; y < height; y++) {
                        //flip vertical
                        Color inColor = reader.getColor(x, y);
                        writer.setColor(x, height - y-1, inColor);
                    }
                break;
            case "Both":
                for (int x = 0; x < width; x++)
                    for (int y = 0; y < height; y++) {
                        //flip vertical and horizontal
                        Color inColor = reader.getColor(x, y);
                        writer.setColor(width - x-1, height - y-1, inColor);
                    }
                break;
        }//end switch
        // Send to output socket
        out.setImage(outImg);
    }
}
    