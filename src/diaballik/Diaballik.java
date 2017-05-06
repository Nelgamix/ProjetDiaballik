package diaballik;

import diaballik.controller.ActionsController;
import diaballik.controller.AffichageController;
import diaballik.controller.TerrainController;
import diaballik.model.ConfigurationPartie;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Terrain;
import diaballik.view.CaseView;
import diaballik.view.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Diaballik extends Application {
    public Stage stage;

    public final static String CSS_MENU_FILE = "DiaballikMenu.css";
    public final static String CSS_JEU_FILE = "DiaballikJeu.css";
    public final static String CSS_DIALOG_FILE = "DiaballikDialogs.css";

    public final static String SAVES_DIRECTORY = "saves";

    private final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN); // save
    private final KeyCombination ctrlZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN); // rollback

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
        Optional<ConfigurationPartie> cp = Dialogs.showNewGameDialog();
        if (cp.isPresent()) {
            initSceneJeu(cp.get());
            showSceneJeu();
        }
    }

    public void newGame(String path, boolean isSave) {
        ConfigurationPartie cp = new ConfigurationPartie(path, isSave);
        initSceneJeu(cp);
        showSceneJeu();
    }

    public void endGame(Joueur gagnant) {
        Dialogs.showEndGame(gagnant);

        showSceneMenu();
    }

    public void exit() {
        Platform.exit();
    }

    private void initSceneJeu(ConfigurationPartie cp) {
        jeu = new Jeu(cp, this);

        TerrainController terrainController = new TerrainController(this);
        ActionsController actionsController = new ActionsController(this);
        AffichageController affichageController = new AffichageController(this);
        BorderPane root = new BorderPane();

        root.setCenter(terrainController.getTerrainView());
        root.setRight(actionsController.getActionsView());
        root.setTop(affichageController.getAffichageView());

        sceneJeu = new Scene(root, CaseView.LARGEUR * Terrain.LARGEUR + 225, CaseView.HAUTEUR * Terrain.HAUTEUR + 75);
        sceneJeu.setOnKeyPressed(k -> {
            if (ctrlS.match(k)) {
                actionsController.saveGame(SAVES_DIRECTORY);
            } else if (ctrlZ.match(k)) {
                actionsController.rollback();
            }
        });
        sceneJeu.getStylesheets().add(getClass().getResource(CSS_JEU_FILE).toExternalForm());
    }
    private void initSceneMenu() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        // TODO: séparer menuScene dans une autre classe

        Label titre = new Label("Diaballik");
        titre.setPadding(new Insets(0, 0, 25, 0));

        Button newGame = new Button("Nouvelle partie");
        newGame.setOnAction(e -> newGame());

        Button loadGame = new Button("Charger une partie");
        loadGame.setOnAction(e -> {
            Optional<String> ofilename = Dialogs.showLoadName(SAVES_DIRECTORY);
            ofilename.ifPresent(s -> newGame(SAVES_DIRECTORY + "/" + s, true));

            // Méthode avec Filechooser
            // Plus souple mais complexifie le choix pour l'utilisateur
            /*FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Diaballik sauvegarde", "*.txt"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                newGame(file.getAbsolutePath(), true);
            }*/
        });

        Button regles = new Button("Règles");
        regles.setOnAction(e -> getHostServices().showDocument("http://inf362.forge.imag.fr/Projet/Regles/diaballik/"));

        Button credits = new Button("Crédits");
        credits.setOnAction(e -> Dialogs.showCredits());

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> exit());

        vBox.getChildren().add(titre);
        vBox.getChildren().add(newGame);
        vBox.getChildren().add(loadGame);
        vBox.getChildren().add(regles);
        vBox.getChildren().add(credits);
        vBox.getChildren().add(quitter);

        root.setCenter(vBox);

        Platform.runLater(root::requestFocus);

        sceneMenu = new Scene(root, 600, 400);
        sceneMenu.getStylesheets().add(getClass().getResource(CSS_MENU_FILE).toExternalForm());
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

    public Scene getSceneJeu() {
        return sceneJeu;
    }
}
