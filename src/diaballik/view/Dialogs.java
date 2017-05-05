package diaballik.view;

import diaballik.Diaballik;
import diaballik.model.ConfigurationPartie;
import diaballik.model.IA;
import diaballik.model.Joueur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Dialogs {
    public static boolean confirmByDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'action");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static void showCredits() {
        int i = 0;

        Dialog<Boolean> credits = new Dialog<>();
        credits.setTitle("Credits");

        // Layouts
        BorderPane corps = new BorderPane();
        corps.setId("dialogCredits");

        GridPane table = new GridPane();
        VBox infos = new VBox();

        // Controls
        Label msgTop = new Label("Jeu réalisé par...");
        msgTop.setAlignment(Pos.TOP_CENTER);
        msgTop.setPrefWidth(Double.MAX_VALUE);

        ColumnConstraints cc = new ColumnConstraints(200);
        table.getColumnConstraints().add(cc);
        table.setAlignment(Pos.TOP_CENTER);
        table.setPadding(new Insets(10, 0, 20, 0));
        ArrayList<Pair<String, String>> noms = new ArrayList<>(
                Arrays.asList(
                        new Pair<>("Nicolas Huchet", "Domaine"),
                        new Pair<>("Loïc Houdebine", "Domaine"),
                        new Pair<>("Paul Reynaud", "Domaine"),
                        new Pair<>("Rana Sherif", "Domaine"),
                        new Pair<>("Anis Belahadji", "Domaine"),
                        new Pair<>("Mourad Idchrife", "Domaine")
                )
        );
        for (Pair<String, String> p : noms) {
            table.add(new Label(p.getKey()), 0, i);
            table.add(new Label(p.getValue()), 1, i);
            i++;
        }

        Label dapres = new Label("D'après le jeu de stratégie de Philippe Lefrançois");
        Label plus = new Label("Plus d'informations: www.diaballik.com");
        infos.getChildren().add(dapres);
        infos.getChildren().add(plus);
        infos.setAlignment(Pos.BOTTOM_CENTER);
        // Hyperlink : getHostServices().showDocument("http://.....");

        // End
        corps.setTop(msgTop);
        corps.setCenter(table);
        corps.setBottom(infos);

        credits.getDialogPane().setContent(corps);
        credits.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG_FILE).toExternalForm());
        credits.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        credits.getDialogPane().setPrefSize(400, 250);

        credits.showAndWait();
    }

    public static Optional<ConfigurationPartie> showNewGameDialog() {
        Dialogs d = new Dialogs();
        return d.getNewGameDialog();
    }

    private Optional<ConfigurationPartie> getNewGameDialog() {
        ObservableList<String> iaDifficultes = FXCollections.observableArrayList(
                "Non",
                "Facile",
                "Moyen",
                "Difficile"
        );

        Dialog<ConfigurationPartie> config = new Dialog<>();
        config.setTitle("Nouvelle partie");

        GridPane content = new GridPane();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        ColumnConstraints cc3 = new ColumnConstraints();
        cc1.setPercentWidth(20);
        cc2.setPercentWidth(50);
        cc3.setPercentWidth(30);
        content.getColumnConstraints().addAll(cc1, cc2, cc3);
        content.setId("dialogNewGame");
        content.setHgap(15);
        content.setVgap(10);

        // Header row
        Label l = new Label("Joueur");
        l.getStyleClass().add("dialogNewGameHeader");
        content.add(l, 0, 0);
        l = new Label("Nom");
        l.getStyleClass().add("dialogNewGameHeader");
        content.add(l, 1, 0);
        l = new Label("IA");
        l.getStyleClass().add("dialogNewGameHeader");
        content.add(l, 2, 0);

        // Row joueur 1
        TextField nomJoueur1 = new TextField();
        nomJoueur1.setPromptText("Nom");
        ComboBox<String> iaJoueur1 = new ComboBox<>(iaDifficultes);
        iaJoueur1.setValue("Non");
        content.add(new Label("1"), 0, 1);
        content.add(nomJoueur1, 1, 1);
        content.add(iaJoueur1, 2, 1);

        // Row joueur 2
        TextField nomJoueur2 = new TextField();
        nomJoueur2.setPromptText("Nom");
        ComboBox<String> iaJoueur2 = new ComboBox<>(iaDifficultes);
        iaJoueur2.setValue("Non");
        content.add(new Label("2"), 0, 2);
        content.add(nomJoueur2, 1, 2);
        content.add(iaJoueur2, 2, 2);

        config.getDialogPane().setContent(content);
        config.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG_FILE).toExternalForm());
        config.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        config.getDialogPane().setPrefSize(400, 150);

        config.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                int ia1 = convertDifficulte(iaJoueur1.getValue());
                int ia2 = convertDifficulte(iaJoueur2.getValue());
                return new ConfigurationPartie(nomJoueur1.getText(), nomJoueur2.getText(), ia1, ia2);
            }

            return null;
        });

        Platform.runLater(nomJoueur1::requestFocus);

        return config.showAndWait();
    }

    private static int convertDifficulte(String difficulte) {
        switch (difficulte) {
            case "Facile":
                return IA.DIFFICULTE_FACILE;
            case "Moyen":
                return IA.DIFFICULTE_MOYEN;
            case "Difficile":
                return IA.DIFFICULTE_DIFFICILE;
            default:
                return 0;
        }
    }

    public static Optional<String> showLoadName(String directory) {
        Dialogs d = new Dialogs();
        return d.getLoadName(directory);
    }

    private Optional<String> getLoadName(String directory) {
        ObservableList<String> obs = FXCollections.observableArrayList(getFilesInDir(directory));

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Charger une partie");

        BorderPane content = new BorderPane();
        ListView<String> filesView = new ListView<>(obs);
        filesView.setMaxWidth(Double.MAX_VALUE);
        filesView.setMaxHeight(Double.MAX_VALUE);
        content.setId("dialogLoadName");
        content.setTop(new Label("Sélectionnez une partie à charger"));
        content.setCenter(filesView);
        content.setPadding(new Insets(25));

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                return filesView.getSelectionModel().getSelectedItem();
            }

            return null;
        });

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG_FILE).toExternalForm());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(400, 400);
        return dialog.showAndWait();
    }

    private List<String> getFilesInDir(String directory) {
        List<String> results = new ArrayList<>();

        File[] files = new File(directory).listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    results.add(file.getName());
                }
            }
        } else {
            return null;
        }

        return results;
    }

    public static void showEndGame(Joueur gagnant) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText("Fin de la partie");
        alert.setContentText("Partie terminée!\nJoueur " + gagnant.getNom() + " l'emporte!");

        alert.showAndWait();
    }
}
