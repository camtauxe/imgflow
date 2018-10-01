package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
// import javafx.beans.value.ChangeListener;

public class NodePropertySlider extends NodeProperty<Integer> {

    private VBox    vbox;
    private Slider  slider;
    private Label   label;

    private String  name;
    private int     sliderMin;
    private int     sliderMax;

    public NodePropertySlider(String name, int min, int max) {
        super();
        this.name = name;
        sliderMin = min;
        sliderMax = max;

        buildGUI();
    }

    private void buildGUI() {
        vbox    = new VBox(5.0);
        label   = new Label(name + ": " + sliderMin);
        slider  = new Slider(sliderMin, sliderMax, sliderMin);

        vbox.getChildren().add(label);
        vbox.getChildren().add(slider);

        slider.valueProperty().addListener((oldVal, newVal, obs) -> {
            label.setText(name + ": " + newVal.intValue());
            value = newVal.intValue();
        });

        GUIContent = vbox;
    }
}