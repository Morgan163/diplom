package fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import model.FiberEntity;
import model.RelatedSensorsEntity;
import model.SensorEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RedactorPage extends Application {
    private static final int BREG_RADIUS = 25;
    private static final double TEXT_FIELD_WIDTH = 60;
    private static final double TEXT_FIELD_HEIGHT = 25;
    private static final String START = "start";
    private static final String END = "end";
    private Button breg;
    private Button topology;
    private Button save;
    private MenuBar menuBar;
    private Rectangle source;
    private Pane pane;

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
        //save = (Button)root.lookup("#save");
        menuBar = (MenuBar) root.lookup("#menu-bar");
        source = (Rectangle) root.lookup("#source");
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
        if (topologyClick) {

        }
    }

    private void drawBreg(MouseEvent event) {
        Image image = new Image("/image/grid.jpg");
        Circle circle = new Circle(event.getSceneX(), event.getSceneY() - BREG_RADIUS, BREG_RADIUS,
                new ImagePattern(image));
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

    private void disableFocus(){
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
        } else {
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
        for (Circle circle : lineCircleMap.get(deleteLine)) {
            if ((!circle.equals(excludedCircle))&&(circleLinesHashMap.containsKey(circle))) {
                circleLinesHashMap.get(circle).remove(deleteLine);
            }
        }
        pane.getChildren().remove(lineTextMap.get(deleteLine));
        lineTextMap.remove(deleteLine);
        focusLine = null;
    }

    private void deleteCircle() {
        pane.getChildren().remove(focusCircle);
        pane.getChildren().remove(circleTextMap.get(focusCircle));
        for (Map.Entry<Line, String> lineStringEntry : circleLinesHashMap.get(focusCircle).entrySet()) {
            deleteLine(lineStringEntry.getKey(), focusCircle);
        }
        circleLinesHashMap.remove(focusCircle);
        focusCircle = null;
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
