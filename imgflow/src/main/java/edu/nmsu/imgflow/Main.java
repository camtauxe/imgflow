package edu.nmsu.imgflow;
// test
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        // Build scene with the result of 'GUIBuilder.createMainWindow()' as
        // the root node, and add to stage.
        scene = new Scene(GUIBuilder.createMainWindow());
        stage.setScene(scene);

        // Set sizing for stage
        stage.setMinWidth(MIN_STAGE_WIDTH);
        stage.setMinHeight(MIN_STAGE_HEIGHT);
        stage.setWidth(DEFAULT_STAGE_WIDTH);
        stage.setHeight(DEFAULT_STAGE_HEIGHT);

        // Show window
        stage.show();
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
}
