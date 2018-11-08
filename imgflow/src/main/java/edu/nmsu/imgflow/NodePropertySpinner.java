package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory;
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
     * The last acceptable value. Used to revert to when an invalid value is entered.
     */
    private int     previousValue;

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
        previousValue = spinnerInitialValue;

        buildGUI();
    }

    /**
     * Build the property's GUI content
     */
    private void buildGUI() {
        // Instantiate components
        vbox    = new VBox(5.0);
        label   = new Label(name);
        //IntegerSpinnerValueFactory valueFactory = new IntegerSpinnerValueFactory(spinnerMin, spinnerMax, spinnerInitialValue);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory<Integer>(){
        
            @Override
            public void increment(int steps) {
                
            }
        
            @Override
            public void decrement(int steps) {
                
            }
        };
        spinner  = new Spinner<Integer>(valueFactory);

        // This causes a lot of problems if somone enters a non-number into the spinner and it's
        // very difficult to fix, so for now, we'll just disable it
        //spinner.setEditable(true);
        
        //allows the spinner to be circular
        valueFactory.setWrapAround(true);

        // Add label and spinner
        vbox.getChildren().add(label);
        vbox.getChildren().add(spinner);

        // Add listener to spinner to update value and change text on label
        spinner.getValueFactory().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                spinner.getValueFactory().setValue(previousValue);
            }
            else {
                parentNode.onPropertyUpdate(this);
                previousValue = value;
                value = newVal.intValue();
            }
        });

        GUIContent = vbox;
    }

    //updates the maximum value of the passed in spinner to newMax by creating a new ValueFactory
    //and updating the spinners ValueFactory
    public void updateSpinnerMax(int newMax){
        IntegerSpinnerValueFactory newFactory = new IntegerSpinnerValueFactory(spinnerMin, newMax, getValue());
        
        //must be set back to true for the new ValueFactory otherwise it won't wrap
        newFactory.setWrapAround(true);

        spinner.setValueFactory(newFactory); 
    }
}