package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

/**
 * A HoverQuery represents a bundle of information about what elements of a graph
 * a certain point is hovering over. Given a reference to a graph and a point on this
 * graph (in graph units!) the resulting HoverQuery specifies what node the point is
 * over (if any), if that point is over the node's header and what socket on that node
 * the point is over (if any)
 */
public class HoverQuery {

    /**
     * Static instance representing a query where
     * the point was not over anything. Instances gotten with the
     * the query() method can be compared with this to test if
     * their point was over any node.
    */
    public static final HoverQuery NO_HOVER = new HoverQuery();

    /**
     * The node that this query's point is over.
     * Or null if the point is not over any node.
     */
    private GraphNode  hoveringNode;
    /**
     * The socket that this query's point is over.
     * Or null if the point is not over any socket.
     */
    private NodeSocket hoveringSocket;

    /**
     * True if the point is over a socket. False otherwise
     */
    private boolean overSocket;
    /**
     * True if the point is over the header of the hovering node. False otherwise
     */
    private boolean overHeader;

    /**
     * Get a HoverQuery for the given point in the given graph.
     * This will return either a new instance of HoverQuery if the
     * point is over a node, or simply a reference to the NO_HOVER
     * singleton if the point is not over any node.
     */
    public static HoverQuery query(Graph graph, Point2D pos) {
        HoverQuery query = new HoverQuery(graph, pos);
        if (query.getHoveringNode() == null)
            return NO_HOVER;
        return query;
    }

    /**
     * Create a new HoverQuery for the given point in the given graph.
     */
    private HoverQuery(Graph graph, Point2D pos) {
        super();
        // Iterate through nodes and check if the point is over each one
        for (GraphNode node : graph.getNodes()) {
            // Get the height of the node in graph units (the width is constant)
            double nodeHeight = (Math.max(node.getNumInputSockets(), node.getNumOutputSockets()) + 1) * GraphNode.NODE_ROW_HEIGHT;
            // Get the position relative to the position of the node
            Point2D relPos = pos.subtract(node.position);
            // test if the point is overlapping the node's bounding box
            if (
                relPos.getX() <= GraphNode.NODE_WIDTH && relPos.getX() >= 0.0 &&
                relPos.getY() <= nodeHeight && relPos.getY() >= 0.0
            ) {
                hoveringNode = node;
                // if the point is in the node's first row (the header), overHeader is true
                overHeader = relPos.getY() <= GraphNode.NODE_ROW_HEIGHT;

                // iterate through sockets and check for overlap
                for (NodeSocket socket : node.getAllSockets()) {
                    Point2D socketPos = socket.getPosition();
                    // test if the point is overlapping the socket's bounding box
                    if (
                        relPos.getX() >= socketPos.getX() &&
                        relPos.getX() <= socketPos.getX() + GraphNode.NODE_SOCKET_SIZE &&
                        relPos.getY() >= socketPos.getY() &&
                        relPos.getY() <= socketPos.getY() + GraphNode.NODE_SOCKET_SIZE
                    ) {
                        hoveringSocket = socket;
                        overSocket = true;
                    }
                } // end for sockets
            } // end if hovering
        } // end for nodes
    }

    /**
     * Create a new hover query representing a query that
     * hovers over no nodes
     */
    private HoverQuery() {
        hoveringNode    = null;
        hoveringSocket  = null;
        overHeader      = false;
        overSocket      = false;
    }

    // Getters

    public GraphNode getHoveringNode() { return hoveringNode; }
    public NodeSocket getHoveringSocket() { return hoveringSocket; }
    public boolean isOverHeader() { return overHeader; }
    public boolean isOverSocket() { return overSocket; }
}