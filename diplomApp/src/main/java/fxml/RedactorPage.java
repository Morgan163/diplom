package fxml;

import DAO.FactoryDAO;
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
import javafx.stage.Stage;
import model.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;

import static fxml.DrawingUtil.START_X;
import static fxml.DrawingUtil.START_Y;


public class RedactorPage extends Application {
    private double currentX = START_X;
    private double currentY = START_Y;
    private DatabaseController databaseController = new DatabaseController();
    private DrawingUtil drawingUtil = new DrawingUtil();

    private static final int BREG_RADIUS = 25;
    private static final double TEXT_FIELD_WIDTH = 60;
    private static final double TEXT_FIELD_HEIGHT = 25;
    private static final String START = "start";
    private static final String END = "end";
    public static final String CREATE = "CREATE";
    public static final String REDACT = "REDACT";

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

    private TopologyUtil topologyUtil;
    private String command;
    private String goal;
    private Stage stage;

    public RedactorPage(String command, String goal) {
        this.command = command;
        this.goal = goal;
    }

    public RedactorPage(TopologyUtil topologyUtil, String command, String goal) {
        this.topologyUtil = topologyUtil;
        this.command = command;
        this.goal = goal;
    }

    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
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
        DrawingUtil dr = new DrawingUtil();
        dr.initMenu(root, stage);
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
        pane = (Pane) root.lookup("#pane");
        drawInit();
        if(command.equals(REDACT)){
            circleSensorEntityMap.put(source, topologyUtil.getTopologiesEntity().getSensorBySensor());
            drawTopology();
        }else{
            circleSensorEntityMap.put(source, new SensorEntity());
        }
    }

    private void drawTopology(){
        name.setText(topologyUtil.getTopologiesEntity().getName());
        List<RelatedSensorsEntity> relatedSensorsEntities = new ArrayList<>(
                topologyUtil.getRelatedSensorsEntitySet());
        //Collections.copy(relatedSensorsEntities, topologyUtil.getRelatedSensorsEntitySet());
        Queue<SensorEntity> sensorEntityQueue = new LinkedList<>();
        sensorEntityQueue.add(topologyUtil.getTopologiesEntity().getSensorBySensor());
        Queue<Circle> circles = new LinkedList<>();
        circles.offer(source);

        while (sensorEntityQueue.size() != 0) {
            Circle circle = circles.poll();
            Queue<RelatedSensorsEntity> forDraw = new LinkedList<>();
            SensorEntity sensorEntity = sensorEntityQueue.poll();
            drawingUtil.searchSensors(sensorEntityQueue, forDraw, relatedSensorsEntities, sensorEntity);
            while (forDraw.size() != 0) {
                Circle circle2;
                RelatedSensorsEntity relatedEntity = forDraw.poll();
                if(circleSensorEntityMap.containsValue(relatedEntity.getSensorBySensor2Id())){
                   circle2 = getCircleBySensor(relatedEntity.getSensorBySensor2Id());
                }else{
                    circle2 = drawingUtil.drawBreg(currentX, currentY, pane, circleTextMap, true,
                            relatedEntity.getSensorBySensor2Id().getWave().toString(), "");
                }
                usageCircle(circle2, relatedEntity.getSensorBySensor2Id());
                circles.offer(circle2);
                Long l = relatedEntity.getFiberByFiberId().getLength();
                String way =  l.toString();
                currentY += DrawingUtil.dy;
                line = drawingUtil.drawTopology(circle, true, pane, lineTextMap, true,
                       way, null);
                usageLineStart(circle, relatedEntity);
                drawingUtil.drawTopology(circle2, false, pane, lineTextMap, true,
                        relatedEntity.getFiberByFiberId().getLength().toString(), line);
                usageLineFinish(circle2, relatedEntity.getFiberByFiberId());
            }
            currentY = START_Y;
            currentX+=DrawingUtil.dx;
        }
        currentY = START_Y;
        currentX = START_X;
    }

    private Circle getCircleBySensor(SensorEntity sensorEntity){
        for(Map.Entry<Circle, SensorEntity> entry:circleSensorEntityMap.entrySet()){
            if(entry.getValue().equals(sensorEntity)){
                return entry.getKey();
            }
        }
        return null;
    }

    private void drawInit() {
        Image image = new Image("/image/light.jpg");
        source.setFill(new ImagePattern(image));
    }

    private void bregButtonMouseClick(MouseEvent event) {
        if (bregClick) {
            bregClick = false;
            breg.setId("grid-button");
        } else {
            bregClick = true;
            breg.setId("grid-button-click");
        }
    }

    private void topologyButtonMouseClick(MouseEvent event) {
        if (topologyClick) {
            topologyClick = false;
            topology.setId("topology");
            if (!topologyStart) {
                topologyStart = true;
            }
        } else {
            topologyClick = true;
            topology.setId("topology-click");
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
            TopologiesEntity topologiesEntity;
            if (topologyUtil == null) {
                topologiesEntity = new TopologiesEntity();
            }else{
                topologiesEntity = topologyUtil.getTopologiesEntity();
            }
            topologiesEntity.setSensorBySensor(circleSensorEntityMap.get(source));
            topologiesEntity.setName(name.getText());
            TopologyUtil topologyUtil = new TopologyUtil();
            topologyUtil.setTopologiesEntity(topologiesEntity);
            try {
                databaseController.saveTopology(topologyUtil);
            } catch (SQLException o_O) {
                System.out.println(o_O.getMessage());
            }
            TopologyPage topologyPage = new TopologyPage(goal);
            try {
                topologyPage.start(stage);
            } catch (Exception o_O) {
                System.out.println(o_O);
            }
        }

    }

    private boolean check() {
        for (Map.Entry<Circle, SensorEntity> entry : circleSensorEntityMap.entrySet()) {
            if (!entry.getKey().equals(source)) {
                String text = circleTextMap.get(entry.getKey()).getText();
                if ((!StringUtils.isBlank(text)) && (StringUtils.isNumeric(text))) {
                    entry.getValue().setWave(Long.parseLong(text, 10));
                } else {
                    showAlert("Ошибка при сохранении", "Топология не может содержать решетки " +
                            "без указания длины волны");
                    circleTextMap.get(entry.getKey()).setVisible(true);
                    return false;
                }
            } else {
                entry.getValue().setWave(1L);
            }
        }
        for (Map.Entry<Line, FiberEntity> entry : lineFiberEntityMap.entrySet()) {
            String text = lineTextMap.get(entry.getKey()).getText();
            if ((!StringUtils.isBlank(text)) && (StringUtils.isNumeric(text))) {
                entry.getValue().setLength(Long.parseLong(text, 10));
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
        Circle circle = drawingUtil.drawBreg(event.getSceneX(), event.getScreenY()- 3*BREG_RADIUS, pane,
                circleTextMap, false, "" , "");
        usageCircle(circle, new SensorEntity());
        /*bregClick = false;*/
    }

    private void usageCircle(Circle circle, SensorEntity sensorEntity){
        circleSensorEntityMap.put(circle, sensorEntity);
        circle.setOnMousePressed(e -> bregPressed(e, circle));
        circle.setOnMouseDragged(e -> bregDrag(e, circle));
        circle.setOnMouseClicked(e -> bregClick(e, circle));
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
            drawTopology(circle);
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

    private void drawTopology(Circle circle) {
        if (topologyStart) {
            line = drawingUtil.drawTopology(circle, topologyStart, pane, lineTextMap, false, "", null);
            usageLineStart(circle, new RelatedSensorsEntity());
            topologyStart = false;
        } else {
            line = drawingUtil.drawTopology(circle, topologyStart, pane, lineTextMap, false, "", line);
            usageLineFinish(circle, new FiberEntity());
            topologyStart = true;
           /* topologyClick = false;*/
            line = null;
        }
    }

    private void usageLineStart(Circle circle, RelatedSensorsEntity related){
        putToLineMap(circle, line, START);
        List<Circle> circles = new ArrayList<>();
        circles.add(circle);
        lineCircleMap.put(line, circles);
        relatedSensorsEntity = related;
        relatedSensorsEntity.setSensorBySensor1Id(circleSensorEntityMap.get(circle));
    }

    private void usageLineFinish(Circle circle, FiberEntity newFiber){
        relatedSensorsEntity.setSensorBySensor2Id(circleSensorEntityMap.get(circle));
        lineFiberEntityMap.put(line, newFiber);
        relatedSensorsEntity.setFiberByFiberId(lineFiberEntityMap.get(line));
        relatedSensorsEntities.add(relatedSensorsEntity);
        relatedSensorsEntity = null;
        lineCircleMap.get(line).add(circle);
        line.setOnMouseClicked(this::topologyClick);
        putToLineMap(circle, line, END);

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
      /*  try {
            FactoryDAO.getInstance().getFiberDAO().deleteFiber(lineFiberEntityMap.get(deleteLine));
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
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
       /* try {
            FactoryDAO.getInstance().getSensorDAO().deleteSensor(circleSensorEntityMap.get(focusCircle));
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
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
        List<RelatedSensorsEntity> forDelete = new ArrayList<>();
        for (RelatedSensorsEntity relatedSensorsEntity : relatedSensorsEntities) {
            if ((relatedSensorsEntity.getSensorBySensor1Id().equals(sensorEntity) ||
                    (relatedSensorsEntity.getSensorBySensor2Id().equals(relatedSensorsEntity)))) {
                forDelete.add(relatedSensorsEntity);
            }
        }
        for (RelatedSensorsEntity relatedSensorsEntity : forDelete) {
            relatedSensorsEntities.remove(relatedSensorsEntity);
           /* try {
                FactoryDAO.getInstance().getRelatedSensorsDAO().deleteRelatedSensors(relatedSensorsEntity);
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
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
