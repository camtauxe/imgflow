package edu.nmsu.imgflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Main class for the Imgflow application
 * 
 * Inherits from the JavaFX Application class and contains the main method which
 * launches an instance of itself.
 * 
 * Statically hold a reference to the instance of the application which can be retrieved with
 * getInstance(). This instance contains individual instance variables such as a reference to the JavaFX stage
 * and its scene.
 */
public class Main extends Application {

    // ################################
    // # STATIC VARIABLES AND CONSTANTS
    // ################################

    // The minimum size for the stage (window). The window will not allow itself to be resized below this value
    private static final double MIN_STAGE_WIDTH  = 800.0;
    private static final double MIN_STAGE_HEIGHT = 600.0;

    // The default size for the stage (window). The window will open at this size.
    private static final double DEFAULT_STAGE_WIDTH  = 1024.0;
    private static final double DEFAULT_STAGE_HEIGHT = 768.0;

    /**
     * The current instance of the application.
     */
    private static Main instance;

    // ################################
    // # INSTANCE VARIABLES
    // ################################

    /**
     * The JavaFX stage (the application window)
     */
    private Stage stage;
    /**
     * The JavaFX scene (the top-level of the GUI hierarchy)
     */
    private Scene scene;

    /**
     * The graph that is currently being "worked on" (i.e. displayed
     * and editable in the viewport and property panel)
     */
    private Graph activeGraph;

    /**
     * The active viewport
     */
    private Viewport viewport;

    /**
     * The active property panel
     */
    private PropertyPanel propertyPanel;

    // ################################
    // # METHODS
    // ################################

    /**
     * Main method: Launches an instance of Main
     */
    public static void main( String[] args ) {
        // This is a JavaFX Application method.
        // It does some groundwork, creates a stage and then
        // calls 'start' with a reference to that stage on
        // the JavaFX Application thread.
        launch();
    }

    /**
     * Start the appliction. Build the GUI and show the stage.
     * 
     * This will be called automatically by JavaFX after 'launch'
     * is called in the main method.
     */
    public void start(Stage s) {
        // Set instance
        instance = this;
        stage = s;

        // Build scene with the result of createMainWindow() as
        // the root node, and add to stage.
        scene = new Scene(createMainWindow());
        stage.setScene(scene);

        // Set sizing for stage
        stage.setMinWidth(MIN_STAGE_WIDTH);
        stage.setMinHeight(MIN_STAGE_HEIGHT);
        stage.setWidth(DEFAULT_STAGE_WIDTH);
        stage.setHeight(DEFAULT_STAGE_HEIGHT);

        // Show window
        stage.show();
    }

    /**
     * Create a JavaFX Pane representing the top-level of the application GUI.
     * This should be the root node of the main scene.
     * 
     * The Pane is a GridPane with 2 columns and two rows.
     * The top row spans both columns and contains the MenuBar while the second row is split
     * into the two columns. The right-most column is of a fixed-width
     * while the left-most column grows to fill the remaining space. The left column contains
     * an instance of Viewport (referring to a new Graph instance) while the right column
     * contains the property panel whose contents change depending on the selected node.
     * 
     * @return The created JavaFX Pane
     */
    private Pane createMainWindow() {

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
        RowConstraints row1 = new RowConstraints();
        // Second row grows to fill available space
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        pane.getRowConstraints().addAll(row1, row2);

        // Create MenuBar
        MenuBar menuBar = new MenuBar();
        // Create "File" Menu
        Menu fileMenu = new Menu("File");
        // Add menu items
        MenuItem save = new MenuItem("Save Graph");
        save.setOnAction((actionEvent) -> {
            GraphSaveLoad.chooseFileAndSaveGraph(activeGraph);
        });
        MenuItem load = new MenuItem("Load Graph");
        fileMenu.getItems().addAll(save, load);
        // Add node creation menu
        Menu createMenu = NodeFactory.buildNodeCreationMenu();
        // Add menus to menu bar
        menuBar.getMenus().addAll(fileMenu, createMenu);
        pane.add(menuBar, 0, 0, 2, 1);

        // Create viewport and add to first row, first column
        activeGraph = new Graph();
        viewport = new Viewport(activeGraph);
        pane.add(viewport.getPane(), 0, 1);

        // Create Property Panel and add to first row, second column
        propertyPanel = new PropertyPanel();
        pane.add(propertyPanel.getPane(), 1, 1);

        // Update contents of property panel when the selection
        // in the viewport changes
        activeGraph.addNodeSelectListener((node) -> {
            propertyPanel.updateSelectedNode(node);
        });

        return pane;
    }

    // ################################
    // # SETTERS / GETTERS
    // ################################

    /**
     * Get the application instance
     */
    public static Main getInstance() { return instance; }

    /**
     * Get the JavaFX stage (application window)
     */
    public Stage getStage() { return stage; }
    /**
     * Get the JavaFX scene
     */
    public Scene getScene() { return scene; }

    /**
     * Get the Graph that is currently being worked on.
     */
    public Graph getActiveGraph() { return activeGraph; }

    /**
     * Get the active viewport
     */
    public Viewport getViewport() { return viewport; }

    /**
     * Get the active property panel
     */
    public PropertyPanel getPropertyPanel() { return propertyPanel; }
}
