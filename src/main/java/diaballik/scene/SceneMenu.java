package diaballik.scene;

import diaballik.Diaballik;
import diaballik.vue.Dialogs;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SceneMenu {
    private final Diaballik diaballik;
    private Scene scene;

    private final static String LOGO = Diaballik.DOSSIER_IMAGES + "/logo-diaballik.png";

    public SceneMenu(Diaballik diaballik) {
        this.diaballik = diaballik;
    }

    public void initShow() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 200, 20, 200));

        ImageView logo = new ImageView(getClass().getResource(LOGO).toExternalForm());
        VBox.setMargin(logo, new Insets(0, 0, 10, 0));

        Button newGame = new Button("Nouvelle partie");
        newGame.setOnAction(e -> diaballik.getSceneJeu().dialogNouveauJeu());

        Button loadGame = new Button("Charger une partie");
        loadGame.setOnAction(e -> diaballik.getSceneJeu().dialogChargerJeu());

        Button newGameNetwork = new Button("Partie en réseau");
        newGameNetwork.setOnAction(e -> diaballik.getSceneJeu().dialogNouveauJeuReseau());

        Button regles = new Button("Règles");
        regles.setOnAction(e -> diaballik.getHostServices().showDocument("http://inf362.forge.imag.fr/Projet/Regles/diaballik"));

        Button credits = new Button("Crédits");
        credits.setOnAction(e -> Dialogs.montrerCredits());

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> Platform.exit());

        vBox.getChildren().add(logo);
        vBox.getChildren().add(newGame);
        vBox.getChildren().add(loadGame);
        vBox.getChildren().add(newGameNetwork);
        vBox.getChildren().add(regles);
        vBox.getChildren().add(credits);
        vBox.getChildren().add(quitter);

        root.setCenter(vBox);

        Platform.runLater(root::requestFocus);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(Diaballik.CSS_MENU).toExternalForm());

        diaballik.showSceneMenu();
    }

    public Scene getScene() {
        return scene;
    }
}
