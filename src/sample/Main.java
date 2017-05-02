package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.TerrainController;
import sample.model.Terrain;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Terrain terrain = new Terrain();

        TerrainController terrainController = new TerrainController(terrain);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(terrainController.getTerrainView(), 350, 350));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
