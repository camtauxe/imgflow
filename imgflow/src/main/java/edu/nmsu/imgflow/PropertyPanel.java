package edu.nmsu.imgflow;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

/**
 * A Panel in the GUI which displays properties for a node.
 * 
 * It consists of a VBox where the GUI Content for each of a node's properties
 * are displayed in a column. The node that this panel refers to can be changed
 * using the updateSelectedNode function. If no node is being referred to,
 * then a simple "No node selected" warning is displayed.
 */
public class PropertyPanel {

    /**
     * The VBox containing the node's properties' GUI
     */
    private VBox vbox;
    /**
     * The warning displayed when no node is selected
     */
    private Label noSelectionLabel;

    /**
     * The currently selected node. Null if no node is selected.
     */
    private GraphNode selectedNode;

    /**
     * Construct a new PropertyPanel with no selected node
     */
    public PropertyPanel() {
        vbox = new VBox(10.0);
        vbox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        noSelectionLabel = new Label("No Node selected...");
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
        vbox.getChildren().clear();

        if (selectedNode == null) {
            vbox.getChildren().add(noSelectionLabel);
        } else {
            for (NodeProperty<?> prop : selectedNode.properties)
                vbox.getChildren().add(prop.getGUIContent());
        }
    }
}