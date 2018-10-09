package edu.nmsu.imgflow;

import javafx.geometry.Point2D;

public class HoverQuery {

    public static final HoverQuery NO_HOVER = new HoverQuery();

    private GraphNode hoveringNode;
    private boolean overHeader;

    private NodeSocketOutput    hoveringOutputSocket;
    private NodeSocketInput     hoveringInputSocket;

    public static HoverQuery query(Graph graph, Point2D pos) {
        HoverQuery query = new HoverQuery(graph, pos);
        if (query.getHoveringNode() == null)
            return NO_HOVER;
        return query;
    }

    private HoverQuery(Graph graph, Point2D pos) {
        for (GraphNode node : graph.getNodes()) {
            double nodeHeight = (Math.max(node.getNumInputSockets(), node.getNumOutputSockets()) + 1) * GraphNode.NODE_ROW_HEIGHT;
            Point2D relPos = pos.subtract(node.position);
            if (
                relPos.getX() <= GraphNode.NODE_WIDTH && relPos.getX() >= 0.0 &&
                relPos.getY() <= nodeHeight && relPos.getY() >= 0.0
            ) {
                hoveringNode = node;
                overHeader = relPos.getY() <= GraphNode.NODE_ROW_HEIGHT;

                // TODO: Calculate hoveringOutputSocket and hoveringInputSocket
                return;
            }
        }
        hoveringNode            = null;
        overHeader              = false;
        hoveringInputSocket     = null;
        hoveringOutputSocket    = null;
    }

    private HoverQuery() {
        hoveringNode    = null;
        overHeader      = false;
        hoveringInputSocket  = null;
        hoveringOutputSocket = null;
    }

    public GraphNode getHoveringNode() { return hoveringNode; }
    public boolean isOverHeader() { return overHeader; }

    public NodeSocketInput getHoveringInputSocket() { return hoveringInputSocket; }
    public NodeSocketOutput getHoveringOutputSocket() { return hoveringOutputSocket; }
}