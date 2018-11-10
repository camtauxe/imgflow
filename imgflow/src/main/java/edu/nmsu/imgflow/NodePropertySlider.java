package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
// import javafx.beans.value.ChangeListener;

/**
 * The NodePropertySlider is a kind of NodeProperty that encapsulates
 * an integer and uses a slider to manipulate it.
 */
public class NodePropertySlider extends NodeProperty<Integer> {

    // GUI Components
    private VBox    vbox;
    private Slider  slider;
    private Label   label;

    /**
     * The name displayed above the slider
     */
    private String  name;
    /**
     * The minimum value the slider will allow
     */
    private int     sliderMin;
    /**
     * The maximum value the slider will allow
     */
    private int     sliderMax;
    /**
     * The default/starting value of the slider
     */
    private int     sliderDefault;

    /**
     * Create a new NodePropertySlider with the given parent node, min, max and name.
     */
    public NodePropertySlider(GraphNode parent, String name, int min, int max, int defaultValue) {
        super(parent);
        this.name = name;
        sliderMin = min;
        sliderMax = max;

        // Clamp default value to be within min and max
        if(sliderDefault <= sliderMax && sliderDefault >= sliderMin)
            sliderDefault = defaultValue;
        else if(sliderDefault < sliderMin)
            sliderDefault = sliderMin;
        else
            sliderDefault = sliderMax;

        value = sliderDefault;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name + ": " + sliderDefault);
        slider  = new Slider(sliderMin, sliderMax, sliderDefault);

        // Add label and slider
        vbox.getChildren().add(label);
        vbox.getChildren().add(slider);

        // Add listener to slider to update value and change text on label
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(name + ": " + newVal.intValue());
            value = newVal.intValue();
            parentNode.onPropertyUpdate(this);
        });

        GUIContent = vbox;
    }

    /**
     * Set the slider's value according to the given string.
     * The value is clamped between the slider's minimum and
     * maximum values.
     * If the string cannot be parsed into a number, the value
     * does not change.
     */
    public void valueFromString(String str) {
        try {
            int newVal = Integer.parseInt(str);
            if (newVal > sliderMax)
                newVal = sliderMax;
            else if (newVal < sliderMin)
                newVal = sliderMin;
            slider.setValue(newVal);
        } catch (NumberFormatException e) {}
    }
}