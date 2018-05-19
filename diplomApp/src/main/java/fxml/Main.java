package fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;



public class Main extends Application {
    private Stage stage;
    private Button topology;
    private Button model;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/startPage.fxml"));
        initialization(root);
        stage = primaryStage;
        primaryStage.setMaximized(true);
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initialization(Parent root){
        DrawingUtil.initMenu(root, stage);
        model = (Button) root.lookup(".model");
        model.setOnMouseClicked(e->{
            TopologyPage topologyPage = new TopologyPage(TopologyPage.MODEL);
            try {
                topologyPage.start(stage);
            } catch (Exception o_O) {
                System.out.println(o_O.getMessage());
            }
        });
        topology = (Button)root.lookup(".topology");
        topology.setOnMouseClicked(e->{
            TopologyPage topologyPage = new TopologyPage(TopologyPage.TOPOLOGY);
            try {
                topologyPage.start(stage);
            } catch (Exception o_O) {
                System.out.println(o_O.getMessage());
            }
        });

    }
}
