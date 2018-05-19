package fxml;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.FiberEntity;
import model.RelatedSensorsEntity;
import model.SensorEntity;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
                           boolean visible, String text, String id) {
        Image image = null;
        if(StringUtils.isBlank(id)) {
            image = new Image("/image/grid.jpg");
        }else{
            image = drawImage(id);
        }
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

    public static void initMenu(Parent root, Stage stage) {
        MenuBar menuBar = (MenuBar) root.lookup("#menu-bar");
        menuBar.getMenus().get(0).getItems().get(0).setOnAction(e -> {
            TopologyPage main = new TopologyPage(TopologyPage.MODEL);//Моделирование
            try {
                main.start(stage);
            } catch (Exception o_O) {
                System.out.println(o_O.getMessage());
            }
        });
        menuBar.getMenus().get(0).getItems().get(1).setOnAction(e -> {//Топологии
            TopologyPage main = new TopologyPage(TopologyPage.TOPOLOGY);
            try {
                main.start(stage);
            } catch (Exception o_O) {
                System.out.println(o_O.getMessage());
            }
        });
        menuBar.getMenus().get(1).getItems().get(0).setOnAction(e -> {
            try {
                Runtime.getRuntime().exec("hh.exe tutorial/tutorial.chm");
            } catch (IOException o_O) {
                System.out.println(o_O.getMessage());
            }
        });//Руководство
        menuBar.getMenus().get(1).getItems().get(1).setOnAction(e -> {//Справка о программе
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("О программе");
            alert.setHeaderText("Автоматизированная система моделирования волоконно-оптических датчиков" +
                    " со структурным резервированием. \n");
            alert.setContentText("Разработана студентом группы 6413 020302D Самарского университета " +
                    "Лукьяновым Андреем");

            alert.showAndWait();
        });
    }

    private Image drawImage(String text){
        try {
            BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/image/grid.jpg"));
            BufferedImage destinationImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = destinationImage.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, null);
            graphics.setColor(java.awt.Color.white);
            graphics.drawRect(bufferedImage.getWidth()/2-12,bufferedImage.getHeight()/2+6, 30, 30 );
            graphics.drawString(text, bufferedImage.getWidth()/2-12, bufferedImage.getHeight()/2+6);
            graphics.setColor(java.awt.Color.black);
            graphics.setFont(new Font("Serif", java.awt.Font.BOLD, 30));
            graphics.drawString(text, bufferedImage.getWidth()/2-15, bufferedImage.getHeight()/2+10);
            graphics.dispose();
            return SwingFXUtils.toFXImage(destinationImage, null );
        } catch (IOException o_O) {
            System.out.println(o_O.getMessage());
        }
        return null;
    }
}
