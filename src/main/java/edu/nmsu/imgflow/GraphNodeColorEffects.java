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
        hueSlider           = new NodePropertySlider(this, "Hue",               0, 255, 0);
        saturationSlider    = new NodePropertySlider(this, "Saturation (%)",    0, 250, 100);
        brightnessSlider    = new NodePropertySlider(this, "Brightness (%)",    0, 250, 100);

        properties.add(hueSlider);
        properties.add(saturationSlider);
        properties.add(brightnessSlider);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Color Effects"; }

    public String getDescription() { return "Modify the hue, saturation, and brightness of the image."; }

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

        //Iterate through pixels and adjust color
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color inColor = reader.getColor(x, y);
                Color outColor = inColor.deriveColor(
                    hueSlider.getValue(),                   // hue shift
                    saturationSlider.getValue() / 100.0,    // saturation factor
                    brightnessSlider.getValue() / 100.0,    // brightness factor
                    1.0 // preserve opacity
                );
                writer.setColor(x, y, outColor);

            }
        }

        // Send to output socket
        out.setImage(outImg);
    }
}
