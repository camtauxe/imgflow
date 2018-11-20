package edu.nmsu.imgflow;

import java.util.ArrayList;
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

public class BatchProcess {

    /**
     * The FileChooser that is used for selecting
     * input files
     */
    private static FileChooser fileChooser;

    private static Stage window;

    public static void showDialog() {
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
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        grid.add(titleLabel, 0, 0, 2, 1);

        Label inputLabel = new Label("Input");
        GridPane.setHalignment(inputLabel, HPos.CENTER);
        grid.add(inputLabel, 0, 1);

        Label outputLabel = new Label("Output");
        GridPane.setHalignment(outputLabel, HPos.CENTER);
        grid.add(outputLabel, 1, 1);

        ArrayList<GraphNode> fileInNodes  = new ArrayList<GraphNode>();
        ArrayList<GraphNode> fileOutNodes = new ArrayList<GraphNode>();
        for (GraphNode node : Main.getInstance().getActiveGraph().getNodes()) {
            if (node instanceof GraphNodeFileIn)
                fileInNodes.add(node);
            else if (node instanceof GraphNodeFileOut)
                fileOutNodes.add(node);
        }

        VBox inputBox = new VBox(10.0);
        Label inputFilesLabel = new Label("Select Input files");
        Button inputFilesBrowse = new Button("Browse...");
        inputFilesBrowse.setOnAction((actionEvent) -> {
            if (fileChooser == null)
                initFileChooser();
            fileChooser.showOpenMultipleDialog(window);
        });
        Label inputNodeLabel = new Label("Select Input Node");
        ComboBox<GraphNode> inputSelect = createNodeComboBox(fileInNodes);
        inputBox.getChildren().addAll(inputFilesLabel, inputFilesBrowse, inputNodeLabel, inputSelect);


        return grid;
    }

    /**
     * Initialize the FileChooser and set to only
     * load image files
     */
    private static void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Imgflow Graph");
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("Image files", "*.png", "*.jpeg", "*.jpg", "*.bmp", "*.gif")
        );
    }

    private static ComboBox<GraphNode> createNodeComboBox(ArrayList<GraphNode> nodes) {
        ComboBox<GraphNode> cmb = new ComboBox<GraphNode>();

        cmb.setCellFactory(new Callback<ListView<GraphNode>, ListCell<GraphNode>>() {
            public ListCell<GraphNode> call(ListView<GraphNode> list) {
                return new ListCell<GraphNode>() {
                    protected void updateItem(GraphNode item, boolean empty) {
                        if (!empty && item != null) {
                            setText(item.getName());
                        }
                    }
                };
            }
        });

        cmb.getItems().addAll(nodes);

        return cmb;
    }
}