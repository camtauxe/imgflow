package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that can modify the opacity of an image
 */
public class GraphNodeBlur extends GraphNode {

    //private NodePropertySlider opacitySlider;

    private NodeSocketInput  in;
    private NodeSocketOutput out;

    public GraphNodeBlur() {
        //opacitySlider = new NodePropertySlider(this, "Opacity (%)", 0, 100, 100);

        //properties.add(opacitySlider);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Blur"; }

    /**
     * Override processImage to modify the opacity of the input image
     * and send it to the output.
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

        //Iterate through pixels and adjust color
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color inColor = reader.getColor(x, y);
                // 
                writer.setColor(x, y, Color.BLACK);
            }
        }

        // Send to output socket
        out.setImage(outImg);
    }
}
