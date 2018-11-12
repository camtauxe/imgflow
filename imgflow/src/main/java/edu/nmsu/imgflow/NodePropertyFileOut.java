package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.embed.swing.SwingFXUtils;

/**
 * The NodePropertyFileIn is a kind of NodeProperty that reads
 * in a file from disk and returns it as a WritableImage
 */
public class NodePropertyFileOut extends NodeProperty<WritableImage> {

    // GUI Components
    private VBox        vbox;
    private Label       label;
    private Button      saveButton;

    private FileChooser chooser;

    /**
     * Create a new NodePropertyFileIn with the given parent node.
     * Note that the parent node can ONLY be a GraphNodeFileOut
     */
    public NodePropertyFileOut(GraphNodeFileOut parent) {
        super(parent);
        buildGUI();

        chooser = new FileChooser();
        chooser.setTitle(" image file");
        chooser.getExtensionFilters().add(
            new ExtensionFilter("PNG files", "*.png")
        );
    }

    /**
     * We don't want to save the selected file (or rather, the path)
     * when saving this node to a file, so it's serialized value
     * will always just be 'null'
     */
    public String serializeValue() { return "null"; }

    /**
     * The File Out property does not do any saving or loading,
     * so this function does nothing
     */
    public void valueFromString(String str) {}; 

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        vbox        = new VBox(5.0);
        label       = new Label("File output");
        saveButton  = new Button("Save");

        vbox.getChildren().addAll(label, saveButton);

        saveButton.setOnAction((actionEvent) -> {
            GraphNodeFileOut node = (GraphNodeFileOut)parentNode;
            node.getInputSocket().requestUpdate();
            WritableImage img = node.getInputSocket().getImage();
            if (img == null) {
                System.out.println("No input image available! Unable to save image!");
            }
            else {
                RenderedImage rendered = SwingFXUtils.fromFXImage(img, null);
                File file = chooser.showSaveDialog(Main.getInstance().getStage());
                try {
                    ImageIO.write(rendered, "png", file);
                    System.out.println("Succesfully saved image!");
                } catch (Exception e) {
                    System.out.println("Error saving file!");
                    System.out.println(e.getMessage());
                }
            }

        });

        GUIContent = vbox;
    }
}