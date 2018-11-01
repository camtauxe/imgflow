package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
// import javafx.beans.value.ChangeListener;

/**
 * The NodePropertySpinner is a kind of NodeProperty that encapsulates
 * an integer and uses a spinner to manipulate it.
 */
public class NodePropertySpinner extends NodeProperty<Integer> {

    // GUI Components
    private VBox    vbox;
    private Spinner<Integer>  spinner;
    private Label   label;

    /**
     * The name displayed above the spinner
     */
    private String  name;
    /**
     * The minimum value the spinner will allow
     */
    private int     spinnerMin;
    /**
     * The maximum value the spinner will allow
     */
    private int     spinnerMax;
    /**
     * The default/starting value of the spinner
     */
    private int     spinnerInitialValue;

    /**
     * Create a new NodePropertySpinner with the given parent node, min, max and name.
     */
    public NodePropertySpinner(GraphNode parent, String name, int min, int max, int defaultValue) {
        super(parent);
        this.name = name;
        spinnerMin = min;
        spinnerMax = max;

        // Clamp default value to be within min and max
        if(spinnerInitialValue <= spinnerMax && spinnerInitialValue >= spinnerMin)
            spinnerInitialValue = defaultValue;
        else if(spinnerInitialValue < spinnerMin)
            spinnerInitialValue = spinnerMin;
        else
            spinnerInitialValue = spinnerMax;

        value = spinnerInitialValue;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name);
        IntegerSpinnerValueFactory valueFactory = new IntegerSpinnerValueFactory(spinnerMin, spinnerMax, spinnerInitialValue);
        spinner  = new Spinner<Integer>(valueFactory);

        spinner.setEditable(true);

        // Add label and spinner
        vbox.getChildren().add(label);
        vbox.getChildren().add(spinner);

        // Add listener to spinner to update value and change text on label
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            // If a bad value is entered into the textbox, default to the 
            // previous value 
            try {
                if (newVal == null) {
                    spinner.getValueFactory().setValue(oldVal);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            parentNode.onPropertyUpdate(this);
        });

        GUIContent = vbox;
    }
}