package fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

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

    private Map<Circle, Map<Line, String>> lineMap = new HashMap<>();
    private Map<Circle, TextField> circleTextMap = new HashMap<>();
    private Map<Line, TextField> lineTextMap = new HashMap<>();


    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/redactorPage.fxml"));
        initialization(root);
        pane.setOnMouseClicked(this::paneClick);
        breg.setOnMouseClicked(this::bregButtonMouseClick);
        topology.setOnMouseClicked(this::topologyButtonMouseClick);
        primaryStage.setTitle("Редактор топологий");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root) {
        breg = (Button) root.lookup("#grid-button");
        topology = (Button) root.lookup("#buttons");
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
        if (lineMap.containsKey(circle)) {
            for (Map.Entry<Line, String> lineStringEntry : lineMap.get(circle).entrySet()) {
                if (lineStringEntry.getValue().equals(START)) {
                    lineStringEntry.getKey().setStartX(bregCoordinateX + event.getSceneX());
                    lineStringEntry.getKey().setStartY(bregCoordinateY + event.getSceneY());
                } else {
                    lineStringEntry.getKey().setEndX(bregCoordinateX + event.getSceneX());
                    lineStringEntry.getKey().setEndY(bregCoordinateY + event.getSceneY());
                }
                moveLineText(event, lineStringEntry.getKey());
            }
        }
    }

    private void moveLineText(MouseEvent event, Line line) {
        TextField textField = lineTextMap.get(line);
       /* if (textField.isVisible()) {
            textField.setVisible(false);
        }*/
        textField.setLayoutX((line.getEndX() + line.getStartX()) / 2 - TEXT_FIELD_WIDTH / 2
                + event.getSceneX());
        textField.setLayoutY((line.getEndY() + line.getStartY()) / 2 - TEXT_FIELD_HEIGHT / 2
                + event.getSceneY());
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
        }
    }

    private void topologyClick(MouseEvent event, Line line) {
        if (event.getButton().name().equals("SECONDARY")) {
            if (lineTextMap.get(line).isVisible()) {
                lineTextMap.get(line).setVisible(false);
            } else {
                lineTextMap.get(line).setVisible(true);
            }
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
        } else {
            line.setStartX(topologyStartX);
            line.setStartY(topologyStartY);
            line.setEndX(circle.getCenterX());
            line.setEndY(circle.getCenterY());
            line.setOnMouseClicked(e -> topologyClick(e, line));
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
        }
    }

    private void putToLineMap(Circle circle, Line line, String position) {
        if (lineMap.containsKey(circle)) {
            lineMap.get(circle).put(line, position);
        } else {
            Map<Line, String> lines = new HashMap<>();
            lines.put(line, position);
            lineMap.put(circle, lines);
        }
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
