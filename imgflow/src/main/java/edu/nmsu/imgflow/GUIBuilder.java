package edu.nmsu.imgflow;

import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;

/**
 * Contains static methods for building various GUI components
 * used in the application.
 */
public abstract class GUIBuilder {

    /**
     * Create a JavaFX Pane representing the top-level of the application GUI.
     * This should be the root node of the main scene.
     * 
     * The Pane is a GridPane with 2 columns. The right-most column is of a fixed-width
     * while the left-most column grows to fill the remaining space. The left column contains
     * an instance of Viewport (referring to a new Graph instance) while the right column
     * contains information and settings pertaining to the currently selected Node (not yet implemented)
     * 
     * @return The created JavaFX Pane
     */
    public static Pane createMainWindow() {

        GridPane pane = new GridPane();

        pane.setGridLinesVisible(true); // For debug, disable this for release

        // Set column contraints
        // First column grows to fill available space
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        // Second column is of a fixed width (in pixels)
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPrefWidth(300.0);
        pane.getColumnConstraints().addAll(column1, column2);

        // Set row constraints
        // Just one row which grows to fill the space
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        pane.getRowConstraints().add(row1);

        // Create viewport and add to first row, first column
        Viewport viewport = new Viewport(Graph.buildTestGraph());
        pane.add(viewport.getPane(), 0, 0);

        return pane;
    }
}