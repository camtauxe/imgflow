package edu.nmsu.imgflow;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Abstract class for the creation of new GraphNodes
 */
public abstract class NodeFactory {

    /**
     * Create and return a new GraphNode of the given type.
     * Accepted strings for 'type' are:
     * 'filein','fileout','invert','coloreffects','opacity'
     * 
     * If an invalid string is given, null is returned
     */
    public static GraphNode createNode(String type) {
        switch (type) {
            case "filein":          return new GraphNodeFileIn();
            case "fileout":         return new GraphNodeFileOut();
            case "invert":          return new GraphNodeInvert();
            case "coloreffects":    return new GraphNodeColorEffects();
            case "opacity":         return new GraphNodeOpacity();
            case "splitcolor":      return new GraphNodeSplitColor();
            case "joincolor":       return new GraphNodeJoinColor();
            case "matte":           return new GraphNodeMatte();
            case "test":            return new GraphNodeTest();
            case "blur":            return new GraphNodeBlur();
            default: return null;
        }
    }

    /**
     * Create a JavaFX menu with MenuItems for creating new nodes
     * and adding them to the active graph.
     */
    public static Menu buildNodeCreationMenu() {
        Menu menu = new Menu("Create");

        menu.getItems().addAll(
            buildNodeMenuItem("filein",         "File IN"),
            buildNodeMenuItem("fileout",        "File OUT"),
            buildNodeMenuItem("invert",         "Invert Colors"),
            buildNodeMenuItem("coloreffects",   "Color Effects"),
            buildNodeMenuItem("opacity",        "Opacity"),
            buildNodeMenuItem("splitcolor",     "Split RGB"),
            buildNodeMenuItem("joincolor",      "Join RBG"),
            buildNodeMenuItem("matte",           "Matte"),
            buildNodeMenuItem("test",            "Test"),
            buildNodeMenuItem("blur",            "Blur")
        );

        return menu;
    }

    /**
     * Create a MenuItem that creates a node of the given type when
     * selected and adds it to the active graph.
     */
    private static MenuItem buildNodeMenuItem(String type, String name) {
        MenuItem item = new MenuItem(name);

        item.setOnAction((actionEvent) -> {
            GraphNode node = createNode(type);
            if (node != null)
                Main.getInstance().getActiveGraph().getNodes().add(node);
        });

        return item;
    }
}
