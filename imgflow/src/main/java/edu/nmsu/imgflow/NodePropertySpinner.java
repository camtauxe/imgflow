package edu.nmsu.imgflow;

import javafx.scene.layout.VBox;
import javafx.scene.control.Spinner;

import javafx.beans.value.ChangeListener;

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
     * The change listener used to respond to changes in the spinner.
     * This defined here because it will need to be re-assigned whenever
     * the spinner's value factory changes. For instance when calling updateSpinnerMax()
     */
    private ChangeListener<Integer> spinnerListener = (obs, oldVal, newVal) -> {
        // If new value is null (not a number) or out of range, do not update value
        // It is possible for the new value to be out of range because, when entering
        // an out of range value, this listener is still called once before it is changed
        if (newVal != null && newVal >= spinnerMin && newVal >= spinnerMax) {
            value = newVal.intValue();
            parentNode.onPropertyUpdate(this);
        }
    };

    /**
     * Create a new NodePropertySpinner with the given parent node, min, max and name.
     */
    public NodePropertySpinner(GraphNode parent, String name, int min, int max, int defaultValue) {
        super(parent);
        this.name = name;
        spinnerMin = min;
        spinnerMax = max;

        // Clamp default value to be within min and max
        if(defaultValue <= spinnerMax && defaultValue >= spinnerMin)
            spinnerInitialValue = defaultValue;
        else if(defaultValue < spinnerMin)
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
        
        //allows the spinner to be circular
        valueFactory.setWrapAround(true);

        // Add label and spinner
        vbox.getChildren().add(label);
        vbox.getChildren().add(spinner);

        // Add listener
        spinner.getValueFactory( ).valueProperty().addListener(spinnerListener);

        GUIContent = vbox;
    }

    //updates the maximum value of the passed in spinner to newMax by creating a new ValueFactory
    //and updating the spinners ValueFactory
    public void updateSpinnerMax(int newMax){
        IntegerSpinnerValueFactory newFactory = new IntegerSpinnerValueFactory(spinnerMin, newMax, getValue());
        
        //set wraparound and listener for new value factory
        newFactory.setWrapAround(true);
        newFactory.valueProperty().addListener(spinnerListener);

        spinner.setValueFactory(newFactory); 
    }
}