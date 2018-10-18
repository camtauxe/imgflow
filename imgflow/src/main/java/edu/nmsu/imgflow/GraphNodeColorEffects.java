package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

/**
 * A type of graph node that can modify the hue, 
 * saturation, and brightness of an image
 */
public class GraphNodeColorEffects extends GraphNode {

    private NodePropertySlider hueSlider, saturationSlider, brightnessSlider;

    private NodeSocketInput  in;
    private NodeSocketOutput out;

    public GraphNodeColorEffects() {
        hueSlider = new NodePropertySlider(this, "Hue", 0, 100, 0);
        saturationSlider = new NodePropertySlider(this, "Saturation", -100, 100, 0);
        brightnessSlider = new NodePropertySlider(this, "Brightness", -100, 100, 0);

        properties.add(hueSlider);
        properties.add(saturationSlider);
        properties.add(brightnessSlider);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Color Effects"; }

    /**
     * Override processImage to modify the colors of the input image
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

        //Iterate through and change the node color
        // 
        //deriveColor affects hue is on a scale from 0 - 350 
        //and saturation and brightness on a scale from 0 - 2.
        //This node doesn't affect opacity, therefore defaults to 100
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color inColor = reader.getColor(x, y);
                writer.setColor(x, y, inColor.deriveColor( hueSlider.getValue() * 3.5
                                                           ,(saturationSlider.getValue() / 100) + 1
                                                           ,(brightnessSlider.getValue() / 100) + 1
                                                           ,100));

            }
        }

        // Send to output socket
        out.setImage(outImg);
    }
}
