package edu.nmsu.imgflow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * A Panel in the GUI which displays properties for a node.
 * 
 * It consists of a VBox that contains a thumbnail preview of a node,
 * a button to delete the node, and most importantly, another VBox that contains
 * all of the properties of the selected node. When no node is selected, the property
 * VBox is replaced with a message reading "No node selected"
 */
public class PropertyPanel {

    /**
     * The VBox containing all of the property panel's content
     */
    private VBox vbox;
    /**
     * The ImageView used to draw a thumbnail of the selected node
     */
    private ImageView preview;
    /**
     * A label displaying the type of the selected node
     */
    private Label nodeLabel;
    /**
     * The VBox containing the node's properties' GUI
     */
    private VBox propertyBox;
    /**
     * The warning displayed when no node is selected
     */
    private Label noSelectionLabel;
    /**
     * A pane to contain the noSelectionLabel for layout purposes
     */
    private StackPane labelWrapper;
    /**
     * Button to delete the selected node from the graph
     */
    private Button deleteNodeButton;
    /**
     * The currently selected node. Null if no node is selected.
     */
    private GraphNode selectedNode;

    /**
     * Construct a new PropertyPanel with no selected node
     */
    public PropertyPanel() {
        vbox = new VBox(5.0);
        vbox.setPadding(new Insets(20.0, 15.0, 20.0, 15.0));

        // Create a wrapper pane and place the the preview ImageView inside it
        Pane previewWrapper = new Pane();
        vbox.getChildren().add(previewWrapper);
        // Create preview ImageView
        preview = new ImageView();
        preview.getStyleClass().add("preview-img");
        preview.setPreserveRatio(true);
        // Bind ImageView size to wrapper
        preview.fitWidthProperty().bind(previewWrapper.widthProperty());
        preview.fitHeightProperty().bind(preview.fitWidthProperty());
        previewWrapper.getChildren().add(preview);

        nodeLabel = new Label("");
        nodeLabel.getStyleClass().add("node-label");
        HBox nodeLabelWrapper = new HBox();
        nodeLabelWrapper.setAlignment(Pos.BASELINE_CENTER);
        nodeLabelWrapper.getChildren().add(nodeLabel);
        vbox.getChildren().add(nodeLabelWrapper);

        noSelectionLabel = new Label("No Node selected...");
        noSelectionLabel.getStyleClass().add("no-selection-label");
        labelWrapper = new StackPane();
        labelWrapper.setAlignment(Pos.CENTER);
        labelWrapper.getChildren().add(noSelectionLabel);
        VBox.setVgrow(labelWrapper, Priority.ALWAYS);

        propertyBox = new VBox(10.0);
        propertyBox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        propertyBox.getStyleClass().add("property-box");
        VBox.setVgrow(propertyBox, Priority.ALWAYS);
        vbox.getChildren().add(propertyBox);

        deleteNodeButton = new Button("Delete Node");
        deleteNodeButton.setOnAction((actionEvent) -> {
            if (selectedNode == null) return;
            selectedNode.disconnectAllSockets();
            Main.getInstance().getActiveGraph().getNodes().remove(selectedNode);
            updateSelectedNode(null);
        });
        // The Delete node button is placed inside an Hbox to right-align it
        HBox buttonWrapper = new HBox();
        buttonWrapper.setAlignment(Pos.BASELINE_RIGHT);
        buttonWrapper.getChildren().add(deleteNodeButton);
        vbox.getChildren().add(buttonWrapper);

        updateSelectedNode(null);
    }

    /**
     * Get the layout pane for the panel's GUI content
     */
    public Pane getPane() { return vbox; }

    /**
     * Change the node to be displayed in the panel and update
     * panel contents accordingly.
     */
    public void updateSelectedNode(GraphNode newSelection) {
        selectedNode = newSelection;
        propertyBox.getChildren().clear();

        if (selectedNode == null) {
            setUIEnabled(false);
            preview.setImage(null);
            propertyBox.getChildren().add(labelWrapper);
        } else {
            setUIEnabled(true);
            nodeLabel.setText(newSelection.getBaseName());
            for (NodeProperty<?> prop : selectedNode.properties)
                propertyBox.getChildren().add(prop.getGUIContent());
            // Update preivew image
            selectedNode.update();
            refreshPreview();
        }
    }

    private void setUIEnabled(boolean enabled) {
        preview.setVisible(enabled);
        preview.setManaged(enabled);
        nodeLabel.setVisible(enabled);
        nodeLabel.setManaged(enabled);
        deleteNodeButton.setVisible(enabled);
        deleteNodeButton.setManaged(enabled);
        deleteNodeButton.setDisable(!enabled);
    }

    /**
     * Refresh the preview image of the selected node.
     * If no node is selected, this does nothing
     */
    public void refreshPreview() {
        if (selectedNode == null) return;

        NodeSocket socket = selectedNode.getThumbnailSocket();
            if (socket != null)
                preview.setImage(socket.getImage());
    }
}