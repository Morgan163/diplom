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
import java.util.Queue;

public class DrawingUtil {
    public static double START_X = 230;
    public static double START_Y = 200;
    public static double dx = 180;
    public static double dy = 150;
    private static final int BREG_RADIUS = 25;
    private static final double TEXT_FIELD_WIDTH = 60;
    private static final double TEXT_FIELD_HEIGHT = 25;
    private Line line;

    public Circle drawBreg(double x, double y, Pane pane, Map<Circle, TextField> circleTextMap,
                            boolean visible, String text) {
        Image image = new Image("/image/grid.jpg");
        Circle circle = new Circle(x, y, BREG_RADIUS,
                new ImagePattern(image));
        TextField textField = new TextField(text);
        textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setLayoutX(circle.getCenterX() - TEXT_FIELD_WIDTH / 2);
        textField.setLayoutY(circle.getCenterY() - BREG_RADIUS * 2);
        textField.setVisible(visible);
        circleTextMap.put(circle, textField);

        pane.getChildren().add(circle);
        pane.getChildren().add(pane.getChildren().size() - 1, textField);
        return circle;
    }

    public Line drawTopology(Circle circle, boolean topologyStart, Pane pane, Map<Line, TextField> lineTextMap,
                              boolean visible, String text, Line oldLine) {
        if (topologyStart) {
            Line line = new Line();
            line.setStroke(Color.BLUE);
            line.setStrokeWidth(7);
            line.setStartX(circle.getCenterX());
            line.setStartY(circle.getCenterY());
           return line;
        } else {
            oldLine.setEndX(circle.getCenterX());
            oldLine.setEndY(circle.getCenterY());
            pane.getChildren().add(0, oldLine);
            TextField textField = new TextField(text);
            textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setLayoutX((oldLine.getEndX() + oldLine.getStartX()) / 2 - TEXT_FIELD_WIDTH / 2);
            textField.setLayoutY((oldLine.getEndY() + oldLine.getStartY()) / 2 - TEXT_FIELD_HEIGHT / 2);
            textField.setVisible(visible);
            lineTextMap.put(oldLine, textField);
            pane.getChildren().add(pane.getChildren().size() - 1, textField);
            return oldLine;
        }
    }

    public void searchSensors(Queue<SensorEntity> sensorEntityQueue, Queue<RelatedSensorsEntity> forDraw,
                                     List<RelatedSensorsEntity> relatedSensorsEntities, SensorEntity sensorEntity) {
        List<RelatedSensorsEntity> forRemove = new ArrayList<>();
        relatedSensorsEntities.stream().filter(o -> o.getSensorBySensor1Id().equals(sensorEntity)).forEach(
                o -> {
                    sensorEntityQueue.offer(o.getSensorBySensor2Id());
                    forDraw.offer(o);
                    forRemove.add(o);
                }
        );
        relatedSensorsEntities.removeAll(forRemove);
    }
}
