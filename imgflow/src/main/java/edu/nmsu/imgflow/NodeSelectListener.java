package edu.nmsu.imgflow;

/**
 * A listener to be registered with the Viewport so that other parts
 * of the application can respond to nodes in the Viewport being selected
 * or de-selected.
 */
public interface NodeSelectListener {
    /**
     * Respond to node selection or de-selection
     * @param node The node that was selected, or null
     * if a node was de-selected (meaning that there is no
     * currently selected node)
     */
    public void handle(GraphNode node);
}