package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * The NodePropertyDropDown is a kind of NodeProperty that encapsulates
 * a string and uses a drop-down menu to manipulate. A list of the strings
 * available in the drop-down is provided to the constructor.
 */
public class NodePropertyDropDown extends NodeProperty<String> {

    // GUI Components
    private VBox                vbox;
    private ComboBox<String>    menu;
    private Label               label;

    /**
     * The name displayed above the drop-down menu
     */
    private String  name;
    /**
     * The options available in the drop-down menu
     */
    private String[] options;

    /**
     * Create a new NodePropertyDropDown with the given parent node, name and text options.
     */
    public NodePropertyDropDown(GraphNode parent, String name, String[] options) {
        super(parent);
        this.name = name;

        this.options = options;

        value = options[0];

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name);
        menu    = new ComboBox<String>();

        menu.getItems().addAll(options);
        menu.getSelectionModel().select(0);

        // Add label and combo box
        vbox.getChildren().add(label);
        vbox.getChildren().add(menu);

        menu.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            value = newVal;
            parentNode.onPropertyUpdate(this);
        });

        GUIContent = vbox;
    }

    /**
     * Set the selected value according to the given string.
     * If the given string is not in the options, this simply
     * selects the first option.
     */
    public void valueFromString(String str) {
        for (int i = 0; i < options.length; i++) {
            if (str.equals(options[i])) {
                menu.getSelectionModel().select(i);
                return;
            }
        }
        menu.getSelectionModel().select(0);
    }
}