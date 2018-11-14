package edu.nmsu.imgflow;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;



import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.control.TextField;


/**
 * Represents a single node on the image pipeline graph.
 *
 * Each node has a number of input and output sockets and performs some function
 * mapping image data on the inputs to image data sent to the outputs. Each node also
 * maintains its position on the node graph view and has a name and a base name
 * which is different.
 *
 * The base name identifies the type of the node
 * (and is set for an entire subclass of GraphNode) while the name identifies an individual
 * node and is set by the user. For example: A type of node that performs a Gaussian Blur on its input
 * would have a base name of "Gaussian Blur" while individual nodes on the graph may have names like
 * "apply pre-blur" or "strong blur"
 */
public abstract class GraphNode {

    // ################################
    // # STATIC VARIABLES AND CONSTANTS
    // ################################

    // Sizing for drawings nodes.
    // Note that these are in GRAPH UNITS!!!
    public static final double  NODE_WIDTH  = 2.0;
    // A node is drawn split up into rows: the top being the header and additional rows
    // below it being drawn beneath it for each input or output socket
    public static final double  NODE_ROW_HEIGHT  = 0.4;
    public static final double  NODE_ROW_PADDING = 0.1;
    public static final double  NODE_SOCKET_SIZE = NODE_ROW_HEIGHT - (2 * NODE_ROW_PADDING);
    // This is the position relative to the top-left corner of the node that corresponds
    // to the beginning of the baseline for the node title text
    public static final Point2D NODE_TITLE_POS = new Point2D(NODE_ROW_PADDING, NODE_ROW_HEIGHT - NODE_ROW_PADDING);
    public static final double  NODE_TITLE_SIZE = 0.2;


    // ################################
    // # INSTANCE VARIABLES
    // ################################

    /**
     * The position of the node in the Graph view (IN GRAPH UNITS)
     */
    protected Point2D position;
    /**
     * The name of this individual node
     */
    protected String  name;

    /**
     * A list of this node's properties.
     */
    protected ArrayList<NodeProperty<?>> properties;

    /**
     * A text node property for this node's name
     */
    protected NodePropertyText nameProperty;

    /**
     * The list of all of this node's input sockets
     */
    protected ArrayList<NodeSocketInput> inputSockets;

    /**
     * The list of all of this node's output sockets
     */
    protected ArrayList<NodeSocketOutput> outputSockets;

    /**
     * The list of all of this node's sockets (inputs and outputs concatenated together)
     */
    protected ArrayList<NodeSocket> allSockets;

    // ################################
    // # CONSTRUCTOR
    // ################################

    /**
     * Create a new GraphNode at position (0,0)
     * and with a default name equivalent to its base name.
     *
     * GraphNode is abstract so this can not be called directly, and must
     * be called as 'super()' in the constructor of a subclass.
     */
    public GraphNode() {
        // Instantiate instance variables
        position        = new Point2D(0.0, 0.0);
        name            = getBaseName();
        properties      = new ArrayList<NodeProperty<?>>();
        inputSockets    = new ArrayList<NodeSocketInput>();
        outputSockets   = new ArrayList<NodeSocketOutput>();
        allSockets      = new ArrayList<NodeSocket>();

        // Instantiate and add name property
        nameProperty    = new NodePropertyText(this, "Name", name);
        properties.add(nameProperty);

        // Instantiate all input and output sockets
        int numInputs = getNumInputSockets();
        for (int i = 0; i < numInputs; i++) {
            NodeSocketInput socket = new NodeSocketInput(this, i);
            inputSockets.add(socket);
            allSockets.add(socket);
        }

        int numOutputs = getNumOutputSockets();
        for (int i = 0; i < numOutputs; i++) {
            NodeSocketOutput socket = new NodeSocketOutput(this, i);
            outputSockets.add(socket);
            allSockets.add(socket);
        }
    }

    // ################################
    // # METHODS
    // ################################

    // The methods here are roughly sorted by how likely you
    // are to want to override them in a subclass (with more likely at the top)

    /**
     * Get the node's base name, identifying the kind of the node this is.
     */
    public String getBaseName() { return "Untitled"; }

    /**
     * Get the number of input sockets this kind of node has
     */
    public int getNumInputSockets() { return 1; }

