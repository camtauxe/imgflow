package edu.nmsu.imgflow;

import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;

/**
 * A type of graph node that crops an image
 */
public class GraphNodeCrop extends GraphNode {

    private NodePropertySpinner spinnerOriginX, spinnerOriginY, spinnerWidth, spinnerHeight;

    private NodeSocketInput  in;
    private NodeSocketOutput out;

    public GraphNodeCrop() {

        spinnerOriginX = new NodePropertySpinner(this, "Pixel offset of X origin", 0, 100, 0);
        spinnerOriginY = new NodePropertySpinner(this, "Pixel offset of Y origin", 0, 100, 0);
        spinnerWidth = new NodePropertySpinner(this, "Pixel width", 0, 100, 100);
        spinnerHeight = new NodePropertySpinner(this, "Pixel height", 0, 100, 100);

        properties.add(spinnerOriginX);
        properties.add(spinnerOriginY);
        properties.add(spinnerWidth);
        properties.add(spinnerHeight);

        in  = inputSockets.get(0);
        out = outputSockets.get(0);
    }

    public String getBaseName() { return "Crop"; }

    /**
     * Override processImage to crop the input image
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

        //get the spinner values to be used in creation of the ouput
        int originX = (int)((spinnerOriginX.getValue() / 100.0) * inImg.getWidth());
        int originY = (int)((spinnerOriginY.getValue() / 100.0) * inImg.getWidth());
        int newWidth = (int)((spinnerWidth.getValue() / 100.0) * inImg.getWidth());
        int newHeight = (int)((spinnerHeight.getValue() / 100.0) * inImg.getHeight());

        //error checking, default image width to as large as can be 
        //if user attempting to crop past the edge of picture
        if( originX + newWidth > inImg.getWidth() )
            newWidth = (int) inImg.getWidth() - originX;
        if( originY + newHeight > inImg.getHeight() )
            newHeight = (int) inImg.getHeight() - originY;


        WritableImage outImg = new WritableImage(inImg.getPixelReader(), originX,originY,newWidth, newHeight);

        // Send to output socket
        out.setImage(outImg);
    }

    public void onInputUpdate(NodeSocketInput socket) {
        super.onInputUpdate(socket);
        in.requestUpdate();
        if(in.getImage() != null){
            spinnerOriginX.updateSpinnerMax( (int) in.getImage().getWidth() );
            spinnerOriginY.updateSpinnerMax( (int) in.getImage().getHeight() );
            spinnerWidth.updateSpinnerMax( (int) in.getImage().getWidth() );
            spinnerHeight.updateSpinnerMax( (int) in.getImage().getHeight() );
        }
    }
}
