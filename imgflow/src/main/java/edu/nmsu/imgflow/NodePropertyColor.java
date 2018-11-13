package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * The NodePropertySlider is a kind of NodeProperty that encapsulates
 * an integer and uses a slider to manipulate it.
 */
public class NodePropertyColor extends NodeProperty<Color> {

    // GUI Components
    private VBox        vbox;
    private ColorPicker picker;
    private Label       label;

    /**
     * The name displayed above the slider
     */
    private String  name;
    /**
     * The color that this property starts with
     */
    private Color defaultColor;

    /**
     * Create a new NodePropertySlider with the given parent node, min, max and name.
     */
    public NodePropertyColor(GraphNode parent, String name, Color defaultColor) {
        super(parent);
        this.name = name;
        this.defaultColor = defaultColor;
        value = defaultColor;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name);
        picker  = new ColorPicker();

        picker.setValue(defaultColor);

        // Add label and slider
        vbox.getChildren().add(label);
        vbox.getChildren().add(picker);

        // Add listener to slider to update value and change text on label
        picker.valueProperty().addListener((obs, oldVal, newVal) -> {
            value = newVal;
            parentNode.onPropertyUpdate(this);
        });

        GUIContent = vbox;
    }

    /**
     * Get the selected color as an RGBA string like
     * "rbga(255, 255, 255, 1.0)"
     */
    public String serializeValue() {
        int r = (int)(value.getRed()*255);
        int g = (int)(value.getGreen()*255);
        int b = (int)(value.getBlue()*255);
        return "rgba("+r+", "+g+", "+b+", "+value.getOpacity()+")";
    }

    /**
     * Set the color picker's value to the color
     * specified by the given string. If the string
     * is null or invalid, the picker will instead
     * switch to the default color value.
     */
    public void valueFromString(String str) {
        try {
            picker.setValue(Color.web(str));
        } catch (IllegalArgumentException e) {
            picker.setValue(defaultColor);
        } catch (NullPointerException e) {
            picker.setValue(defaultColor);
        }
    }
}