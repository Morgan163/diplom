package fxml;

import controller.ModellingController;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.math.BigDecimal;
import java.util.*;

public class ModelPage extends Application {

    private static final double LIGHT_SPEED = 3L;

    private ObservableList<ObservableList> data;
    private TableView waweWayTable;

    private TableView reservTable;

    private ModellingController modellingController;

    private List<Long> timesList;
    private Map<Long, List<String>> sensorEntityListMap;

    private Stage stage;

    public ModelPage(ModellingController modellingController) {
        this.modellingController = modellingController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/modellingPage.fxml"));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Моделирование");
        primaryStage.setScene(new Scene(root));
        initialization(root);
        primaryStage.show();
    }

    private void initialization(Parent root) {
        DrawingUtil.initMenu(root, stage);
        waweWayTable = (TableView) root.lookup(".table");
        reservTable = (TableView) root.lookup(".reserv");
        createDatas(modellingController.getSensorWayMap());
        fillWaweTable();
        fillReservTable();
    }

    private void fillWaweTable() {
        Map<SensorEntity, List<Long>> sensorWayMap = modellingController.getSensorWayMap();
        TableColumn times = new TableColumn("Время\\Длина волны");
        times.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        waweWayTable.getColumns().add(times);
        int i=1;
        for (Map.Entry<Long, List<String>> entry: sensorEntityListMap.entrySet()){
            final int j = i;
            TableColumn column = new TableColumn(String.valueOf(entry.getKey())+"нм");
            column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                    ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            waweWayTable.getColumns().add(column);
            i++;
        }
        data = FXCollections.observableArrayList();
        for(Long temp:timesList){
            ObservableList<String> row = FXCollections.observableArrayList();
            BigDecimal decimal = new BigDecimal((double)temp/LIGHT_SPEED);
            row.add(decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"*10^(-8)c");
            for (Map.Entry<Long, List<String>> entry: sensorEntityListMap.entrySet()){
                row.add(entry.getValue().get(timesList.indexOf(temp)));
            }
            data.add(row);
        }
        waweWayTable.setItems(data);
    }

    private void fillReservTable(){
        Map<SensorEntity, Integer> reserv = modellingController.getReserv();
        TableColumn sensor = new TableColumn("Датчик");
        sensor.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        reservTable.getColumns().add(sensor);
        TableColumn res = new TableColumn("Степень резервирования");
        res.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(1).toString()));
        reservTable.getColumns().add(res);
        ObservableList<ObservableList> tableData = FXCollections.observableArrayList();
        for (Map.Entry<SensorEntity, Integer> entry:reserv.entrySet()){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add("Датчик "+String.valueOf(entry.getKey().getId()));
            row.add(entry.getValue().toString());
            tableData.add(row);
        }
        reservTable.setItems(tableData);
    }

    private void createDatas(Map<SensorEntity, List<Long>> sensorWayMap) {
        Set<Long> times = new HashSet<>();
        sensorEntityListMap = new HashMap<>();
        for (Map.Entry<SensorEntity, List<Long>> entry : sensorWayMap.entrySet()) {
            times.addAll(entry.getValue());
        }
        timesList = new ArrayList<>(times);
        for (Map.Entry<SensorEntity, List<Long>> entry : sensorWayMap.entrySet()) {
            if (sensorEntityListMap.containsKey(entry.getKey().getWave())) {
                for (Long temp : entry.getValue()) {
                    List<String> list = sensorEntityListMap.get(entry.getKey().getWave());
                    list.set(timesList.indexOf(temp), list.get(timesList.indexOf(temp)) + "/д" + entry.getKey().getId());
                }
            } else {
                String [] strings = new String[timesList.size()];
                for(int i=0;i<strings.length;i++){
                    strings[i] = "";
                }
                for (Long temp : entry.getValue()) {
                    strings[timesList.indexOf(temp)] = "д"+String.valueOf(entry.getKey().getId());
                }
                sensorEntityListMap.put(entry.getKey().getWave(), Arrays.asList(strings));
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
