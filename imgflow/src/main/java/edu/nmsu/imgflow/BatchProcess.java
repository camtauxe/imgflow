package edu.nmsu.imgflow;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Callback;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.DirectoryChooser;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;

/**
 * Abstract class to handle the batch processing feature, which allows for
 * running multiple images through the graph in one go.
 * 
 * This class builds a GUI for a dialog box in which the user selects a number
 * of input files, a File IN node, an output directory and a File OUT node. When the
 * process button is pressed, each selected input file is loaded into the selected File IN
 * node and then an image is saved from the selected File OUT node into the output directory.
 */
public abstract class BatchProcess {

    /**
     * The FileChooser that is used for selecting
     * input files
     */
    private static FileChooser fileChooser;

    /**
     * The DirectoryChooser that is used for selecting the output
     * directory
     */
    private static DirectoryChooser dirChooser;

    /**
     * The List of input files chosen with fileChooser.
     * Null or empty if no files are selected
     */
    private static List<File> inputFiles;

    /**
     * The directory chosen with dirChooser.
     * Null if no directory is selected
     */
    private static File outputDir;

    /**
     * The selected File IN node. Null if no 
     * node is selected
     */
    private static GraphNodeFileIn inputNode;

    /**
     * The selected File OUT node. Null if no
     * node is selected
     */
    private static GraphNodeFileOut outputNode;

    /**
     * The stage for the dialog box
     */
    private static Stage window;

    /**
     * Show the dialog box for handling batch processing.
     * This function does not return until the dialog is closed
     */
    public static void showDialog() {
        // reset values which may still be
        // set from the last time the dialog was opened
        inputFiles = null;
        outputDir = null;
        inputNode = null;
        outputNode = null;

        // init stage
        window = new Stage();
        window.initOwner(Main.getInstance().getStage());
        window.initModality(Modality.APPLICATION_MODAL);

        // init scene
        Scene scene = new Scene(buildGUI());
        scene.getStylesheets().add("main.css");
        window.setScene(scene);
        window.sizeToScene();
        window.setResizable(false);

        window.showAndWait();
    }

