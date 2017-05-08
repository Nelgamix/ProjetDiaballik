package diaballik.vue;

import diaballik.Diaballik;
import diaballik.Utils;
import diaballik.model.ConfigurationPartie;
import diaballik.model.IA;
import diaballik.model.Jeu;
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
import sun.misc.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Dialogs {
    public static boolean dialogConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'action");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static void montrerCredits() {
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
        credits.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        credits.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        credits.getDialogPane().setPrefSize(400, 250);

        credits.showAndWait();
    }

    public static Optional<ConfigurationPartie> montrerDialogNouvellePartie() {
        Dialogs d = new Dialogs();
        return d.getDialogNouvellePartie();
    }

    private Optional<ConfigurationPartie> getDialogNouvellePartie() {
        ObservableList<String> iaDifficultes = FXCollections.observableArrayList(
                "Non",
                "Facile",
                "Moyen",
                "Difficile"
        );

        Dialog<ConfigurationPartie> config = new Dialog<>();
        config.setTitle("Nouvelle partie");

        VBox content = new VBox(20);
        GridPane configJoueurs = new GridPane();
        GridPane autre = new GridPane();

        content.getChildren().add(configJoueurs);
        content.getChildren().add(autre);

        ColumnConstraints ccJoueur = new ColumnConstraints();
        ColumnConstraints ccNom = new ColumnConstraints();
        ColumnConstraints ccIA = new ColumnConstraints();
        ccJoueur.setPercentWidth(20);
        ccNom.setPercentWidth(50);
        ccIA.setPercentWidth(30);
        configJoueurs.getColumnConstraints().addAll(ccJoueur, ccNom, ccIA);
        configJoueurs.setId("dialogNewGame");
        configJoueurs.setHgap(15);
        configJoueurs.setVgap(10);

        ColumnConstraints ccLabel = new ColumnConstraints();
        ColumnConstraints ccCtrl = new ColumnConstraints();
        ccLabel.setPercentWidth(50);
        ccCtrl.setPercentWidth(50);
        autre.getColumnConstraints().addAll(ccLabel, ccCtrl);
        autre.setHgap(10);
        autre.setVgap(10);

        // Header row
        Label l = new Label("Joueur");
        l.getStyleClass().add("dialogNewGameHeader");
        configJoueurs.add(l, 0, 0);
        l = new Label("Nom");
        l.getStyleClass().add("dialogNewGameHeader");
        configJoueurs.add(l, 1, 0);
        l = new Label("IA");
        l.getStyleClass().add("dialogNewGameHeader");
        configJoueurs.add(l, 2, 0);

        // Row joueur 1
        TextField nomJoueur1 = new TextField();
        nomJoueur1.setPromptText("Nom");
        ComboBox<String> iaJoueur1 = new ComboBox<>(iaDifficultes);
        iaJoueur1.setValue("Non");
        configJoueurs.add(new Label("1"), 0, 1);
        configJoueurs.add(nomJoueur1, 1, 1);
        configJoueurs.add(iaJoueur1, 2, 1);

        // Row joueur 2
        TextField nomJoueur2 = new TextField();
        nomJoueur2.setPromptText("Nom");
        ComboBox<String> iaJoueur2 = new ComboBox<>(iaDifficultes);
        iaJoueur2.setValue("Non");
        configJoueurs.add(new Label("2"), 0, 2);
        configJoueurs.add(nomJoueur2, 1, 2);
        configJoueurs.add(iaJoueur2, 2, 2);

        // autre
        // row noms aléatoires
        Label nomsAleatoires = new Label("Noms aléatoires");
        autre.add(nomsAleatoires, 0, 0);
        CheckBox checkNomsAleatoires = new CheckBox();
        checkNomsAleatoires.selectedProperty().addListener((o, ov, nv) -> {
            nomJoueur1.setDisable(nv);
            nomJoueur2.setDisable(nv);
        });
        autre.add(checkNomsAleatoires, 1, 0);

        // row terrain
        ObservableList<String> terrainsDispo = FXCollections.observableArrayList(getFichiersDansDossier(Diaballik.DOSSIER_TERRAINS, ".txt", true));
        Label terrain = new Label("Variante de terrain");
        autre.add(terrain, 0, 1);
        ComboBox<String> terrains = new ComboBox<>(terrainsDispo);
        terrains.getSelectionModel().select(0);
        terrains.setMaxWidth(Double.MAX_VALUE);
        autre.add(terrains, 1, 1);

        // setup
        config.getDialogPane().setContent(content);
        config.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        config.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        config.getDialogPane().setPrefSize(400, 150);

        config.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                int ia1 = convertirDifficulte(iaJoueur1.getValue());
                int ia2 = convertirDifficulte(iaJoueur2.getValue());
                if (checkNomsAleatoires.isSelected())
                    return new ConfigurationPartie(ia1, ia2, terrains.getSelectionModel().getSelectedItem() + ".txt");
                else {
                    if (!Utils.nomValide(nomJoueur1.getText()) || !Utils.nomValide(nomJoueur2.getText()))
                        return null;
                    else
                        return new ConfigurationPartie(nomJoueur1.getText(), nomJoueur2.getText(), ia1, ia2, terrains.getSelectionModel().getSelectedItem() + ".txt");
                }
            }

            return null;
        });

        Platform.runLater(nomJoueur1::requestFocus);

        return config.showAndWait();
    }

    private static int convertirDifficulte(String difficulte) {
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

    public static Optional<String> montrerDialogChoisirFichier(String directory) {
        Dialogs d = new Dialogs();
        return d.getDialogChoisirFichier(directory);
    }

    private Optional<String> getDialogChoisirFichier(String directory) {
        ObservableList<String> obs = FXCollections.observableArrayList(getFichiersDansDossier("." + directory, ".txt", false));

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
                return filesView.getSelectionModel().getSelectedItem() + ".txt";
            }

            return null;
        });

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(400, 400);
        return dialog.showAndWait();
    }

    private List<String> getFichiersDansDossier(String directory, String extension, boolean resource) {
        List<String> results = new ArrayList<>();

        if (resource) {
            final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

            if (jarFile.isFile()) { // Si on lance depuis un jar
                final JarFile jar;
                try {
                    jar = new JarFile(jarFile);
                    final Enumeration<JarEntry> entries = jar.entries(); // envoie toutes les entrées du jar
                    while(entries.hasMoreElements()) {
                        final String name = entries.nextElement().getName();
                        if (name.startsWith(directory.substring(1) + "/")) { // filtrer selon le directory
                            if (!name.endsWith("/")) {
                                System.out.println(name);
                                results.add(name.substring(directory.length(), name.length() - extension.length()));
                            }
                        }
                    }

                    jar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // Run with IDE
                final URL url = Launcher.class.getResource(directory);
                if (url != null) {
                    try {
                        final File apps = new File(url.toURI());
                        File[] files = apps.listFiles((dir, name) -> name.endsWith(extension));
                        if (files != null) {
                            for (File app : files) {
                                System.out.println(app);
                                results.add(app.getName().substring(0, app.getName().length() - extension.length()));
                            }
                        }
                    } catch (URISyntaxException ignored) {}
                }
            }
        } else {
            File file = new File(directory);

            File[] files = file.listFiles((dir, name) -> name.endsWith(extension));

            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        results.add(f.getName().substring(0, f.getName().length() - extension.length()));
                    }
                }
            }
        }

        return results;
    }

    public static void montrerFinJeu(Joueur gagnant, int victoireType) {
        String victoireMessage = "Type de victoire: ";
        switch (victoireType) {
            case Jeu.VICTOIRE_NORMALE:
                victoireMessage += "normale.";
                break;
            case Jeu.VICTOIRE_ANTIJEU:
                victoireMessage += "par antijeu.";
                break;
            default:
                victoireMessage += "inconnue.";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText("Fin de la partie");
        alert.setContentText("Partie terminée!\nJoueur " + gagnant.getNom() + " l'emporte!\n" + victoireMessage);

        alert.showAndWait();
    }
}