    /**
     * Get the number of output sockets this kind of node has
     */
    public int getNumOutputSockets() { return 1; }

    /**
     * Get the socket whose image this node uses to draw its thumbnail preview.
     * By default, this will be the first output socket (or input socket if there
     * are no outputs), but other nodes may override it. This can also be null
     * if the node does not have a thumbnail preview.
     */
    public NodeSocket getThumbnailSocket() {
        if (getNumOutputSockets() > 0)
            return outputSockets.get(0);
        else if (getNumInputSockets() > 0)
            return inputSockets.get(0);
        return null;
    }

    /**
     * Process the image from the input(s) and write new image data
     * to the output(s). This function should not be called directly and is
     * instead called by the graph node's update() function which will gaurentee
     * that before calling processImage(), that all inputs are up-to-date and that,
     * after the function returns, all output sockets will be flagged as up-to-date.
     *
     * By default, this does nothing so virtually every kind of node will want to override it.
     */
    protected void processImage() {}

    /**
     * Respond to a node property updating its value. By default, this will propagate
     * an update through this node's output sockets, unless the changed property
     * is the name property in which case the node changes its name. Also, if the node
     * is currently selected, it will update to refresh the preview image.
     * Some graph nodes may want to override this to do something else 
     * (for example, only update some outputs depending on the property that was updated).
     */
    public void onPropertyUpdate(NodeProperty<?> updatedProperty) {
        if (updatedProperty == nameProperty) {
            name = nameProperty.getValue();
        }
        else {
            for (NodeSocketOutput output : outputSockets) {
                output.propagateUpdate();
            }
            if (Main.getInstance().getActiveGraph().getSelectedNode() == this) {
                update();
                Main.getInstance().getPropertyPanel().refreshPreview();
            }
        }
    }

    /**
     * Respond to an input socket updating its data (either by disconnecting, connecting
     * or having its connected output socket update). By default, this will propagate
     * an update through this node's output sockets. But some graph nodes may to want
     * to override this to do something else (for example only update some outpus
     * depending on the socket that was updated)
     */
    public void onInputUpdate(NodeSocketInput socket) {
        for (NodeSocketOutput output : outputSockets) {
            output.propagateUpdate();
        }
    }

    /**
     * Re-render this node to ensure that its outputs' image data
     * is up-to-date. This will also update all up-stream nodes
     * if necessary.
     */
    public void update() {
        // Update all input sockets
        for (NodeSocketInput input : inputSockets) {
            input.requestUpdate();
        }

        processImage();

        // Clear needsUpdate flag on output nodes
        for (NodeSocketOutput output : outputSockets) {
            output.setNeedsUpdate(false);
        }
    }

    /**
     * Disconnect all of this node's sockets from their attached
     * sockets.
     */
    public void disconnectAllSockets() {
        for (NodeSocket socket : allSockets)
            socket.disconnect();
    }

