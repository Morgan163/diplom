package fxml;

import controller.DatabaseController;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.RelatedSensorsEntity;
import model.SensorEntity;
import model.TopologyUtil;
import org.hibernate.mapping.Collection;

import java.sql.SQLException;
import java.util.*;

public class TopologyPage extends Application {
    private double currentX = DrawingUtil.START_X;
    private double currentY = DrawingUtil.START_Y;

    private Button redactor;
    private Button delete;
    private Button create;

    private ListView<TopologyUtil> topologyListView;
    private Pane pane;
    private Circle source;

    private DatabaseController databaseController = new DatabaseController();
    private DrawingUtil drawingUtil = new DrawingUtil();

    private Map<Circle, TextField> circleTextMap = new HashMap<>();
    private Map<Line, TextField> lineTextMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/topologyPage.fxml"));
        initialization(root);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Редактор топологий");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root) {
        redactor = (Button) root.lookup(".redactor");
        delete = (Button) root.lookup(".delete");
        create = (Button) root.lookup("create");
        topologyListView = (ListView<TopologyUtil>) root.lookup("#list");
        fillTopologyList();
        source = (Circle) root.lookup("#source");
        source.setCenterX(69);
        source.setCenterY(416);
        Image image = new Image("/image/light.jpg");
        source.setFill(new ImagePattern(image));
        pane = (Pane) root.lookup("#pane");
    }

    private void fillTopologyList() {
        try {
            List<TopologyUtil> topologyUtils = databaseController.getTopologies();
            ObservableList<TopologyUtil> observableList = FXCollections.observableArrayList(topologyUtils);
            topologyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            topologyListView.setItems(observableList);
            topologyListView.setEditable(false);
            topologyListView.setOnMouseClicked(e -> selectItemListener());
        } catch (SQLException o_O) {
            System.out.println(o_O.getMessage());
        }
    }

    private void selectItemListener() {
        pane.getChildren().remove(0,pane.getChildren().size());
        pane.getChildren().add(source);
        TopologyUtil topologyUtil = topologyListView.getSelectionModel().getSelectedItem();
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
                RelatedSensorsEntity relatedSensorsEntity = forDraw.poll();
                Circle circle2 = drawingUtil.drawBreg(currentX, currentY, pane, circleTextMap, true,
                        relatedSensorsEntity.getSensorBySensor2Id().getWave().toString());
                circles.offer(circle2);
                currentY += DrawingUtil.dy;
                Line line = drawingUtil.drawTopology(circle, true, pane, lineTextMap, true,
                        relatedSensorsEntity.getFiberByFiberId().getLength().toString(), null);
                drawingUtil.drawTopology(circle2, false, pane, lineTextMap, true,
                        relatedSensorsEntity.getFiberByFiberId().getLength().toString(), line);
            }
            currentY = DrawingUtil.START_Y;
            currentX+=DrawingUtil.dx;

        }
        currentY = DrawingUtil.START_Y;
        currentX = DrawingUtil.START_Y;


    }


}
