package it.unibo.samplejavafx.bounds;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static java.lang.Double.parseDouble;

public class BoundManagerFields {
    private final TextField stageWidth = new TextField();
    private final TextField stageHeight = new TextField();
    private final TextField sceneWidth = new TextField();
    private final TextField sceneHeight = new TextField();
    private final TextField rootWidth = new TextField();
    private final TextField rootHeight = new TextField();
    private final TextField nodeWidth = new TextField();
    private final TextField nodeHeight = new TextField();

    public void addElementsTo(final Pane node) {
        node.getChildren().addAll(
            new HBox(new Label("Stage width/height: "), stageWidth, stageHeight),
            new HBox(new Label("Scene width/height: "), sceneWidth, sceneHeight),
            new HBox(new Label("Root width/height: "), rootWidth, rootHeight),
            new HBox(new Label("Node width/height: "), nodeWidth, nodeHeight)
        );
    }

    public String getStageWidth() {
        return stageWidth.getText();
    }

    public String getStageHeight() {
        return stageHeight.getText();
    }

    public String getSceneWidth() {
        return sceneWidth.getText();
    }

    public String getSceneHeight() {
        return sceneHeight.getText();
    }

    public String getRootWidth() {
        return rootWidth.getText();
    }

    public String getRootHeight() {
        return rootHeight.getText();
    }

    public String getNodeWidth() {
        return nodeWidth.getText();
    }

    public String getNodeHeight() {
        return nodeHeight.getText();
    }
}
