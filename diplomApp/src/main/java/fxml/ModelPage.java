package fxml;

import controller.ModellingController;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.SensorEntity;

import javax.swing.text.TabableView;
import java.util.*;

public class ModelPage extends Application {

    private static final Long LIGHT_SPEED = 30000000000L;

    private ObservableList<ObservableList> data;
    private TableView waweWayTable;

    private TableView reservTable;
    private List<TableColumn<SensorEntity, Integer>> reservColumn;

    private ModellingController modellingController;

    private List<Long> timesList;
    private Map<Long, List<String>> sensorEntityListMap;

    public ModelPage(ModellingController modellingController) {
        this.modellingController = modellingController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/modellingPage.fxml"));
        initialization(root);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Моделирование");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root) {
        waweWayTable = (TableView) root.lookup(".table");
        reservTable = (TableView) root.lookup(".reserv");
    }

    private void fillWaweTable() {
        Map<SensorEntity, List<Long>> sensorWayMap = modellingController.getSensorWayMap();
        TableColumn times = new TableColumn("Время\\Длина волны");
        times.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        waweWayTable.getColumns().add(times);
        int i=1;
        for (Map.Entry<Long, List<String>> entry: sensorEntityListMap.entrySet()){
            TableColumn column = new TableColumn(String.valueOf(entry.getKey())+"нм");
            column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                    ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(i).toString()));
            waweWayTable.getColumns().add(column);
        }


    }

    private void createDatas(Map<SensorEntity, List<Long>> sensorWayMap) {
        Set<Long> times = new HashSet<>();
        sensorEntityListMap = new HashMap<>();
        for (Map.Entry<SensorEntity, List<Long>> entry : sensorWayMap.entrySet()) {
            for (Long longEntry : entry.getValue()) {
                times.add(longEntry);
            }
        }
        timesList = new ArrayList<>(times);
        for (Map.Entry<SensorEntity, List<Long>> entry : sensorWayMap.entrySet()) {
            if (sensorEntityListMap.containsKey(entry.getKey().getWave())) {
                for (Long temp : entry.getValue()) {
                    List<String> list = sensorEntityListMap.get(entry.getKey().getWave());
                    list.set(timesList.indexOf(temp), list.get(timesList.indexOf(temp)) + "/" + entry.getKey().getId());
                }
            } else {
                List<String> strings = new ArrayList<>(timesList.size());
                for (Long temp : entry.getValue()) {
                    strings.add(timesList.indexOf(temp), String.valueOf(entry.getKey().getId()));
                }
                sensorEntityListMap.put(entry.getKey().getWave(), strings);
            }
        }
    }

    private Long getLongByWawe(Map<Long, List<String>> sensorEntityListMap, Long wave) {
        List<Long> longs = new ArrayList<>();
        sensorEntityListMap.entrySet().stream().filter(o -> (o.getKey().equals(wave))).forEach(
                o -> longs.add(o.getKey())
        );
        return longs.get(0);
    }
}
