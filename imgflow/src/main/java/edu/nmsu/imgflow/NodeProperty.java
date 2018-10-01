package edu.nmsu.imgflow;

import javafx.scene.layout.Pane;

public class NodeProperty<T> {

    protected T     value;
    protected Pane  GUIContent;

    public NodeProperty() {
        GUIContent = new Pane();
    }

    public T getValue() { return value; }

    public Pane getGUIContent() { return GUIContent; }
}