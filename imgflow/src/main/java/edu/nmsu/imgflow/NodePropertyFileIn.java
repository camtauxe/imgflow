package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The NodePropertyFileIn is a kind of NodeProperty that reads
 * in a file from disk and returns it as a WritableImage
 */
public class NodePropertyFileIn extends NodeProperty<WritableImage> {

    // GUI Components
    private VBox        vbox;
    private Label       label;
    private Button      loadButton;
    
    private FileChooser chooser;

    /**
     * Create a new NodePropertyFileIn with the given parent node
     */
    public NodePropertyFileIn(GraphNode parent) {
        super(parent);

        chooser = new FileChooser();
        chooser.setTitle("Open image file");
        chooser.getExtensionFilters().add(
            new ExtensionFilter("Image files", "*.png", "*.jpeg", "*.jpg", "*.bmp", "*.gif")
        );

        buildGUI();
    }

    public String serializeValue() { return "null"; }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        vbox        = new VBox(5.0);
        label       = new Label("File input");
        loadButton  = new Button("Load");

        vbox.getChildren().addAll(label, loadButton);

        loadButton.setOnAction((actionEvent) -> {
            try {
                File file = chooser.showOpenDialog(Main.getInstance().getStage());
                String url = file.toURI().toURL().toExternalForm();
                Image img = new Image(url);
                if (img.isError())
                    throw img.getException();
                value = new WritableImage(img.getPixelReader(), (int)img.getWidth(), (int)img.getHeight());
                System.out.println("Successfully loaded image!");
            } catch (Exception e) {
                System.out.println("Error loading image!");
                System.out.println(e.getClass() + " : " + e.getMessage());
                value = null;
            } finally {
                parentNode.onPropertyUpdate(this);
            }
        });

        GUIContent = vbox;
    }
}