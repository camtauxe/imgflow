package edu.nmsu.imgflow;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.geometry.Point2D;

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
            case "crop":            return new GraphNodeCrop();
            case "resize":          return new GraphNodeResize();
            case "composite":       return new GraphNodeComposite();
            case "chromakey":       return new GraphNodeChromaKey();
            case "rectangle":       return new GraphNodeRectangle();
            case "ellipse":         return new GraphNodeEllipse();
            case "flip":            return new GraphNodeFlip();
            case "rotate":          return new GraphNodeRotate();
            case "test":            return new GraphNodeTest();
            default: return null;
        }
    }

    /**
     * Get the basic type name from the base name of node.
     * If an invalid string is given, null is returned.
     */
    public static String typeFromBaseName(String baseName) {
        switch (baseName) {
            case "File IN":         return "filein";
            case "File OUT":        return "fileout";
            case "Invert":          return "invert";
            case "Color Effects":   return "coloreffects";
            case "Opacity":         return "opacity";
            case "Split RGB":       return "splitcolor";
            case "Join RGB":        return "joincolor";
            case "Matte":           return "matte";
            case "Crop":            return "crop";
            case "Resize":          return "resize";
            case "Composite":       return "composite";
            case "Chroma Key":      return "chromakey";
            case "Rectangle":       return "rectangle";
            case "Ellipse":         return "ellipse";
            case "Flip":            return "flip";
            case "Rotate":          return "rotate";
            case "Test Node":       return "test";
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
            buildNodeMenuItem("rectangle",      "Rectangle"),
            buildNodeMenuItem("ellipse",        "Ellipse"),
            buildNodeMenuItem("invert",         "Invert Colors"),
            buildNodeMenuItem("coloreffects",   "Color Effects"),
            buildNodeMenuItem("opacity",        "Opacity"),
            buildNodeMenuItem("chromakey",      "Chroma Key"),
            buildNodeMenuItem("splitcolor",     "Split RGB"),
            buildNodeMenuItem("joincolor",      "Join RBG"),
            buildNodeMenuItem("matte",          "Matte"),
            buildNodeMenuItem("composite",      "Composite"),
            buildNodeMenuItem("crop",           "Crop"),
            buildNodeMenuItem("resize",         "Resize"),
            buildNodeMenuItem("flip",           "Flip"),
            buildNodeMenuItem("rotate",         "Rotate")
        );

        return menu;
    }

    /**
     * Create a MenuItem that creates a node of the given type when
     * selected and adds it to the active graph. The position of the
     * new node will be in the center of the viewport
     */
    private static MenuItem buildNodeMenuItem(String type, String name) {
        MenuItem item = new MenuItem(name);

        item.setOnAction((actionEvent) -> {
            GraphNode node = createNode(type);
            if (node != null) {
                Point2D center = Main.getInstance().getViewport().getViewportCenter();
                node.setPosition(center);
                Main.getInstance().getActiveGraph().getNodes().add(node);
            }
        });

        return item;
    }
}