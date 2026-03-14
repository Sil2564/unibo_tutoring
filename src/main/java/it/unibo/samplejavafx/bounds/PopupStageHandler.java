package it.unibo.samplejavafx.bounds;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PopupStageHandler implements EventHandler<ActionEvent> {
    
    private static final double BORDER_WIDTH = 5.0;
    private static final Color BORDER_COLOR = Color.RED;
    private static final Color SCENE_BACKGROUND_COLOR = Color.BEIGE;
    
    private final BoundManagerFields fields;

    public PopupStageHandler(final BoundManagerFields fields) {
        this.fields = fields;
    }

    @Override
    public void handle(final ActionEvent actionEvent) {
        final Stage stage = createStage();
        final VBox rootNode = createRootNode();
        final Label statusLabel = createStatusLabel(stage);
        final Label infoLabel = createInfoLabel();
        final TextField textField = createTextField();
        final Label label2 = new Label("Label 2");
        rootNode.getChildren().addAll(statusLabel, label2, infoLabel, textField);
        final Scene scene = createScene(rootNode, infoLabel);
        bindStatusLabel(statusLabel, stage, scene);
        scene.setFill(SCENE_BACKGROUND_COLOR);
        stage.setScene(scene);
        stage.show();
    }

    private Stage createStage() {
        final Stage stage = new Stage();
        configureStageSize(stage);
        return stage;
    }

    private void configureStageSize(final Stage stage) {
        final StringBuilder titleBuilder = new StringBuilder();
        if (!fields.getStageWidth().isBlank()) {
            final double width = Double.parseDouble(fields.getStageWidth());
            titleBuilder.append("W=").append(fields.getStageWidth());
            stage.setWidth(width);
        }
        if (!fields.getStageHeight().isBlank()) {
            if (!titleBuilder.isEmpty()) {
                titleBuilder.append("; ");
            }
            titleBuilder.append("H=").append(fields.getStageHeight());
            stage.setHeight(Double.parseDouble(fields.getStageHeight()));
        }
        if (!titleBuilder.isEmpty()) {
            stage.setTitle(titleBuilder.toString());
        }
    }

    private VBox createRootNode() {
        final VBox rootNode = new VBox();
        configureRootNodeBorder(rootNode);
        configureRootNodeSize(rootNode);
        return rootNode;
    }

    private void configureRootNodeBorder(final VBox rootNode) {
        final BorderStroke borderStroke = new BorderStroke(
            BORDER_COLOR,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(BORDER_WIDTH)
        );
        rootNode.setBorder(new Border(borderStroke));
    }

    private void configureRootNodeSize(final VBox rootNode) {
        if (!fields.getRootWidth().isBlank()) {
            rootNode.setPrefWidth(Double.parseDouble(fields.getRootWidth()));
        }
        if (!fields.getRootHeight().isBlank()) {
            rootNode.setPrefHeight(Double.parseDouble(fields.getRootHeight()));
        }
    }

    private Label createStatusLabel(final Stage stage) {
        return new Label();
    }

    private Label createInfoLabel() {
        final Label label = new Label("Label 1");
        final StringBuilder textBuilder = new StringBuilder();
        if (!fields.getRootWidth().isBlank()) {
            textBuilder.append("VBox Pref W: ").append(fields.getRootWidth()).append("; ");
        }
        if (!fields.getRootHeight().isBlank()) {
            textBuilder.append("VBox Pref H: ").append(fields.getRootHeight()).append(".");
        }
        if (!textBuilder.isEmpty()) {
            label.setText(textBuilder.toString());
        }
        return label;
    }

    private TextField createTextField() {
        final TextField textField = new TextField();
        configureTextFieldSize(textField);
        return textField;
    }

    private void configureTextFieldSize(final TextField textField) {
        final StringBuilder textBuilder = new StringBuilder();
        if (!fields.getNodeWidth().isBlank()) {
            textBuilder.append("Pref W: ").append(fields.getNodeWidth()).append("; ");
            textField.setPrefWidth(Double.parseDouble(fields.getNodeWidth()));
        }
        if (!fields.getNodeHeight().isBlank()) {
            textBuilder.append("Pref H: ").append(fields.getNodeHeight()).append(".");
            textField.setPrefHeight(Double.parseDouble(fields.getNodeHeight()));
        }
        if (!textBuilder.isEmpty()) {
            textField.appendText(textBuilder.toString());
        }
    }

    private Scene createScene(final VBox rootNode, final Label infoLabel) {
        if (hasSceneDimensions()) {
            infoLabel.setText("Scene W/H: " + fields.getSceneWidth() + ", " + fields.getSceneHeight());
            return new Scene(
                rootNode,
                Double.parseDouble(fields.getSceneWidth()),
                Double.parseDouble(fields.getSceneHeight())
            );
        }
        return new Scene(rootNode);
    }

    private boolean hasSceneDimensions() {
        return !fields.getSceneWidth().isBlank() && !fields.getSceneHeight().isBlank();
    }

    private void bindStatusLabel(final Label statusLabel, final Stage stage, final Scene scene) {
        statusLabel.textProperty().bind(
            StringBinding.stringExpression(
                new SimpleStringProperty("Stage X=")
                    .concat(stage.xProperty())
                    .concat(", Y=")
                    .concat(stage.yProperty())
                    .concat(" W=")
                    .concat(stage.widthProperty())
                    .concat(" H=")
                    .concat(stage.heightProperty())
                    .concat("\nScene W=")
                    .concat(scene.widthProperty())
                    .concat(" H=")
                    .concat(scene.heightProperty())
            )
        );
    }
}
