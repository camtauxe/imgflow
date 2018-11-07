package edu.nmsu.imgflow;

import javafx.scene.layout.Pane;

/**
 * A NodeProperty represents some parameter on the operation that a node performs.
 * 
 * Every property encapsulates some value (type T) and creates some kind of GUI construct
 * that allows for the value to be changed. NodeProperty by itself is an abstract class
 * so it must be extended in order to instantiate it and add it to a node.
 */
public abstract class NodeProperty<T> {

    /**
     * The underlying value for this property
     */
    protected T     value;
    /**
     * The GUI content that will be displayed in the property panel
     * for this property
     */
    protected Pane  GUIContent;
    /**
     * The GraphNode that this property belongs to
     */
    protected GraphNode parentNode;

    /**
     * Construct a new NodeProperty with the given parentNode
     */
    public NodeProperty(GraphNode parent) {
        GUIContent = new Pane();
        parentNode = parent;
    }

    /**
     * Get the current value of this property.
     */
    public T getValue() { return value; }

    /**
     * Get a string representation of this property's value,
     * used when saving the graph to a file. By default,
     * this just calls toString on the value, but other
     * properties may want to override it
     */
    public String serializeValue() { 
        if (value == null) return "null";
        return value.toString();
    }

    /**
     * Get the GUI content used to control this property's value
     * and to display in the property panel
     */
    public Pane getGUIContent() { return GUIContent; }
}