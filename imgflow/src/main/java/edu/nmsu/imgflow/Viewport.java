package edu.nmsu.imgflow;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javafx.geometry.Point2D;

import javafx.beans.value.ChangeListener;
import javafx.scene.input.MouseButton;
import javafx.scene.Cursor;

/**
 * Represents a view of a graph on a canvas, allowing the user to view a graph,
 * select and move nodes and make/delete connections.
 * 
 * The Viewport references a JavaFX GridPane which, apart from some controls, contains a JavaFX
 * canvas on which the view of the graph is drawn. It is VERY IMPORTANT to understand how the sizing
 * and positioning of elements on the canvas works. The coordinate system on the graph uses arbitrary units
 * which we're calling "Graph Units" (with the origin being in the center of the graph). Graph Units are distinct
 * from the usual pixel coordinate system used on the canvas (where each unit is one pixel in length and the
 * origin is in the upper-left-hand corner of the canvas), but it is possible to convert between graph units
 * and pixel units. This distinction is made so that the canvas can act as a sort of "window" into the graph
 * where the position and size of the window can change, changing what is drawn on the the canvas without changing
 * the internal positions of any elements on the graph. Each time the canvas is drawn, the Graphics Context is
 * transformed into the graph unit system so draw calls are made using graph units.
 */
public class Viewport {

    // ################################
    // # STATIC VARIABLES AND CONSTANTS
    // ################################

    /**
     * The number of pixels in a single graph unit (WHILE AT 1.0 ZOOM)
     */
    private static final double PIXELS_PER_UNIT = 100.0;
    /**
     * The most the user can zoom-in (as a multiplier to PIXELS_PER_UNIT)
     */
    private static final double MAX_ZOOM        = 50.0;
    /**
     * The most the user can zoom-out (as a multiplier to PIXELS_PER_UNIT)
     */
    private static final double MIN_ZOOM        = 0.05;
    /**
     * The speed of zooming (as a multiplier on the scroll units from the scroll event)
     */
    private static final double ZOOM_SPEED      = 0.001;

    // ################################
    // # INSTANCE VARIABLES
    // ################################

    /**
     * The current zoom level (as a multiplier on PIXELS_PER_UNIT)
     */
    private double  viewportZoom   = 1.0;
    /**
     * The point on the graph in the exact center of the viewport (in graph units)
     */
    private Point2D viewportCenter = new Point2D(0.0, 0.0);
    /**
     * While panning the view, the point on the graph where the mouse cursor is.
     * (While not dragging, this should be null)
     */
    private Point2D dragAnchor;

    /**
     * The graph being displayed
     */
    private Graph graph;
    /**
     * The HoverQuery with information regarding what the mouse is
     * currently hovering over
     */
    private HoverQuery hoverQuery;
    /**
     * The HoverQuery from the last mouse event
     */
    private HoverQuery prevHoverQuery;
    /**
     * If a node is currently being clicked-and-dragged by the mouse, this references it.
     * Null if no node is being dragged.
     */
    private GraphNode draggingNode;
    /**
     * If a node is currently being dragged, this is the position of the mouse cursor
     * (relative to the position of the node, in graph units). Null if no node is being dragged.
     */
    private Point2D draggingNodeOffset;
    /**
     * The node that is currently selected, or null if no node is selected.
     */
    private GraphNode selectedNode;
    /**
     * The list of registered node selection listeners, triggered
     * when the selection changes.
     */
    private ArrayList<NodeSelectListener> nodeSelectListeners;


    /**
     * The JavaFX GridPane containing the canvas and other controls
     */
    private GridPane pane;
    /**
     * The JavaFX canvas on which the graph is drawn.
     */
    private Canvas canvas;

    // ################################
    // # METHODS
    // ################################

    /**
     * Create a new Viewport displaying the given graph.
     */
    public Viewport(Graph graph) {
        this.graph = graph;
        nodeSelectListeners = new ArrayList<NodeSelectListener>();
        prevHoverQuery = HoverQuery.NO_HOVER;
        hoverQuery = HoverQuery.NO_HOVER;
        buildPane();
    }

