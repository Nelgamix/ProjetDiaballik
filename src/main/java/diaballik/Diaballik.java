package diaballik;

import diaballik.scene.SceneJeu;
import diaballik.scene.SceneMenu;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.stage.Stage;

public class Diaballik extends Application {
    private Stage stage;

    public final static String DOSSIER_SAUVEGARDES = "saves";
    public final static String DOSSIER_TERRAINS = "/defaultTerrains";
    public final static String DOSSIER_IMAGES = "/images";
    private final static String DOSSIER_CSS = "/css";

    public final static String EXTENSION_SAUVEGARDE = ".txt";

    public final static String CSS_MENU = DOSSIER_CSS + "/DiaballikMenu.css";
    public final static String CSS_JEU = DOSSIER_CSS + "/DiaballikJeu.css";
    public final static String CSS_DIALOG = DOSSIER_CSS + "/DiaballikDialogs.css";
    public final static String CSS_POPOVER = DOSSIER_CSS + "/DiaballikPopover.css";

    public final static String NOM_JEU = "Diaballik";

    private SceneJeu sceneJeu;
    private SceneMenu sceneMenu;

    public void showSceneJeu() {
        stage.setScene(sceneJeu.getScene());
        sizeStage();
    }
    public void showSceneMenu() {
        stage.setScene(sceneMenu.getScene());
    }
    public void showSceneMenu(boolean fromJeu) {
        showSceneMenu();
        sizeStage();
    }

    private void sizeStage() {
        stage.sizeToScene();
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
    }

    @Override
    public void stop() throws Exception {
        sceneJeu.stopReseau();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        sceneJeu = new SceneJeu(this);
        sceneMenu = new SceneMenu(this);

        sceneMenu.initShow();

        stage.setTitle(NOM_JEU);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public SceneJeu getSceneJeu() {
        return sceneJeu;
    }
    public SceneMenu getSceneMenu() {
        return sceneMenu;
    }

    public void setCurseurSelection() {
        stage.getScene().getRoot().setCursor(Cursor.HAND);
    }
    public void setCurseurNormal() {
        stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
    }
}
