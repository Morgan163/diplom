package fxml;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.FiberEntity;
import model.RelatedSensorsEntity;
import model.SensorEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawingUtil {
    private static final int BREG_RADIUS = 25;
    private static final double TEXT_FIELD_WIDTH = 60;
    private static final double TEXT_FIELD_HEIGHT = 25;
    private Line line;

    private Circle drawBreg(double x, double y, Pane pane, Map<Circle, TextField> circleTextMap) {
        Image image = new Image("/image/grid.jpg");
        Circle circle = new Circle(x, y - BREG_RADIUS, BREG_RADIUS,
                new ImagePattern(image));
        TextField textField = new TextField();
        textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setLayoutX(circle.getCenterX() - TEXT_FIELD_WIDTH / 2);
        textField.setLayoutY(circle.getCenterY() - BREG_RADIUS * 2);
        textField.setVisible(false);
        circleTextMap.put(circle, textField);

        pane.getChildren().add(circle);
        pane.getChildren().add(pane.getChildren().size() - 1, textField);
        return circle;
    }

    private Line drawTopology(Circle circle, boolean topologyStart, Pane pane, Map<Line, TextField> lineTextMap) {
        if (topologyStart) {
            line = new Line();
            line.setStroke(Color.BLUE);
            line.setStrokeWidth(7);
            line.setStartX(circle.getCenterX());
            line.setStartY(circle.getCenterY());
           return line;
        } else {
            line.setEndX(circle.getCenterX());
            line.setEndY(circle.getCenterY());
            pane.getChildren().add(0, line);
            TextField textField = new TextField();
            textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setLayoutX((line.getEndX() + line.getStartX()) / 2 - TEXT_FIELD_WIDTH / 2);
            textField.setLayoutY((line.getEndY() + line.getStartY()) / 2 - TEXT_FIELD_HEIGHT / 2);
            textField.setVisible(false);
            lineTextMap.put(line, textField);
            pane.getChildren().add(pane.getChildren().size() - 1, textField);
            return line;
        }
    }
}
