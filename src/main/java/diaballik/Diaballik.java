package diaballik;

import diaballik.controleur.ActionsControleur;
import diaballik.controleur.AffichageControleur;
import diaballik.controleur.TerrainControleur;
import diaballik.model.ConfigurationPartie;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Terrain;
import diaballik.vue.CaseVue;
import diaballik.vue.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

    public final static String DOSSIER_SAUVEGARDES = "/saves";
    public final static String DOSSIER_TERRAINS = "/defaultTerrains";
    public final static String DOSSIER_CSS = "/css";

    private final static String CSS_MENU = DOSSIER_CSS + "/DiaballikMenu.css";
    private final static String CSS_JEU = DOSSIER_CSS + "/DiaballikJeu.css";
    public final static String CSS_DIALOG = DOSSIER_CSS + "/DiaballikDialogs.css";

    public final static String NOM_JEU = "Diaballik";

    private final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN); // sauvegarde
    private final KeyCombination ctrlZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN); // actionAnnuler

    private Scene sceneJeu;
    private Scene sceneMenu;

    private Jeu jeu;

    public void showSceneJeu() {
        stage.setScene(sceneJeu);
    }

    public void showSceneMenu() {
        stage.setScene(sceneMenu);
    }

    private void nouveauJeu() {
        Optional<ConfigurationPartie> cp = Dialogs.montrerDialogNouvellePartie();
        if (cp.isPresent()) {
            initSceneJeu(cp.get());
            showSceneJeu();
        }
    }

    private void nouveauJeu(String path, boolean isSave) {
        ConfigurationPartie cp = new ConfigurationPartie(path, isSave);
        initSceneJeu(cp);
        showSceneJeu();
    }

    public void finJeu(Joueur gagnant, int victoireType) {
        Dialogs.montrerFinJeu(gagnant, victoireType);

        showSceneMenu();
    }

    public void terminer() {
        Platform.exit();
    }

    private void initSceneJeu(ConfigurationPartie cp) {
        jeu = new Jeu(cp, this);

        TerrainControleur terrainControleur = new TerrainControleur(this);
        ActionsControleur actionsControleur = new ActionsControleur(this);
        AffichageControleur affichageControleur = new AffichageControleur(this);
        BorderPane root = new BorderPane();

        root.setCenter(terrainControleur.getTerrainVue());
        root.setRight(actionsControleur.getActionsVue());
        root.setTop(affichageControleur.getAffichageVue());

        sceneJeu = new Scene(root, CaseVue.LARGEUR * Terrain.LARGEUR + 225, CaseVue.HAUTEUR * Terrain.HAUTEUR + 75);
        sceneJeu.setOnKeyPressed(k -> {
            if (ctrlS.match(k)) {
                actionsControleur.actionSauvegarderJeu(DOSSIER_SAUVEGARDES);
            } else if (ctrlZ.match(k)) {
                actionsControleur.actionAnnuler();
            }
        });
        sceneJeu.getStylesheets().add(getClass().getResource(CSS_JEU).toExternalForm());
    }
    private void initSceneMenu() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        Label titre = new Label(NOM_JEU);
        titre.setPadding(new Insets(0, 0, 25, 0));

        Button newGame = new Button("Nouvelle partie");
        newGame.setOnAction(e -> nouveauJeu());

        Button loadGame = new Button("Charger une partie");
        loadGame.setOnAction(e -> {
            Optional<String> ofilename = Dialogs.montrerDialogChoisirFichier(DOSSIER_SAUVEGARDES);
            if (ofilename != null)
                ofilename.ifPresent(s -> nouveauJeu(DOSSIER_SAUVEGARDES + "/" + s, true));

            // Méthode avec Filechooser
            // Plus souple mais complexifie le choix pour l'utilisateur
            /*FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Diaballik sauvegarde", "*.txt"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                nouveauJeu(file.getAbsolutePath(), true);
            }*/
        });

        Button regles = new Button("Règles");
        regles.setOnAction(e -> getHostServices().showDocument("http://inf362.forge.imag.fr/Projet/Regles/diaballik/"));

        Button credits = new Button("Crédits");
        credits.setOnAction(e -> Dialogs.montrerCredits());

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> terminer());

        vBox.getChildren().add(titre);
        vBox.getChildren().add(newGame);
        vBox.getChildren().add(loadGame);
        vBox.getChildren().add(regles);
        vBox.getChildren().add(credits);
        vBox.getChildren().add(quitter);

        root.setCenter(vBox);

        Platform.runLater(root::requestFocus);

        sceneMenu = new Scene(root, 600, 400);
        sceneMenu.getStylesheets().add(getClass().getResource(CSS_MENU).toExternalForm());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        initSceneMenu();

        stage.setTitle(NOM_JEU);
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

    public void setCurseurSelection(Scene scene) {
        scene.setCursor(Cursor.HAND);
    }

    public void setCurseurNormal(Scene scene) {
        scene.setCursor(Cursor.DEFAULT);
    }
}
