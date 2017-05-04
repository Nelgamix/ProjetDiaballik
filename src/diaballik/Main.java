package diaballik;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import diaballik.controller.ActionsController;
import diaballik.controller.AffichageController;
import diaballik.controller.TerrainController;
import diaballik.model.Jeu;
import diaballik.model.Terrain;
import diaballik.view.CaseView;

public class Main extends Application {

    private Stage stage;

    private Scene sceneJeu;
    private Scene sceneMenu;

    private void initSceneJeu() {
        Jeu jeu = new Jeu();

        TerrainController terrainController = new TerrainController(jeu);
        ActionsController actionsController = new ActionsController(jeu);
        AffichageController affichageController = new AffichageController(jeu);
        BorderPane root = new BorderPane();
        root.setCenter(terrainController.getTerrainView());
        root.setRight(actionsController.getActionsView());
        root.setTop(affichageController.getAffichageView());

        sceneJeu = new Scene(root, CaseView.LARGEUR * Terrain.LARGEUR + 150, CaseView.HAUTEUR * Terrain.HAUTEUR + 50);
        sceneJeu.getStylesheets().add(getClass().getResource("Main.css").toExternalForm());
    }
    private void initSceneMenu() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        Button jouer = new Button("Jouer");
        jouer.setOnAction(e -> stage.setScene(sceneJeu));
        jouer.setMaxWidth(160);
        jouer.setMaxHeight(30);
        jouer.setStyle("-fx-font-size: 20px");
        vBox.getChildren().add(jouer);

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> Platform.exit());
        quitter.setMaxWidth(160);
        quitter.setMaxHeight(30);
        quitter.setStyle("-fx-font-size: 20px");
        vBox.getChildren().add(quitter);

        root.setCenter(vBox);
        
        Platform.runLater(root::requestFocus);

        sceneMenu = new Scene(root, 600, 400);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        initSceneJeu();
        initSceneMenu();

        stage.setTitle("Diaballik");
        stage.setResizable(false);
        stage.setScene(sceneMenu);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
