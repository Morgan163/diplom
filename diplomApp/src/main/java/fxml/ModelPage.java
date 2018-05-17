package fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.SensorEntity;

import javax.swing.text.TabableView;
import java.util.List;

public class ModelPage extends Application {

    private TabableView waweWayTable;
    private List<TableColumn<SensorEntity, Double>> waweColumns;

    private TabableView reservTable;
    private TableColumn<SensorEntity, Integer> reservColumn;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/modellingPage.fxml"));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Моделирование");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root) {
       waweWayTable = (TabableView) root.lookup(".table");
       reservTable = (TabableView) root.lookup(".reserv");

    }
}
