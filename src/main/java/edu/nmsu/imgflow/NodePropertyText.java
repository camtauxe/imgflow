package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

/**
 * The NodePropertyText is a kind of NodeProperty that encapsulates
 * an string and uses a text field to manipulate it.
 */
public class NodePropertyText extends NodeProperty<String> {

    // GUI Components
    private VBox        vbox;
    private TextField   field;
    private Label       label;

    /**
     * The name displayed above the text field
     */
    private String  name;

    /**
     * Create a new NodePropertyText with the given parent node, name and default text
     */
    public NodePropertyText(GraphNode parent, String name, String defaultText) {
        super(parent);
        this.name = name;

        value = defaultText;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name);
        field   = new TextField(value);

        // Add label and text field
        vbox.getChildren().add(label);
        vbox.getChildren().add(field);

        // Add listener to update value and alert parent node
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            value = newVal;
            parentNode.onPropertyUpdate(this);
        });

        GUIContent = vbox;
    }

    /**
     * Set the value of this property according to the given string
     */
    public void valueFromString(String str) {
        field.setText(str);
    }
}