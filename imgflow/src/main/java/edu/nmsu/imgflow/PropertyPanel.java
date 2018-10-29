package edu.nmsu.imgflow;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

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
     * The VBox containing the node's properties' GUI
     */
    private VBox propertyBox;
    /**
     * The warning displayed when no node is selected
     */
    private Label noSelectionLabel;
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
        vbox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));

        noSelectionLabel = new Label("No Node selected...");

        propertyBox = new VBox(10.0);
        propertyBox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        VBox.setVgrow(propertyBox, Priority.ALWAYS);
        vbox.getChildren().add(propertyBox);

        deleteNodeButton = new Button("Delete");
        deleteNodeButton.setOnAction((actionEvent) -> {
            if (selectedNode == null) return;
            selectedNode.disconnectAllSockets();
            Main.getInstance().getActiveGraph().getNodes().remove(selectedNode);
            updateSelectedNode(null);
        });
        vbox.getChildren().add(deleteNodeButton);

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
            propertyBox.getChildren().add(noSelectionLabel);
            deleteNodeButton.setDisable(true);
        } else {
            deleteNodeButton.setDisable(false);
            for (NodeProperty<?> prop : selectedNode.properties)
                propertyBox.getChildren().add(prop.getGUIContent());
        }
    }
}