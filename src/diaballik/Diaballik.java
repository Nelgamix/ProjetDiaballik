package diaballik;

import diaballik.model.Joueur;
import diaballik.view.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import diaballik.controller.ActionsController;
import diaballik.controller.AffichageController;
import diaballik.controller.TerrainController;
import diaballik.model.Jeu;
import diaballik.model.Terrain;
import diaballik.view.CaseView;

import java.io.File;

public class Diaballik extends Application {
    public Stage stage;

    public final static String DEFAULT_TERRAIN_PATH = "defaultTerrains/defaultTerrain.txt";

    private Scene sceneJeu;
    private Scene sceneMenu;

    private Jeu jeu;

    public void showSceneJeu() {
        stage.setScene(sceneJeu);
    }

    public void showSceneMenu() {
        stage.setScene(sceneMenu);
    }

    public void newGame() {
        newGame(DEFAULT_TERRAIN_PATH, false);
    }

    public void newGame(String path) {
        newGame(path, false);
    }

    public void newGame(String path, boolean isSave) {
        initSceneJeu(path, isSave);
        showSceneJeu();
    }

    public void finJeu(Joueur gagnant) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!\nJoueur " + gagnant.getNom() + " won!!!");

        alert.showAndWait();

        showSceneMenu();
    }

    public void exit() {
        Platform.exit();
    }

    private void initSceneJeu(String path, boolean isSave) {
        jeu = new Jeu(path, isSave, this);

        TerrainController terrainController = new TerrainController(this);
        ActionsController actionsController = new ActionsController(this);
        AffichageController affichageController = new AffichageController(this);
        BorderPane root = new BorderPane();

        root.setCenter(terrainController.getTerrainView());
        root.setRight(actionsController.getActionsView());
        root.setTop(affichageController.getAffichageView());

        sceneJeu = new Scene(root, CaseView.LARGEUR * Terrain.LARGEUR + 225, CaseView.HAUTEUR * Terrain.HAUTEUR + 75);
        sceneJeu.getStylesheets().add(getClass().getResource("DiaballikJeu.css").toExternalForm());
    }
    private void initSceneMenu() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        // TODO: séparer menuScene dans une autre classe

        Label titre = new Label("Diaballik");

        Button newGame = new Button("Nouvelle partie");
        newGame.setOnAction(e -> newGame());

        Button loadGame = new Button("Charger une partie");
        loadGame.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Diaballik sauvegarde", "*.txt"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                newGame(file.getAbsolutePath(), true);
            }
        });

        Button credits = new Button("Crédits");
        credits.setOnAction(e -> Dialogs.showCredits());

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> exit());

        vBox.getChildren().add(titre);
        vBox.getChildren().add(newGame);
        vBox.getChildren().add(loadGame);
        vBox.getChildren().add(credits);
        vBox.getChildren().add(quitter);

        root.setCenter(vBox);

        Platform.runLater(root::requestFocus);

        sceneMenu = new Scene(root, 600, 400);
        sceneMenu.getStylesheets().add(getClass().getResource("DiaballikMenu.css").toExternalForm());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        initSceneMenu();

        stage.setTitle("Diaballik");
        stage.setResizable(false);
        stage.setScene(sceneMenu);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Jeu getJeu() {
        return this.jeu;
    }
}
