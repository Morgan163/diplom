package fxml;

import controller.DatabaseController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RedactorPage extends Application {
    private DatabaseController databaseController = new DatabaseController();

    private static final int BREG_RADIUS = 25;
    private static final double TEXT_FIELD_WIDTH = 60;
    private static final double TEXT_FIELD_HEIGHT = 25;
    private static final String START = "start";
    private static final String END = "end";

    private Button breg;
    private Button topology;
    private Button save;

    private MenuBar menuBar;
    private Circle source;
    private Pane pane;
    private TextField name;

    private boolean bregClick = false;
    private boolean topologyClick = false;
    private boolean topologyStart = true;

    private double bregCoordinateX;
    private double bregCoordinateY;

    private double topologyStartX;
    private double topologyStartY;
    private Line line;


    private Map<Circle, Map<Line, String>> circleLinesHashMap = new HashMap<>();
    private Map<Circle, TextField> circleTextMap = new HashMap<>();
    private Map<Line, TextField> lineTextMap = new HashMap<>();
    private Map<Line, List<Double>> lineCoordinateMap = new HashMap<>();
    private Map<Line, List<Circle>> lineCircleMap = new HashMap<>();

    private Map<Circle, SensorEntity> circleSensorEntityMap = new HashMap<>();
    private Map<Line, FiberEntity> lineFiberEntityMap = new HashMap<>();
    private List<RelatedSensorsEntity> relatedSensorsEntities = new ArrayList<>();
    private RelatedSensorsEntity relatedSensorsEntity;

    private Circle focusCircle;
    private Line focusLine;


    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/redactorPage.fxml"));
        initialization(root);
        pane.setOnMouseClicked(this::paneClick);
        breg.setOnMouseClicked(this::bregButtonMouseClick);
        topology.setOnMouseClicked(this::topologyButtonMouseClick);
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, this::deleteButtonEvent);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Редактор топологий");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root) {
        breg = (Button) root.lookup("#grid-button");
        topology = (Button) root.lookup("#topology");
        save = (Button) root.lookup("#buttons");
        save.setOnMouseClicked(this::clickSaveButton);
        menuBar = (MenuBar) root.lookup("#menu-bar");
        name = (TextField) root.lookup("#name-field");
        source = (Circle) root.lookup("#source");
        source.setCenterX(69);
        source.setCenterY(416);
        source.setOnMouseClicked(e -> bregClick(e, source));
        circleSensorEntityMap.put(source, new SensorEntity());
        pane = (Pane) root.lookup("#pane");
        drawInit();
    }


    private void drawInit() {
        Image image = new Image("/image/light.jpg");
        source.setFill(new ImagePattern(image));
    }

    private void bregButtonMouseClick(MouseEvent event) {
        if (bregClick) {
            bregClick = false;
        } else {
            bregClick = true;
        }
    }

    private void topologyButtonMouseClick(MouseEvent event) {
        if (topologyClick) {
            topologyClick = false;
            if (!topologyStart) {
                topologyStart = true;
            }
        } else {
            topologyClick = true;
        }
    }

    private void paneClick(MouseEvent event) {
        if (bregClick) {
            drawBreg(event);
        }

    }

    private void clickSaveButton(MouseEvent event) {
        if (check()) {
            for (Map.Entry<Circle, SensorEntity> entry : circleSensorEntityMap.entrySet()) {
                try {
                    databaseController.saveSensor(entry.getValue());
                } catch (SQLException o_O) {
                    System.out.println(o_O.getMessage());
                }
            }
            for (Map.Entry<Line, FiberEntity> entry : lineFiberEntityMap.entrySet()) {
                try {
                    databaseController.saveFiber(entry.getValue());
                } catch (SQLException o_O) {
                    System.out.println(o_O.getMessage());
                }
            }
            for (RelatedSensorsEntity relatedSensorsEntity : relatedSensorsEntities) {
                try {
                    databaseController.saveRelatedSensors(relatedSensorsEntity);
                } catch (SQLException o_O) {
                    System.out.println(o_O.getMessage());
                }
            }
            TopologiesEntity topologiesEntity = new TopologiesEntity();
            topologiesEntity.setSensorBySensor(circleSensorEntityMap.get(source));
            topologiesEntity.setName(name.getText());
            TopologyUtil topologyUtil = new TopologyUtil();
            topologyUtil.setTopologiesEntity(topologiesEntity);
            try {
                databaseController.saveTopology(topologyUtil);
            } catch (SQLException o_O) {
                System.out.println(o_O.getMessage());
            }
        }

    }

    private boolean check() {
        for (Map.Entry<Circle, SensorEntity> entry : circleSensorEntityMap.entrySet()) {
            if (!entry.getKey().equals(source)) {
                String text = circleTextMap.get(entry.getKey()).getText();
                if ((!StringUtils.isBlank(text)) && (StringUtils.isNumeric(text))) {
                    entry.getValue().setWave(Long.getLong(text));
                } else {
                    showAlert("Ошибка при сохранении", "Топология не может содержать решетки " +
                            "без указания длины волны");
                    circleTextMap.get(entry.getKey()).setVisible(true);
                    return false;
                }
            }else{
                entry.getValue().setWave(1L);
            }
        }
        for (Map.Entry<Line, FiberEntity> entry : lineFiberEntityMap.entrySet()) {
            String text = lineTextMap.get(entry.getKey()).getText();
            if ((!StringUtils.isBlank(text)) && (StringUtils.isNumeric(text))) {
                entry.getValue().setLength(Long.getLong(text));
            } else {
                showAlert("Ошибка при сохранении", "Топология не может содержать оптоволоконные участки " +
                        "без указания длины");
                lineTextMap.get(entry.getKey()).setVisible(true);
                return false;
            }
        }
        if (StringUtils.isBlank(name.getText())) {
            showAlert("Ошибка при сохранении", "Имя топологии не может быть пустым");
            return false;
        }
        return true;
    }

    private void drawBreg(MouseEvent event) {
        Image image = new Image("/image/grid.jpg");
        Circle circle = new Circle(event.getSceneX(), event.getSceneY() - BREG_RADIUS, BREG_RADIUS,
                new ImagePattern(image));
        circleSensorEntityMap.put(circle, new SensorEntity());
        circle.setOnMousePressed(e -> bregPressed(e, circle));
        circle.setOnMouseDragged(e -> bregDrag(e, circle));
        circle.setOnMouseClicked(e -> bregClick(e, circle));

        TextField textField = new TextField();
        textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        textField.setLayoutX(circle.getCenterX() - TEXT_FIELD_WIDTH / 2);
        textField.setLayoutY(circle.getCenterY() - BREG_RADIUS * 2);
        textField.setVisible(false);
        circleTextMap.put(circle, textField);

        pane.getChildren().add(circle);
        pane.getChildren().add(pane.getChildren().size() - 1, textField);
        bregClick = false;
    }

    private void bregPressed(MouseEvent event, Circle circle) {
        bregCoordinateX = circle.getCenterX() - event.getSceneX();
        bregCoordinateY = circle.getCenterY() - event.getSceneY();
        if (circleLinesHashMap.containsKey(circle)) {
            for (Map.Entry<Line, String> lineStringEntry : circleLinesHashMap.get(circle).entrySet()) {
                List<Double> coordinates = new ArrayList<>();
                coordinates.add((lineStringEntry.getKey().getStartX() + lineStringEntry.getKey().getEndX()) / 2
                        - event.getSceneX() / 2);
                coordinates.add((lineStringEntry.getKey().getStartY() + lineStringEntry.getKey().getEndY()) / 2
                        - event.getSceneY() / 2);
                if (lineCoordinateMap.containsKey(lineStringEntry.getKey())) {
                    lineCoordinateMap.remove(lineStringEntry.getKey());
                }
                lineCoordinateMap.put(lineStringEntry.getKey(), coordinates);
            }
        }

    }

    private void bregDrag(MouseEvent event, Circle circle) {
        moveCircle(event, circle);
        moveCircleText(event, circle);
        moveLine(event, circle);
    }

    private void moveCircle(MouseEvent event, Circle circle) {
        circle.setCenterX(bregCoordinateX + event.getSceneX());
        circle.setCenterY(bregCoordinateY + event.getSceneY());
    }

    private void moveCircleText(MouseEvent event, Circle circle) {
        TextField textField = circleTextMap.get(circle);
        if (textField.isVisible()) {
            textField.setVisible(false);
        }
        textField.setLayoutX(bregCoordinateX - textField.getWidth() / 2 + event.getSceneX());
        textField.setLayoutY(bregCoordinateY - BREG_RADIUS * 2 + event.getSceneY());
    }

    private void moveLine(MouseEvent event, Circle circle) {
        if (circleLinesHashMap.containsKey(circle)) {
            for (Map.Entry<Line, String> lineStringEntry : circleLinesHashMap.get(circle).entrySet()) {
                moveLineText(event, lineStringEntry.getKey());
                if (lineStringEntry.getValue().equals(START)) {
                    lineStringEntry.getKey().setStartX(bregCoordinateX + event.getSceneX());
                    lineStringEntry.getKey().setStartY(bregCoordinateY + event.getSceneY());
                } else {
                    lineStringEntry.getKey().setEndX(bregCoordinateX + event.getSceneX());
                    lineStringEntry.getKey().setEndY(bregCoordinateY + event.getSceneY());
                }
            }
        }
    }

    private void moveLineText(MouseEvent event, Line line) {
        TextField textField = lineTextMap.get(line);
        textField.setLayoutX(lineCoordinateMap.get(line).get(0)
                - TEXT_FIELD_WIDTH / 2 + event.getSceneX() / 2);
        textField.setLayoutY(lineCoordinateMap.get(line).get(1)
                - TEXT_FIELD_HEIGHT / 2 + event.getSceneY() / 2);

    }

    private void bregClick(MouseEvent event, Circle circle) {
        if (topologyClick) {
            drawTopology(event, circle);
        }
        if (!circle.equals(source)) {
            if (event.getButton().name().equals("SECONDARY")) {

                if (circleTextMap.get(circle).isVisible()) {
                    circleTextMap.get(circle).setVisible(false);
                } else {
                    circleTextMap.get(circle).setVisible(true);
                }
            } else {
                disableFocus();
                focusCircle = circle;
                Image image = new Image("/image/gridClick.jpg");
                focusCircle.setFill(new ImagePattern(image));
            }
        }
    }


    private void topologyClick(MouseEvent event) {
        Line line = (Line) event.getPickResult().getIntersectedNode();
        if (event.getButton().name().equals("SECONDARY")) {
            if (lineTextMap.get(line).isVisible()) {
                lineTextMap.get(line).setVisible(false);
            } else {
                lineTextMap.get(line).setVisible(true);
            }
        } else {
            disableFocus();
            focusLine = line;
            focusLine.setStroke(Color.RED);
        }
    }

    private void disableFocus() {
        if (focusCircle != null) {
            Image image = new Image("/image/grid.jpg");
            focusCircle.setFill(new ImagePattern(image));
            focusCircle = null;
        }
        if (focusLine != null) {
            focusLine.setStroke(Color.BLUE);
            focusLine = null;
        }
    }

    private void drawTopology(MouseEvent event, Circle circle) {
        if (topologyStart) {
            topologyStartX = circle.getCenterX();
            topologyStartY = circle.getCenterY();
            topologyStart = false;
            line = new Line();
            line.setStroke(Color.BLUE);
            line.setStrokeWidth(7);
            putToLineMap(circle, line, START);
            List<Circle> circles = new ArrayList<>();
            circles.add(circle);
            lineCircleMap.put(line, circles);
            relatedSensorsEntity = new RelatedSensorsEntity();
            relatedSensorsEntity.setSensorBySensor1Id(circleSensorEntityMap.get(circle));
        } else {
            relatedSensorsEntity.setSensorBySensor2Id(circleSensorEntityMap.get(circle));
            lineFiberEntityMap.put(line, new FiberEntity());
            relatedSensorsEntity.setFiberByFiberId(lineFiberEntityMap.get(line));
            relatedSensorsEntities.add(relatedSensorsEntity);
            relatedSensorsEntity = null;

            lineCircleMap.get(line).add(circle);
            line.setStartX(topologyStartX);
            line.setStartY(topologyStartY);
            line.setEndX(circle.getCenterX());
            line.setEndY(circle.getCenterY());
            line.setOnMouseClicked(this::topologyClick);
            putToLineMap(circle, line, END);
            topologyStart = true;
            pane.getChildren().add(0, line);
            topologyClick = false;

            TextField textField = new TextField();
            textField.setMinSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setMaxSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
            textField.setLayoutX((line.getEndX() + line.getStartX()) / 2 - TEXT_FIELD_WIDTH / 2);
            textField.setLayoutY((line.getEndY() + line.getStartY()) / 2 - TEXT_FIELD_HEIGHT / 2);
            textField.setVisible(false);
            lineTextMap.put(line, textField);
            pane.getChildren().add(pane.getChildren().size() - 1, textField);
            line = null;
        }
    }

    private void putToLineMap(Circle circle, Line line, String position) {
        if (circleLinesHashMap.containsKey(circle)) {
            circleLinesHashMap.get(circle).put(line, position);
        } else {
            Map<Line, String> lines = new HashMap<>();
            lines.put(line, position);
            circleLinesHashMap.put(circle, lines);
        }
    }

    private void deleteButtonEvent(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DELETE)) {
            if (focusLine != null) {
                deleteLine(focusLine, null);
            }
            if (focusCircle != null) {
                deleteCircle();
            }
        }
    }

    private void deleteLine(Line deleteLine, Circle excludedCircle) {
        pane.getChildren().remove(deleteLine);
        deleteRelatedSensor(lineFiberEntityMap.get(deleteLine));
        lineFiberEntityMap.remove(deleteLine);
        for (Circle circle : lineCircleMap.get(deleteLine)) {
            if ((!circle.equals(excludedCircle)) && (circleLinesHashMap.containsKey(circle))) {
                circleLinesHashMap.get(circle).remove(deleteLine);
            }
        }
        pane.getChildren().remove(lineTextMap.get(deleteLine));
        lineTextMap.remove(deleteLine);
        focusLine = null;
    }

    private void deleteCircle() {
        deleteRelatedSensor(circleSensorEntityMap.get(focusCircle));
        circleSensorEntityMap.remove(focusCircle);
        pane.getChildren().remove(focusCircle);
        pane.getChildren().remove(circleTextMap.get(focusCircle));
        for (Map.Entry<Line, String> lineStringEntry : circleLinesHashMap.get(focusCircle).entrySet()) {
            deleteLine(lineStringEntry.getKey(), focusCircle);
        }
        circleLinesHashMap.remove(focusCircle);
        focusCircle = null;
    }

    private void deleteRelatedSensor(SensorEntity sensorEntity) {
        RelatedSensorsEntity forDelete = null;
        for (RelatedSensorsEntity relatedSensorsEntity : relatedSensorsEntities) {
            if ((relatedSensorsEntity.getSensorBySensor1Id().equals(sensorEntity) ||
                    (relatedSensorsEntity.getSensorBySensor2Id().equals(relatedSensorsEntity)))) {
                forDelete = relatedSensorsEntity;
            }
        }
        if (relatedSensorsEntity != null) {
            relatedSensorsEntities.remove(forDelete);
        }
    }

    private void deleteRelatedSensor(FiberEntity fiberEntity) {
        RelatedSensorsEntity forDelete = null;
        for (RelatedSensorsEntity relatedSensorsEntity : relatedSensorsEntities) {
            if (relatedSensorsEntity.getFiberByFiberId().equals(fiberEntity)) {
                forDelete = relatedSensorsEntity;
            }
        }
        if (relatedSensorsEntity != null) {
            relatedSensorsEntities.remove(forDelete);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Ошибка");
        alert.setContentText(message);

        alert.showAndWait();
    }

    public Button getBreg() {
        return breg;
    }

    public void setBreg(Button breg) {
        this.breg = breg;
    }

    public Button getTopology() {
        return topology;
    }

    public void setTopology(Button topology) {
        this.topology = topology;
    }


    public Button getSave() {
        return save;
    }

    public void setSave(Button save) {
        this.save = save;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }
}