    /**
     * Build the GUI contents of the Viewport
     */
    private void buildPane() {
        pane = new GridPane();
        pane.setGridLinesVisible(true); //for debug

        // Set Columns on the GridPane
        // First column expands to fill space
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        // Second column is of a fixed width
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPrefWidth(25.0);
        pane.getColumnConstraints().addAll(column1, column2);

        // Set rows on the GridPane
        // First row expands to fill space
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        // Second row is of a fixed height
        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(25.0);
        pane.getRowConstraints().addAll(row1, row2);

        // Create a Pane which will contain the canvas
        Pane canvasWrapper = new Pane();

        // Create the canvas
        canvas = new Canvas();
        canvas.setManaged(false);
        canvasWrapper.getChildren().add(canvas);

        // Bind the Canvas to always fit exactly within the wrapper
        canvas.setLayoutX(0.0);
        canvas.setLayoutY(0.0);
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());

        addListenersToCanvas(canvas);

        pane.add(canvasWrapper, 0, 0);
    }

    /**
     * Redraw the graph
     */
    private void redraw() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        // Fill background
        ctx.setFill(Color.LIGHTGRAY);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        // Transform context to draw in graph units
        ctx.save();
        transformContextToGraphSpace(ctx);

        drawGrid(ctx);
        for (GraphNode node : graph.getNodes()) {
            node.draw(this);
        }

        ctx.restore(); // transform back

        // Draw border
        ctx.setLineWidth(1.5);
        ctx.setStroke(Color.BLACK);
        ctx.strokeRect(10.0, 10.0, canvas.getWidth()-20.0, canvas.getHeight()-20.0);
    }

    /**
     * Add all necessary mouse and other property listeners to the canvas to add functionality.
     */
    private void addListenersToCanvas(Canvas canvas) {
        // Redraw whenever the size of the canvas changes
        ChangeListener<Number> onResize = (obs, oldVal, newVal) -> {
            redraw();
        };
        canvas.widthProperty().addListener(onResize);
        canvas.heightProperty().addListener(onResize);

        // Change cursor when mouse enters or exits the canvas
        canvas.setOnMouseEntered((mouseEvent) -> {
            Main.getInstance().getScene().setCursor(Cursor.HAND);
        });
        canvas.setOnMouseExited((mouseEvent) -> {
            Main.getInstance().getScene().setCursor(Cursor.DEFAULT);
        });

        // Respond to mouse movement (while not dragging)
        canvas.setOnMouseMoved((mouseEvent) -> {
            // Get coordinate of mouse in graph units
            Point2D graphCoord = canvasCoordToGraphCoord(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
            // update hoverQuery
            hoverQuery = HoverQuery.query(graph, graphCoord);

            // If we are over a node, set cursor accordingly
            if (hoverQuery != HoverQuery.NO_HOVER) {
                if (hoverQuery.isOverHeader())
                    Main.getInstance().getScene().setCursor(Cursor.MOVE);
                else if (hoverQuery.isOverSocket())
                    Main.getInstance().getScene().setCursor(Cursor.CROSSHAIR);
                else
                    Main.getInstance().getScene().setCursor(Cursor.DEFAULT);
            }
            // If the hoveringNode has changed from the last mouse event, update cursor and redraw
            if (hoverQuery.getHoveringNode() != prevHoverQuery.getHoveringNode()) {
                if (hoverQuery == HoverQuery.NO_HOVER) {
                    Main.getInstance().getScene().setCursor(Cursor.HAND);
                }
                prevHoverQuery = hoverQuery;
                redraw();
            }
        });

        // Respond to a mouse button press
        canvas.setOnMousePressed((mouseEvent) -> {
            // Only respond to a press of the left mouse button
            if (mouseEvent.getButton() != MouseButton.PRIMARY) return;

            // Get the position of the mouse in graph units
            Point2D pixelCoord = new Point2D(mouseEvent.getX(), mouseEvent.getY());
            Point2D graphCoord = canvasCoordToGraphCoord(pixelCoord);

            // If hovering over the header of a node, initiate a drag for that node
            if (hoverQuery.isOverHeader()) {
                draggingNode = hoverQuery.getHoveringNode();
                draggingNodeOffset = graphCoord.subtract(draggingNode.getPosition());
            }
            // Otherwise, initiate a drag to pan the view
            else {
                Main.getInstance().getScene().setCursor(Cursor.CLOSED_HAND);
                dragAnchor = graphCoord;
            }
        });

        // Respond to a mouse drag (movement while a button is pressed)
        canvas.setOnMouseDragged((mouseEvent) -> {
            // Get the position of the mouse in graph units
            Point2D pixelCoord = new Point2D(mouseEvent.getX(), mouseEvent.getY());
            Point2D graphCoord = canvasCoordToGraphCoord(pixelCoord);

            // If dragging a node, adjust the position of that node
            if (draggingNode != null) {
                draggingNode.setPosition(graphCoord.subtract(draggingNodeOffset));
                redraw();
            }
            // If panning the view, adjust the viewport center position
            else if (dragAnchor != null) {
                viewportCenter = viewportCenter.subtract(graphCoord.subtract(dragAnchor));
                redraw();
            }
        });

        // Handle a mouse button release
        canvas.setOnMouseReleased((mouseEvent) -> {
            // Select a node if one is being hovered over
            // Otherwise, deselect the selected node
            if (hoverQuery != HoverQuery.NO_HOVER && hoverQuery.getHoveringNode() != selectedNode) {
                selectedNode = hoverQuery.getHoveringNode();
                redraw();
                for (NodeSelectListener listener : nodeSelectListeners)
                    listener.handle(selectedNode);
            }
            else if (hoverQuery == HoverQuery.NO_HOVER && selectedNode != null) {
                selectedNode = null;
                redraw();
                for (NodeSelectListener listener : nodeSelectListeners)
                    listener.handle(null);
            }
            if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
            dragAnchor          = null;
            draggingNode        = null;
            draggingNodeOffset  = null;
        });

        // Handle a mouse scroll
        canvas.setOnScroll((scrollEvent) -> {
            // Adjust zoom
            double newZoom = viewportZoom + (scrollEvent.getDeltaY() * ZOOM_SPEED);
            if (newZoom > MAX_ZOOM) newZoom = MAX_ZOOM;
            if (newZoom < MIN_ZOOM) newZoom = MIN_ZOOM;
            viewportZoom = newZoom;

            // TODO: Adjust viewportCenter so that zoom occurs centered around mouse location

            redraw();
        });
    }

    /**
     * Draw the grid in the background of the viewport.
     */
    private void drawGrid(GraphicsContext ctx) {
        ctx.setStroke(Color.DIMGRAY);

        // Get the width and height of the canvas in graph units
        double viewportWidth  = canvas.getWidth()  / PIXELS_PER_UNIT / viewportZoom;
        double viewportHeight = canvas.getHeight() / PIXELS_PER_UNIT / viewportZoom;

        // Iterate through the units on the x-axis (that are within view) and draw vertical lines for each one
        for (
            int i   = (int) Math.floor(viewportCenter.getX() - viewportWidth / 2.0);
            i       <=      Math.ceil(viewportCenter.getX()  + viewportWidth / 2.0);
            i++
        ) {
            // The line through the origin is drawn thicker
            if (i == 0)
                ctx.setLineWidth(2.5 / PIXELS_PER_UNIT / viewportZoom);
            else
                ctx.setLineWidth(1.0 / PIXELS_PER_UNIT / viewportZoom);
            ctx.strokeLine(
                i, (viewportCenter.getY() - viewportHeight / 2.0),
                i, (viewportCenter.getY() + viewportHeight / 2.0)
            );
        }
        // Iterate through the units on the y-axis (that are within view) and draw horizontal lines for each one
        for (
            int i   = (int) Math.floor(viewportCenter.getY() - viewportHeight / 2.0);
            i       <=      Math.ceil(viewportCenter.getY()  + viewportHeight / 2.0);
            i++
        ) {
            // The line through the origin is drawn thicker
            if (i == 0)
                ctx.setLineWidth(2.5 / PIXELS_PER_UNIT / viewportZoom);
            else
                ctx.setLineWidth(1.0 / PIXELS_PER_UNIT / viewportZoom);
            ctx.strokeLine(
                (viewportCenter.getX() - viewportWidth / 2.0), i,
                (viewportCenter.getX() + viewportWidth / 2.0), i
            );
        }
    }

    // ################################
    // # PUBLIC METHODS
    // ################################

    /**
     * Transform the canvas context from drawing in canvas coordinates to graph coordinates.
     * So that drawing a distance of 1 draws a length of one graph unit regardless of zoom level
     * and drawing at (0,0) draws at the graph origin point regardless of viewport position.
     */
    public void transformContextToGraphSpace(GraphicsContext ctx) {
        double scaleFactor = PIXELS_PER_UNIT * viewportZoom;
        ctx.scale(scaleFactor, scaleFactor);

        ctx.translate(
            (canvas.getWidth()  / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getX(),
            (canvas.getHeight() / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getY()
        );
    }

    /**
     * Transform the canvas context from drawing in graph coordinates to canvas coordinates.
     * So that drawing a distance of 1 draws a length of one pixel and drawing at (0,0)
     * draws in the upper-left-hand corner of the viewport.
     */
    public void transformContextToCanvasSpace(GraphicsContext ctx) {
        ctx.translate(
            viewportCenter.getX() - (canvas.getWidth()  / PIXELS_PER_UNIT / 2.0 / viewportZoom),
            viewportCenter.getY() - (canvas.getHeight() / PIXELS_PER_UNIT / 2.0 / viewportZoom)
        );

        double scaleFactor = 1.0 / (PIXELS_PER_UNIT * viewportZoom);
        ctx.scale(scaleFactor, scaleFactor);
    }

    /**
     * Convert a length in pixels on the canvas to length
     * in graph units (at the viewport's current zoom level)
     */
    public double pixelsToGraphUnits(double pixels) {
        return pixels / PIXELS_PER_UNIT / viewportZoom;
    }

    /**
     * Convert a length in graph units to a length in pixels
     * on the canvas (at the viewport's current zoom level)
     */
    public double graphUnitsToPixels(double units) {
        return units * PIXELS_PER_UNIT * viewportZoom;
    }

    /**
     * Convert a point from canvas coordinates to a graph coordinate.
     * (This returns a new point and does not alter the given point (Point2D's are immutable))
     */
    public Point2D canvasCoordToGraphCoord(Point2D point) {
        double scaleFactor = 1.0 / PIXELS_PER_UNIT / viewportZoom;
        return point
            .multiply(scaleFactor)
            .subtract(
                (canvas.getWidth()  / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getX(),
                (canvas.getHeight() / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getY()
            ); 
    }

    /**
     * Convert a point from a graph coordinate to canvas coordinates
     * (This returns a new point and does not alter the given point (Point2D's are immutable))
     */
    public Point2D graphCoordToCanvasCoord(Point2D point) {
        double scaleFactor = PIXELS_PER_UNIT * viewportZoom;
        return point
            .add(
                (canvas.getWidth()  / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getX(),
                (canvas.getHeight() / PIXELS_PER_UNIT / 2.0 / viewportZoom) - viewportCenter.getY()
            )
            .multiply(scaleFactor);
    }

    /**
     * Add a node select listener to this viewport. It will be invoked
     * whenever the selected node changes (this includes when a node
     * is deselected)
     */
    public void addNodeSelectListener(NodeSelectListener listener) {
        nodeSelectListeners.add(listener);
    }

    // ################################
    // # GETTERS / SETTERS
    // ################################

    /**
     * Get the JavaFX containing this Viewport's GUI content
     */
    public Pane getPane() { return pane; }

    /**
     * Get the graphics context for this viewport's canvas
     */
    public GraphicsContext getGraphicsContext() { return canvas.getGraphicsContext2D(); }

    /**
     * Get the current hover query containing information about what is 
     * being hovered over in the viewport
     */
    public HoverQuery getHoverQuery() { return hoverQuery; }

    /**
     * Get the currently selected node.
     * Null if no node is selected.
     */
    public GraphNode getSelectedNode() { return selectedNode; }

}