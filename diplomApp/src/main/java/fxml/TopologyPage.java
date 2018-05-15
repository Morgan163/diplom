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
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.TopologyUtil;

import java.sql.SQLException;
import java.util.List;

public class TopologyPage extends Application {
    private Button redactor;
    private Button delete;
    private Button create;

    private ListView<TopologyUtil> topologyListView;
    private Pane pane;

    private DatabaseController databaseController = new DatabaseController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/topologyPage.fxml"));
        initialization(root);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Редактор топологий");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root){
        redactor = (Button)root.lookup(".redactor");
        delete = (Button) root.lookup(".delete");
        create = (Button) root.lookup("create");
        topologyListView = (ListView<TopologyUtil>) root.lookup("#list");
        fillTopologyList();
        pane = (Pane) root.lookup("#pane");
    }

    private void fillTopologyList(){
        try {
            List<TopologyUtil> topologyUtils = databaseController.getTopologies();
            ObservableList<TopologyUtil> observableList = FXCollections.observableArrayList(topologyUtils);
            topologyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            topologyListView.setItems(observableList);
            topologyListView.setEditable(false);
            topologyListView.setOnMouseClicked(e->selectItemListener());
        } catch (SQLException o_O) {
            System.out.println(o_O.getMessage());
        }
    }

    private void selectItemListener(){
        TopologyUtil topologyUtil = topologyListView.getSelectionModel().getSelectedItem();

    }
}