    /**
     * Build the contents of the dialog box inside a GridPane
     */
    private static GridPane buildGUI() {
        // Create grid pane
        GridPane grid = new GridPane();
        grid.setHgap(5.0);
        grid.setVgap(5.0);
        grid.setPadding(new Insets(5.0));

        // Set column and row constraints
        ColumnConstraints halfWidthColumn = new ColumnConstraints();
        halfWidthColumn.setPercentWidth(50);
        RowConstraints defaultRow = new RowConstraints();
        RowConstraints growRow = new RowConstraints();
        growRow.setVgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(halfWidthColumn, halfWidthColumn);
        grid.getRowConstraints().addAll(defaultRow, defaultRow, growRow, defaultRow);

        // Create and add label at the top of the pane
        Label titleLabel = new Label("Batch Process");
        titleLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        grid.add(titleLabel, 0, 0, 2, 1);

        // Create and add label for the input half of the pane
        Label inputLabel = new Label("Input");
        inputLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(inputLabel, HPos.CENTER);
        grid.add(inputLabel, 0, 1);

        // Create and add label for the output half of the pane
        Label outputLabel = new Label("Output");
        outputLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(outputLabel, HPos.CENTER);
        grid.add(outputLabel, 1, 1);

        // Assemble lists of all of the File IN and File OUT nodes in the active
        // graph. These are used to populate the combo boxes for selecting the nodes
        ArrayList<GraphNodeFileIn> fileInNodes  = new ArrayList<GraphNodeFileIn>();
        ArrayList<GraphNodeFileOut> fileOutNodes = new ArrayList<GraphNodeFileOut>();
        for (GraphNode node : Main.getInstance().getActiveGraph().getNodes()) {
            if (node instanceof GraphNodeFileIn)
                fileInNodes.add((GraphNodeFileIn)node);
            else if (node instanceof GraphNodeFileOut)
                fileOutNodes.add((GraphNodeFileOut)node);
        }

        // Create and add the VBox containing all the input options
        VBox inputBox = new VBox(10.0);
        inputBox.getStyleClass().add("control-box");
        inputBox.setPadding(new Insets(8.0));
        Label inputFilesLabel = new Label("Select Input files");
        Label inputFilesReadout = new Label("No files selected");
        // Add browse button to summon fileChooser
        Button inputFilesBrowse = new Button("Browse...");
        inputFilesBrowse.setOnAction((actionEvent) -> {
            if (fileChooser == null)
                initFileChooser();
            inputFiles = fileChooser.showOpenMultipleDialog(window);
            if (inputFiles == null)
                inputFilesReadout.setText("No files selected");
            else
                inputFilesReadout.setText(inputFiles.size() + " file(s) selected");
        });
        Label inputNodeLabel = new Label("Select Input Node");
        // Create combobox from list of File IN nodes
        ComboBox<GraphNode> inputSelect = createNodeComboBox(fileInNodes);
        inputSelect.getSelectionModel().selectedItemProperty().addListener((oldVal, newVal, obs) -> {
            inputNode = (GraphNodeFileIn) newVal;
        });
        // Select first node in the list (if there is one)
        if (fileInNodes.size() > 0)
            inputNode = fileInNodes.get(0);
        inputBox.getChildren().addAll(inputFilesLabel, inputFilesBrowse, inputFilesReadout, inputNodeLabel, inputSelect);
        grid.add(inputBox, 0, 2);

        // Create and add the VBox containing all the output options
        VBox outputBox = new VBox(10.0);
        outputBox.setPadding(new Insets(8.0));
        outputBox.getStyleClass().add("control-box");
        Label outputDirLabel = new Label("Select Output Directory");
        Label outputDirReadout = new Label("No directory selected");
        // Add browse button to summon dirChooser
        Button outputDirBrowse = new Button("Browse...");
        outputDirBrowse.setOnAction((actionEvent) -> {
            if (dirChooser == null)
                initDirChooser();
            outputDir = dirChooser.showDialog(window);
            if (outputDir == null)
                outputDirReadout.setText("No directory selected");
            else
                outputDirReadout.setText(outputDir.getName());
        });
        Label outputNodeLabel = new Label("Select Output Node");
        // Create combobox from list of File OUT nodes
        ComboBox<GraphNode> outputSelect = createNodeComboBox(fileOutNodes);
        outputSelect.getSelectionModel().selectedItemProperty().addListener((oldVal, newVal, obs) -> {
            outputNode = (GraphNodeFileOut) newVal;
        });
        // Select first node in the list (if there is one)
        if (fileOutNodes.size() > 0)
            outputNode = fileOutNodes.get(0);
        outputBox.getChildren().addAll(outputDirLabel, outputDirBrowse, outputDirReadout, outputNodeLabel, outputSelect);
        grid.add(outputBox, 1, 2);

        // Create and add process button
        Button processButton = new Button("Process!");
        GridPane.setHalignment(processButton, HPos.CENTER);
        processButton.setOnAction((actionEvent) -> {
            // Immediately return if any necessary parts haven't been selected/chosen
            if (
                inputFiles == null || inputFiles.size() == 0 ||
                outputDir == null ||
                inputNode == null ||
                outputNode == null
            ) {
                System.out.println("Could not process! Please make sure an input and output node"+
                    " is selected and that some input files and output directory have been chosen.");
                return;
            }

            try {
                // Iterate through input files
                for (File file : inputFiles) {
                    // load file
                    inputNode.loadFile(file);
                    // Get path for the output file
                    String outputFilename = file.getName().replaceFirst("[.][^.]+$", ""); // filename without extension
                    String outputPath = outputDir.getCanonicalPath()+File.separator+outputFilename+"_out.png";
                    File outputFile = new File(outputPath);
                    // Save output file
                    outputNode.saveToFile(outputFile);
                }
            } catch (IOException e) {
                System.out.println("An IO Exception occurred!");
                System.out.println(e.getMessage());
            }
        });
        grid.add(processButton, 0, 3, 2, 1);

        return grid;
    }

    /**
     * Initialize the FileChooser and set to only
     * load image files
     */
    private static void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Input Files");
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("Image files", "*.png", "*.jpeg", "*.jpg", "*.bmp", "*.gif")
        );
    }

    /**
     * Initialize the directory chooser
     */
    private static void initDirChooser() {
        dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Output Directory");
    }

    /**
     * Create a custom combo box that selects a GraphNode from the given list of nodes.
     * The text in the combo box is the (custom) name of each node.
     */
    private static ComboBox<GraphNode> createNodeComboBox(ArrayList<? extends GraphNode> nodes) {
        ComboBox<GraphNode> cmb = new ComboBox<GraphNode>();
        cmb.getItems().addAll(nodes);

        cmb.setCellFactory(new Callback<ListView<GraphNode>, ListCell<GraphNode>>() {
            public ListCell<GraphNode> call(ListView<GraphNode> list) {
                return new GraphNodeCell();
            }
        });
        cmb.setButtonCell(new GraphNodeCell());

        // Start with the first node selected (if there is one)
        if (nodes.size() > 0)
            cmb.getSelectionModel().select(0);

        return cmb;
    }

    /**
     * A custom ListCell (for a ComboBox) that contains a GraphNode and
     * displays its (custom) name
     */
    private static class GraphNodeCell extends ListCell<GraphNode> {
        protected void updateItem(GraphNode item, boolean empty) {
            if (!empty && item != null) {
                setText(item.getName());
            }
            super.updateItem(item, empty);
        }
    }
}