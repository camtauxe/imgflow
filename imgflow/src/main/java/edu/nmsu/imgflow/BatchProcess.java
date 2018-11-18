package edu.nmsu.imgflow;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import java.io.File;

public class BatchProcess {

    public static void showDialog() {
        Stage window = new Stage();
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

        ColumnConstraints halfWidthColumn = new ColumnConstraints();
        halfWidthColumn.setPercentWidth(50);
        RowConstraints defaultRow = new RowConstraints();
        RowConstraints growRow = new RowConstraints();
        growRow.setVgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(halfWidthColumn, halfWidthColumn);
        grid.getRowConstraints().addAll(defaultRow, defaultRow, growRow, defaultRow);

        Label titleLabel = new Label("Batch Process");
        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
        grid.add(titleLabel, 0, 0, 2, 1);

        return grid;
    }
}