    /**
     * Draw this node in the given viewport.
     * This assumes that the graphics context in the viewport
     * has already been transformed to graph space!!
     */
    public void draw(Viewport viewport) {
        GraphicsContext ctx = viewport.getGraphicsContext();
        // transform the canvas context to draw relative to the node's position
        ctx.save();
        ctx.translate(position.getX(), position.getY());

        // keep track of how far down the node we're drawing
        double cursorY      = 0.0;

        // Draw header
        ctx.setFill(Color.web("#439be8"));
        ctx.fillRect(0.0, cursorY, NODE_WIDTH, NODE_ROW_HEIGHT);
        cursorY += NODE_ROW_HEIGHT;

        // When drawing the title, we scale back to viewport size because text rendering
        // may break otherwise.
        ctx.save();
        ctx.translate(-position.getX(), -position.getY());
        viewport.transformContextToCanvasSpace(ctx);

        Font font =   new Font(viewport.graphUnitsToPixels(NODE_TITLE_SIZE));
        Point2D pos = viewport.graphCoordToCanvasCoord(position.add(NODE_TITLE_POS));
        ctx.setFill(Color.BLACK);
        ctx.setFont(font);
        ctx.fillText(getName(), pos.getX(), pos.getY());

        // return to drawing relative to node posittion (in graph units)
        ctx.restore();

        // Draw rows
        int rowsToDraw   = Math.max(getNumInputSockets(), getNumOutputSockets());
        for (int i = 0; i < rowsToDraw; i++) {
            // alternate between different shades for background color
            ctx.setFill(i % 2 == 0 ? Color.web("black",0.6) : Color.web("black",0.5));
            ctx.fillRect(0.0, cursorY, NODE_WIDTH, NODE_ROW_HEIGHT);
            cursorY += NODE_ROW_HEIGHT;
        }

        // Draw sockets
        ctx.setFill(Color.web("#5dc9ea"));
        ctx.setStroke(Color.web("#85d8f2"));
        ctx.setLineWidth(viewport.pixelsToGraphUnits(2.5));
        for (NodeSocket socket : allSockets) {
            ctx.fillRect(socket.getPosition().getX(), socket.getPosition().getY(), NODE_SOCKET_SIZE, NODE_SOCKET_SIZE);
            // If the socket is an output socket with a connection, draw the connection line
            if (socket instanceof NodeSocketOutput) {
                NodeSocketOutput out = (NodeSocketOutput)socket;
                NodeSocketInput  in  = out.getConnectingSocket();
                if (in != null) {
                    // Get position of other socket relative to this socket's parent node
                    Point2D inPos = in.getParentNode().getPosition().subtract(position).add(in.getConnectingPosition());
                    Point2D outPos = out.getConnectingPosition();
                    ctx.strokeLine(outPos.getX(), outPos.getY(), inPos.getX(), inPos.getY());
                }
            }
        }

        // Draw outline if node is being hovered over or selected
        boolean isSelected = Main.getInstance().getActiveGraph().getSelectedNode() == this;
        if (viewport.getHoverQuery().getHoveringNode() == this || isSelected) {
            ctx.setLineWidth(viewport.pixelsToGraphUnits(3.0));
            // outline color depends on if this node is selected or not
            ctx.setStroke(isSelected ? Color.YELLOW : Color.WHITE);
            ctx.strokeRect(0.0, 0.0, NODE_WIDTH, cursorY);
        }

        // return drawing relative to graph origin (in graph units)
        ctx.restore();
    }

    /**
    * Change the name of the Node.
    * Just add the button to any subclass, and allow the
    * name to be changed.
    */
    public void changeNodeName() {

      Stage popup = new Stage();
      popup.setTitle("Change Node Name");

      Label label = new Label("Enter New Name.");
      Button button = new Button("Set");
      TextField tf = new TextField();

      tf.setPromptText("Enter name:");

      // When the button is pressed
      // Change the name of this node
      button.setOnAction((actionEvent) -> {

          // Should prompt the user here
          // if the text field is empty
          // for now, just don't change the name
          if(tf.getText().isEmpty()) {
            System.out.println("There was no text, not changing name");
          }
          else {
            this.setName(tf.getText());
          }

          popup.close();
      });

      VBox layout = new VBox(10);
      layout.getChildren().addAll(label,tf,button);
      Scene scene = new Scene(layout,300,300);
      popup.setScene(scene);
      popup.showAndWait();


    }

    // ################################
    // # GETTERS / SETTERS
    // ################################

    /**
     * Get the position of this node in the graph view. (in graph units)
     */
    public Point2D  getPosition() { return position; }
    /**
     * Set the position of this node in the graph view.
     */
    public void     setPosition(Point2D pos) { position = pos; }

    /**
     * Get the name of this individual node.
     */
    public String   getName() { return name; }
    /**
     * Set the namme of this individual node.
     */
    public void     setName(String name) { this.name = name; }

    /**
     * Get the list of this node's properties
     */
    public ArrayList<NodeProperty<?>> getProperties() { return properties; }

    /**
     * Get the list of this node's input sockets
     */
    public ArrayList<NodeSocketInput> getInputSockkets() { return inputSockets; }

    /**
     * Get the list of this node's output sockets
     */
    public ArrayList<NodeSocketOutput> getOutputSockets() { return outputSockets; }

    /**
     * Get the list of all of this node's sockets (inputs and outputs concatenated together)
     */
    public ArrayList<NodeSocket> getAllSockets() { return allSockets; }
}
