package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

/**
 * Represents a single node on the image pipeline graph.
 * 
 * Each node has a number of input and output sockets and performs some function
 * mapping image data on the inputs to image data sent to the outputs. Each node also
 * maintains its position on the node graph view and has a name and a base name
 * which is different. The base name identifies the type of the node 
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
    public static final double  NODE_HEIGHT = 2.0;
    public static final double  NODE_HEADER_HEIGHT  = 0.4;
    public static final double  NODE_HEADER_PADDING = 0.1;
    public static final Point2D NODE_TITLE_POS = new Point2D(NODE_HEADER_PADDING, NODE_HEADER_HEIGHT - NODE_HEADER_PADDING);
    public static final double  NODE_TITLE_SIZE = 0.3;


    // ################################
    // # INSTANCE VARIABLES
    // ################################

    /**
     * The position of the node in the Graph view (IN GRAPH UNITS)
     */
    private Point2D position;
    /**
     * The name of this individual node
     */
    private String  name;

    // ################################
    // # METHODS
    // ################################

    /**
     * Constructor: Create a new GraphNode at position (0,0)
     * and with a default name equivalent to its base name.
     * 
     * GraphNode is abstract so this can not be called directly, and must
     * be called as 'super()' in the constructor of a subclass
     */
    public GraphNode() {
        position = new Point2D(0.0, 0.0);
        name = getBaseName();
    }

    // ################################
    // # GETTERS / SETTERS
    // ################################

    /**
     * Get the node's base name, identifying the kind of the node this is.
     */
    public String getBaseName() { return "Unnamed Node"; }

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
}