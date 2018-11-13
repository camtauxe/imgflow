package edu.nmsu.imgflow;

import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.control.Spinner;

/**
 * A type of graph node that crops an image
 */
public class GraphNodeCrop extends GraphNode {

    private NodePropertySpinner spinnerOriginX, spinnerOriginY, spinnerWidth, spinnerHeight;

    private NodeSocketInput  in;
    private NodeSocketOutput out;

    public GraphNodeCrop() {

        spinnerOriginX = new NodePropertySpinner(this, "Pixel offset of X origin", 0, 10000, 0, Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinnerOriginY = new NodePropertySpinner(this, "Pixel offset of Y origin", 0, 10000, 0);
        spinnerWidth = new NodePropertySpinner(this, "Pixel width", 1, 10000, 100, Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinnerHeight = new NodePropertySpinner(this, "Pixel height", 1, 10000, 100);

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

        //create outImg to be sent to output
        WritableImage outImg;

        //get the spinner values to be used in creation of the ouput
        int originX = spinnerOriginX.getValue();
        int originY = spinnerOriginY.getValue();
        int newWidth = spinnerWidth.getValue();
        int newHeight = spinnerHeight.getValue();


        //image entirely outside of original bounds
        if(originX >= inImg.getWidth() || originY >= inImg.getHeight())
            outImg = null;
        else{
            //error checking, default image dimensions to as large as can be 
            //if user attempting to crop past the edge of picture
            if( originX + newWidth > inImg.getWidth() )
                newWidth = (int) inImg.getWidth() - originX;
            if( originY + newHeight > inImg.getHeight() )
                newHeight = (int) inImg.getHeight() - originY;

            outImg = new WritableImage(inImg.getPixelReader(), originX, originY, newWidth, newHeight);
        }

        // Send to output socket
        out.setImage(outImg);
    }


    //moved to static value spinners as dynamic updating was causing performance issues 

    // //overide the function to dynamically update the max value of the 
    // //spinner based on the dimensions of the input image
    // public void onInputUpdate(NodeSocketInput socket) {
    //     in.requestUpdate();
    //     if(in.getImage() != null){
    //         spinnerOriginX.updateSpinnerMax( (int) in.getImage().getWidth() );
    //         spinnerOriginY.updateSpinnerMax( (int) in.getImage().getHeight() );
    //         spinnerWidth.updateSpinnerMax( (int) in.getImage().getWidth() );
    //         spinnerHeight.updateSpinnerMax( (int) in.getImage().getHeight() );
    //     }
    //     super.onInputUpdate(socket);
    // }
}
