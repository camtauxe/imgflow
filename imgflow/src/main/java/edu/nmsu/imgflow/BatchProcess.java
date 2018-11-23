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

public class BatchProcess {

    /**
     * The FileChooser that is used for selecting
     * input files
     */
    private static FileChooser fileChooser;

    private static DirectoryChooser dirChooser;

    private static List<File> inputFiles;

    private static File outputDir;
    
    private static GraphNodeFileIn inputNode;

    private static GraphNodeFileOut outputNode;

    private static Stage window;

    public static void showDialog() {
        inputFiles = null;
        outputDir = null;
        inputNode = null;
        outputNode = null;

        window = new Stage();
        window.initOwner(Main.getInstance().getStage());
        window.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(buildGUI());
        scene.getStylesheets().add("main.css");
        window.setScene(scene);
        window.sizeToScene();

        window.showAndWait();
    }

    private static GridPane buildGUI() {
        GridPane grid = new GridPane();
        grid.setHgap(5.0);
        grid.setVgap(5.0);
        grid.setPadding(new Insets(5.0));

        ColumnConstraints halfWidthColumn = new ColumnConstraints();
        halfWidthColumn.setPercentWidth(50);
        RowConstraints defaultRow = new RowConstraints();
        RowConstraints growRow = new RowConstraints();
        growRow.setVgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(halfWidthColumn, halfWidthColumn);
        grid.getRowConstraints().addAll(defaultRow, defaultRow, growRow, defaultRow);

        Label titleLabel = new Label("Batch Process");
        titleLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        grid.add(titleLabel, 0, 0, 2, 1);

        Label inputLabel = new Label("Input");
        inputLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(inputLabel, HPos.CENTER);
        grid.add(inputLabel, 0, 1);

        Label outputLabel = new Label("Output");
        outputLabel.getStyleClass().add("bold-label");
        GridPane.setHalignment(outputLabel, HPos.CENTER);
        grid.add(outputLabel, 1, 1);

        ArrayList<GraphNodeFileIn> fileInNodes  = new ArrayList<GraphNodeFileIn>();
        ArrayList<GraphNodeFileOut> fileOutNodes = new ArrayList<GraphNodeFileOut>();
        for (GraphNode node : Main.getInstance().getActiveGraph().getNodes()) {
            if (node instanceof GraphNodeFileIn)
                fileInNodes.add((GraphNodeFileIn)node);
            else if (node instanceof GraphNodeFileOut)
                fileOutNodes.add((GraphNodeFileOut)node);
        }

        VBox inputBox = new VBox(10.0);
        inputBox.getStyleClass().add("control-box");
        inputBox.setPadding(new Insets(8.0));
        Label inputFilesLabel = new Label("Select Input files");
        Button inputFilesBrowse = new Button("Browse...");
        inputFilesBrowse.setOnAction((actionEvent) -> {
            if (fileChooser == null)
                initFileChooser();
            inputFiles = fileChooser.showOpenMultipleDialog(window);
        });
        Label inputNodeLabel = new Label("Select Input Node");
        ComboBox<GraphNode> inputSelect = createNodeComboBox(fileInNodes);
        inputSelect.getSelectionModel().selectedItemProperty().addListener((oldVal, newVal, obs) -> {
            inputNode = (GraphNodeFileIn) newVal;
        });
        if (fileInNodes.size() > 0)
            inputNode = fileInNodes.get(0);
        inputBox.getChildren().addAll(inputFilesLabel, inputFilesBrowse, inputNodeLabel, inputSelect);
        grid.add(inputBox, 0, 2);

        VBox outputBox = new VBox(10.0);
        outputBox.setPadding(new Insets(8.0));
        outputBox.getStyleClass().add("control-box");
        Label outputDirLabel = new Label("Select Output Directory");
        Button outputDirBrowse = new Button("Browse...");
        outputDirBrowse.setOnAction((actionEvent) -> {
            if (dirChooser == null)
                initDirChooser();
            outputDir = dirChooser.showDialog(window);
        });
        Label outputNodeLabel = new Label("Select Output Node");
        ComboBox<GraphNode> outputSelect = createNodeComboBox(fileOutNodes);
        outputSelect.getSelectionModel().selectedItemProperty().addListener((oldVal, newVal, obs) -> {
            outputNode = (GraphNodeFileOut) newVal;
        });
        if (fileOutNodes.size() > 0)
            outputNode = fileOutNodes.get(0);
        outputBox.getChildren().addAll(outputDirLabel, outputDirBrowse, outputNodeLabel, outputSelect);
        grid.add(outputBox, 1, 2);

        Button processButton = new Button("Process!");
        GridPane.setHalignment(processButton, HPos.CENTER);
        processButton.setOnAction((actionEvent) -> {
            if (
                inputFiles == null || inputFiles.size() == 0 ||
                outputDir == null ||
                inputNode == null ||
                outputNode == null
            ) {
                System.out.println("Could not process! Please make sure an input and output node"+
                    " is selected and that some input files and output directory have been chosen.");
            }

            try {
                for (File file : inputFiles) {
                    inputNode.loadFile(file);
                    String outputFilename = file.getName().replaceFirst("[.][^.]+$", "");
                    String outputPath = outputDir.getCanonicalPath()+File.separator+outputFilename+"_out.png";
                    File outputFile = new File(outputPath);
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

    private static void initDirChooser() {
        dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Output Directory");
    }

    private static ComboBox<GraphNode> createNodeComboBox(ArrayList<? extends GraphNode> nodes) {
        ComboBox<GraphNode> cmb = new ComboBox<GraphNode>();
        cmb.getItems().addAll(nodes);

        cmb.setCellFactory(new Callback<ListView<GraphNode>, ListCell<GraphNode>>() {
            public ListCell<GraphNode> call(ListView<GraphNode> list) {
                return new GraphNodeCell();
            }
        });
        cmb.setButtonCell(new GraphNodeCell());

        if (nodes.size() > 0)
            cmb.getSelectionModel().select(0);

        return cmb;
    }

    private static class GraphNodeCell extends ListCell<GraphNode> {
        protected void updateItem(GraphNode item, boolean empty) {
            if (!empty && item != null) {
                setText(item.getName());
            }
            super.updateItem(item, empty);
        }
    }
}