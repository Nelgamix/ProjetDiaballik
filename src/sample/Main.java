package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.TerrainController;
import sample.model.Jeu;
import sample.model.Terrain;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Jeu jeu = new Jeu();

        TerrainController terrainController = new TerrainController(jeu);

        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(terrainController.getTerrainView(), 350, 350));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
