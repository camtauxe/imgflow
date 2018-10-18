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
    private int     sliderDefaultValue;

    /**
     * Create a new NodePropertySlider with the given parent node, min, max and name.
     */
    public NodePropertySlider(GraphNode parent, String name, int min, int max, int defaultValue) {
        super(parent);
        this.name = name;
        sliderMin = min;
        sliderMax = max;
        if(sliderDefaultValue <= sliderMax && sliderDefaultValue >= sliderMin)
            sliderDefaultValue = defaultValue;
        else if(sliderDefaultValue < sliderMin)
            sliderDefaultValue = sliderMin;
        else
            sliderDefaultValue = sliderMax;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name + ": " + sliderDefaultValue);
        slider  = new Slider(sliderMin, sliderMax, sliderDefaultValue);

        // Add label and slider
        vbox.getChildren().add(label);
        vbox.getChildren().add(slider);

        // Add listener to slider to update value and change text on label
        slider.valueProperty().addListener((oldVal, newVal, obs) -> {
            label.setText(name + ": " + newVal.intValue());
            value = newVal.intValue();
        });

        GUIContent = vbox;
    }
}