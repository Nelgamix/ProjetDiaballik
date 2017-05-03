package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.controller.ActionsController;
import sample.controller.AffichageController;
import sample.controller.TerrainController;
import sample.model.Jeu;
import sample.model.Terrain;
import sample.view.CaseView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Jeu jeu = new Jeu();

        TerrainController terrainController = new TerrainController(jeu);
        ActionsController actionsController = new ActionsController(jeu);
        AffichageController affichageController = new AffichageController(jeu);
        BorderPane root = new BorderPane();
        root.setCenter(terrainController.getTerrainView());
        root.setRight(actionsController.getActionsView());
        root.setTop(affichageController.getAffichageView());

        Scene scene = new Scene(root, CaseView.LARGEUR * Terrain.LARGEUR + 150, CaseView.HAUTEUR * Terrain.HAUTEUR + 50);
        scene.getStylesheets().add(getClass().getResource("Main.css").toExternalForm());

        primaryStage.setTitle("Diaballik");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
