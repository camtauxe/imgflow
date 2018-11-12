package edu.nmsu.imgflow;

import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.control.Spinner;


/**
 * A type of graph node that inverts the colors of an image
 */

//Note: may want to add a checkbox for preserving the aspect ratio or not
public class GraphNodeResize extends GraphNode {

    private NodePropertySpinner newWidthSpinner, newHeightSpinner;

    private NodeSocketInput  in;
    private NodeSocketOutput out;

    public GraphNodeResize() {

        newWidthSpinner = new NodePropertySpinner(this, "Width", 1, 4500, 100, Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        newHeightSpinner = new NodePropertySpinner(this, "Height", 1, 4500, 100);

        properties.add(newWidthSpinner);
        properties.add(newHeightSpinner);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Resize"; }

    /**
     * Override processImage to resize the input image
     * and send it to the output.
     */
    public void processImage() {
        //not sure why, but if requestUpdate is not called, getValue on the
        //spinners isn't fuctioning correctly
        in.requestUpdate();

        // Get input image information
        Image inImg = in.getImage();
        // If there is no input image, clear the output image and finish
        if (inImg == null) {
            out.setImage(null);
            return;
        }

        PixelReader reader = inImg.getPixelReader();

        int newWidth   = newWidthSpinner.getValue();
        int newHeight  = newHeightSpinner.getValue();

        // Create a new writable image for the output
        WritableImage   outImg = new WritableImage(newWidth, newHeight);
        PixelWriter     writer = outImg.getPixelWriter();

        //create a scale that will be used to map the pixels to the new image
        double widthScale = inImg.getWidth() / newWidth;
        double heightScale = inImg.getHeight() / newHeight;

        // Selectively move pixels from input to output based on a scale
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                Color inColor = reader.getColor( (int)(x * widthScale) ,(int)( y * heightScale) );
                writer.setColor(x, y, inColor);
            }
        }

        // Send to output socket
        out.setImage(outImg);
    }
    

    //moved to static value spinners as dynamic updating was causing performance issues 

    // //overide the function to dynamically update the max value of the 
    // //spinner based on the dimensions of the input image
    // public void onInputUpdate(NodeSocketInput socket) {
    //     super.onInputUpdate(socket);
    //     in.requestUpdate();
    //     if(in.getImage() != null){
    //         newWidthSpinner.updateSpinnerMax( (int) in.getImage().getWidth() );
    //         newHeightSpinner.updateSpinnerMax( (int) in.getImage().getHeight() );
    //     }
    // }

}
