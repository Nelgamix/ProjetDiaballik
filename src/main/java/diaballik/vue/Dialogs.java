package diaballik.vue;

import diaballik.Diaballik;
import diaballik.autre.Reseau;
import diaballik.autre.Utils;
import diaballik.model.*;
import diaballik.scene.SceneJeu;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Dialogs {
    private int route = 0; //0 = aucune route, 1 = créer, 2 = rejoindre

    public static boolean dialogConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'action");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    public static Optional<ConfigurationPartie> montrerDialogNouvellePartie(ConfigurationPartie cp) {
        Dialogs d = new Dialogs();
        return d.getDialogNouvellePartie(cp);
    }
    private Optional<ConfigurationPartie> getDialogNouvellePartie(ConfigurationPartie cp) {
        ObservableList<String> iaDifficultes = FXCollections.observableArrayList(
                "Humain",
                "IA Facile",
                "IA Moyen",
                "IA Difficile"
        );

        Dialog<ConfigurationPartie> config = new Dialog<>();
        config.setTitle("Nouvelle partie");
        ButtonType boutonJouerType = new ButtonType("Jouer", ButtonBar.ButtonData.OK_DONE);
        config.getDialogPane().getButtonTypes().addAll(boutonJouerType, ButtonType.CANCEL);

        Node boutonJouer = config.getDialogPane().lookupButton(boutonJouerType);

        BorderPane contentWrapper = new BorderPane();
        contentWrapper.setPadding(new Insets(20));

        VBox content = new VBox(20);
        GridPane configJoueurs = new GridPane();
        GridPane autre = new GridPane();

        content.getChildren().add(configJoueurs);
        content.getChildren().add(autre);

        ColumnConstraints ccJoueur = new ColumnConstraints();
        ColumnConstraints ccNom = new ColumnConstraints();
        ColumnConstraints ccIA = new ColumnConstraints();
        ccJoueur.setPercentWidth(20);
        ccNom.setPercentWidth(45);
        ccIA.setPercentWidth(35);
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

        // Row joueur 1
        TextField nomJoueur1 = new TextField("Joueur 1");
        nomJoueur1.setPromptText("Nom");
        nomJoueur1.textProperty().addListener((o, ov, nv) -> boutonJouer.setDisable(nv.trim().length() < 3));
        ComboBox<String> iaJoueur1 = new ComboBox<>(iaDifficultes);
        iaJoueur1.setMaxWidth(Double.MAX_VALUE);
        iaJoueur1.setOnAction(e -> {
            switch (iaJoueur1.getSelectionModel().getSelectedIndex()) {
                case 0:
                    nomJoueur1.setText("Joueur 1");
                    nomJoueur1.setDisable(false);
                    break;
                case 1:
                    nomJoueur1.setText("IA 1 Facile");
                    nomJoueur1.setDisable(true);
                    break;
                case 2:
                    nomJoueur1.setText("IA 1 Moyen");
                    nomJoueur1.setDisable(true);
                    break;
                case 3:
                    nomJoueur1.setText("IA 1 Difficile");
                    nomJoueur1.setDisable(true);
                    break;
            }
        });
        configJoueurs.add(new Label("1"), 0, 1);
        configJoueurs.add(nomJoueur1, 1, 1);
        configJoueurs.add(iaJoueur1, 2, 1);

        // Row joueur 2
        TextField nomJoueur2 = new TextField("Joueur 2");
        nomJoueur2.setPromptText("Nom");
        nomJoueur2.textProperty().addListener((o, ov, nv) -> boutonJouer.setDisable(nv.trim().length() < 3));
        ComboBox<String> iaJoueur2 = new ComboBox<>(iaDifficultes);
        iaJoueur2.setMaxWidth(Double.MAX_VALUE);
        iaJoueur2.setOnAction(e -> {
            switch (iaJoueur2.getSelectionModel().getSelectedIndex()) {
                case 0:
                    nomJoueur2.setText("Joueur 2");
                    nomJoueur2.setDisable(false);
                    break;
                case 1:
                    nomJoueur2.setText("IA 2 Facile");
                    nomJoueur2.setDisable(true);
                    break;
                case 2:
                    nomJoueur2.setText("IA 2 Moyen");
                    nomJoueur2.setDisable(true);
                    break;
                case 3:
                    nomJoueur2.setText("IA 2 Difficile");
                    nomJoueur2.setDisable(true);
                    break;
            }
        });
        configJoueurs.add(new Label("2"), 0, 2);
        configJoueurs.add(nomJoueur2, 1, 2);
        configJoueurs.add(iaJoueur2, 2, 2);

        Label timer = new Label("Temps maximum par tour");
        autre.add(timer, 0, 1);
        ComboBox<Integer> comboTimer = new ComboBox<>();
        comboTimer.setMaxWidth(Double.MAX_VALUE);
        comboTimer.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == 0) return "Pas de timer";
                return object + " secondes";
            }

            @Override
            public Integer fromString(String string) {
                return null;
            }
        });
        comboTimer.getItems().addAll(0, 15, 30, 45, 60);
        autre.add(comboTimer, 1, 1);

        // row terrain
        Label terrain = new Label("Variante de terrain");
        autre.add(terrain, 0, 0);
        TerrainComboBox terrains = new TerrainComboBox();
        terrains.setMaxWidth(Double.MAX_VALUE);
        autre.add(terrains, 1, 0);

        // setup
        Label titre = new Label("Nouvelle partie");
        titre.getStyleClass().add("titre");
        BorderPane.setAlignment(titre, Pos.CENTER);
        BorderPane.setMargin(titre, new Insets(-10, 0, 12, 0));
        contentWrapper.setTop(titre);
        contentWrapper.setCenter(content);
        config.getDialogPane().setContent(contentWrapper);
        config.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        config.getDialogPane().setPrefSize(400, 150);

        config.setResultConverter(b -> {
            if (b == boutonJouerType) {
                int ia1 = convertirDifficulte(iaJoueur1.getValue());
                int ia2 = convertirDifficulte(iaJoueur2.getValue());
                if (!Utils.nomValide(nomJoueur1.getText()) || !Utils.nomValide(nomJoueur2.getText()))
                    return null;
                else
                    return new ConfigurationPartie(nomJoueur1.getText(), nomJoueur2.getText(), ia1, ia2, comboTimer.getSelectionModel().getSelectedItem(), terrains.getTerrainSelectionnePath());
            }

            return null;
        });

        if (cp != null) {
            Platform.runLater(() -> {
                nomJoueur1.setText(cp.getNomJoueur1());
                nomJoueur2.setText(cp.getNomJoueur2());
                iaJoueur1.getSelectionModel().select(cp.getTypeJoueur1() < 4 ? cp.getTypeJoueur1() : 0);
                iaJoueur2.getSelectionModel().select(cp.getTypeJoueur2() < 4 ? cp.getTypeJoueur2() : 0);

                terrains.selectionTerrain(cp.getTerrain());
                comboTimer.getSelectionModel().select((Integer)cp.getDureeTimer());
            });
        } else {
            iaJoueur1.getSelectionModel().selectFirst();
            iaJoueur2.getSelectionModel().selectFirst();

            terrains.selectionTerrain("");
            comboTimer.getSelectionModel().select((Integer)30);
        }

        Platform.runLater(nomJoueur1::requestFocus);

        return config.showAndWait();
    }

    private static int convertirDifficulte(String difficulte) {
        switch (difficulte) {
            case "IA Facile":
                return JoueurIA.DIFFICULTE_FACILE;
            case "IA Moyen":
                return JoueurIA.DIFFICULTE_MOYEN;
            case "IA Difficile":
                return JoueurIA.DIFFICULTE_DIFFICILE;
            default:
                return 0;
        }
    }

    public static Optional<String> montrerDialogChoisirFichier(String directory) {
        Dialogs d = new Dialogs();
        return d.getDialogChoisirFichier(directory);
    }
    private Optional<String> getDialogChoisirFichier(String directory) {
        ObservableList<String> obs = FXCollections.observableArrayList();
        reloadObs(obs, directory);

        if (obs.size() < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Aucune sauvegarde");
            alert.setHeaderText("Pas de sauvegarde trouvée");
            alert.setContentText("Aucune sauvegarde n'a été trouvée.\nImpossible d'afficher cette fenêtre.");

            alert.showAndWait();
            return null;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Charger une partie");

        ButtonType boutonOuvrirType = new ButtonType("Ouvrir", ButtonBar.ButtonData.OK_DONE);
        ButtonType boutonAnnulerType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonOuvrirType, boutonAnnulerType);
        Node boutonJouer = dialog.getDialogPane().lookupButton(boutonOuvrirType);
        //Node boutonAnnuler = dialog.getDialogPane().lookupButton(boutonAnnulerType);
        boutonJouer.setDisable(true);

        BorderPane contentWrapper = new BorderPane();
        contentWrapper.setPadding(new Insets(20));

        GridPane content = new GridPane();
        content.setHgap(20);
        content.setVgap(5);
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc1.setPercentWidth(33);
        cc2.setPercentWidth(67);
        content.getColumnConstraints().addAll(cc1, cc2);

        // Partie de droite (info sur la partie sélectionnée)
        ArrayList<Metadonnees> mds = new ArrayList<>();
        for (String o : obs) {
            mds.add(Utils.getMetadonneesSauvegarde(o));
        }

        GridPane infosSave = new GridPane();
        ColumnConstraints ccis = new ColumnConstraints();
        ccis.setPercentWidth(40);
        infosSave.getColumnConstraints().addAll(ccis);

        Label
                labelInfoTour = new Label(),
                labelInfoNomJoueur1 = new Label(),
                labelInfoNomJoueur2 = new Label();
        TerrainApercu ta = new TerrainApercu();
        //infosSave.add(new Label("Version de la sauvegarde"), 0, 0);
        infosSave.add(new Label("Tour"), 0, 0);
        infosSave.add(new Label("Nom joueur 1"), 0, 1);
        infosSave.add(new Label("Nom joueur 2"), 0, 2);
        infosSave.add(new Label("Aperçu du terrain"), 0, 3);

        infosSave.add(labelInfoTour, 1, 0);
        infosSave.add(labelInfoNomJoueur1, 1, 1);
        infosSave.add(labelInfoNomJoueur2, 1, 2);

        GridPane.setHgrow(ta, Priority.ALWAYS);
        GridPane.setVgrow(ta, Priority.ALWAYS);
        infosSave.add(ta, 1, 3);

        // Partie de gauche (liste de saves)
        BorderPane bp = new BorderPane();
        ListView<String> filesView = new ListView<>(obs);
        filesView.setMaxWidth(Double.MAX_VALUE);
        filesView.setMaxHeight(Double.MAX_VALUE);
        filesView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                dialog.setResult(filesView.getSelectionModel().getSelectedItem() + Diaballik.EXTENSION_SAUVEGARDE);
                dialog.close();
            }
        });
        filesView.setOnKeyPressed(e -> {
            String item = filesView.getSelectionModel().getSelectedItem();
            if (item != null) {
                if (e.getCode() == KeyCode.DELETE) {
                    if (supprimerFichier(item)) {
                        reloadObs(obs, directory);
                        if (obs.size() < 1) dialog.close();
                    }
                } else if (e.getCode() == KeyCode.ENTER) {
                    dialog.setResult(filesView.getSelectionModel().getSelectedItem() + Diaballik.EXTENSION_SAUVEGARDE);
                    dialog.close();
                }
            }
        });
        filesView.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (filesView.getSelectionModel().getSelectedItem() != null)
                boutonJouer.setDisable(false);

            // update infos
            if (filesView.getSelectionModel().getSelectedIndex() > -1) {
                Metadonnees md = mds.get(filesView.getSelectionModel().getSelectedIndex());
                labelInfoTour.setText(md.tour + "");
                labelInfoNomJoueur1.setText(md.joueurVert.getNom());
                labelInfoNomJoueur2.setText(md.joueurRouge.getNom());
                ta.setTerrain(md.terrain);
            }
        });
        filesView.getSelectionModel().selectFirst();

        bp.setCenter(filesView);
        Button del = new Button("Supprimer");
        del.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(del, new Insets(10, 0, 0, 0));
        del.setOnAction(e -> {
            String item = filesView.getSelectionModel().getSelectedItem();
            if (item != null) {
                if (supprimerFichier(item)) {
                    reloadObs(obs, directory);
                    if (obs.size() < 1) dialog.close();
                }
            }
        });
        bp.setBottom(del);

        // setup content
        content.setId("dialogLoadName");
        content.add(new Label("Sauvegardes"), 0, 0);
        content.add(bp, 0, 1);
        content.add(new Label("Sauvegarde sélectionnée"), 1, 0);
        content.add(infosSave, 1, 1);

        dialog.setResultConverter(b -> {
            if (b == boutonAnnulerType) return null;

            if (filesView.getSelectionModel().getSelectedItem() != null) {
                return filesView.getSelectionModel().getSelectedItem() + Diaballik.EXTENSION_SAUVEGARDE;
            }

            return null;
        });

        Label titre = new Label("Charger une partie");
        titre.getStyleClass().add("titre");
        BorderPane.setAlignment(titre, Pos.CENTER);
        BorderPane.setMargin(titre, new Insets(-10, 0, 12, 0));
        contentWrapper.setTop(titre);
        contentWrapper.setCenter(content);
        dialog.getDialogPane().setContent(contentWrapper);
        dialog.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        dialog.getDialogPane().setPrefSize(640, 440);

        return dialog.showAndWait();
    }

    private void reloadObs(ObservableList<String> obs, String directory) {
        if (obs.size() > 0) obs.clear();
        obs.setAll(Utils.getFichiersDansDossier(directory, Diaballik.EXTENSION_SAUVEGARDE, false));
    }
    private boolean supprimerFichier(String nom) {
        File f = new File(Diaballik.DOSSIER_SAUVEGARDES + "/" + nom + Diaballik.EXTENSION_SAUVEGARDE);
        System.out.println("Suppression de " + f.getAbsolutePath());
        if (f.exists() && f.delete()) {
            return true;
        } else {
            System.err.println("(Dialogs.getDialogChoisirFichier) Erreur de suppression de sauvegarde");
            return false;
        }
    }

    public static void montrerRegles() {
        Dialog<Void> d = new Dialog<>();
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        HBox images = new HBox(10);

        ImageView img1 = new ImageView(Dialogs.class.getResource(Diaballik.DOSSIER_IMAGES + "/regles1.png").toExternalForm());
        ImageView img2 = new ImageView(Dialogs.class.getResource(Diaballik.DOSSIER_IMAGES + "/regles2.png").toExternalForm());

        images.getChildren().addAll(img1, img2);

        d.getDialogPane().setContent(images);
        d.showAndWait();
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
    public static void montrerAntijeu(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Antijeu non valide");
        alert.setHeaderText("L'antijeu déclaré n'est pas légitime");
        alert.setContentText("Raison: " + message);

        alert.showAndWait();
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

        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc1.setPercentWidth(40);
        cc2.setPercentWidth(60);
        cc1.setHalignment(HPos.CENTER);
        cc2.setHalignment(HPos.CENTER);
        table.getColumnConstraints().addAll(cc1, cc2);
        table.setAlignment(Pos.TOP_CENTER);
        table.setPadding(new Insets(10, 0, 20, 0));
        ArrayList<Pair<String, String>> noms = new ArrayList<>(
                Arrays.asList(
                        new Pair<>("Nicolas Huchet", "Logique et implémentation"),
                        new Pair<>("Loïc Houdebine", "IHM"),
                        new Pair<>("Paul Reynaud", "IA"),
                        new Pair<>("Rana Sherif", "IA"),
                        new Pair<>("Anis Belahadji", "IA"),
                        new Pair<>("Mourad Idchrife", "IA")
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

    public static void montrerReseau(SceneJeu sceneJeu) {
        Dialogs d = new Dialogs();
        d.getReseau(sceneJeu);
    }
    private void getReseau(SceneJeu sceneJeu) {
        Dialog<Void> dialog = new Dialog<>();
        VBox choixType = new VBox(12);

        GridPane clientChoix = new GridPane();
        GridPane hostChoix = new GridPane();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        String adresseLocale = Utils.getInternalIp();
        String adresseExterne = Utils.getExternalIp();

        BorderPane contentWrapper = new BorderPane();
        contentWrapper.setPadding(new Insets(20));
        Label titre = new Label("Partie en réseau");

        BorderPane hostAttente = new BorderPane();
        hostAttente.setMinWidth(200);
        hostAttente.setId("labelAttente");
        VBox hostLabels = new VBox(2);
        hostLabels.getChildren().addAll(
                new Label("Adresse IP (locale): " + adresseLocale),
                new Label("Adresse IP (externe): " + adresseExterne),
                new Label("Port: " + Reseau.PORT)
        );
        hostLabels.setAlignment(Pos.CENTER);
        BorderPane.setMargin(hostLabels, new Insets(0,0,20,0));
        Button quitter = new Button("Annuler");
        quitter.setMaxWidth(Double.MAX_VALUE);
        quitter.setOnAction(e -> {
            if (route > 0 && route <= 2) {
                if (
                            sceneJeu.getReseau().getTacheActuelle() == Reseau.Tache.ATTENTE_SERVEUR
                        ||  sceneJeu.getReseau().getTacheActuelle() == Reseau.Tache.ATTENTE_CLIENT
                ) {
                    sceneJeu.getReseau().fermerReseau();
                }

                switch (route) {
                    case 1:
                        titre.setText("Partie en réseau: créer");
                        contentWrapper.setCenter(hostChoix);
                        break;
                    case 2:
                        titre.setText("Partie en réseau: rejoindre");
                        contentWrapper.setCenter(clientChoix);
                        break;
                    default: // impossible normalement
                        break;
                }

                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            } else {
                dialog.close();
            }
        });
        hostAttente.setCenter(hostLabels);
        hostAttente.setBottom(quitter);

        hostChoix.setHgap(14);
        hostChoix.setVgap(10);
        ColumnConstraints hcc1 = new ColumnConstraints();
        ColumnConstraints hcc2 = new ColumnConstraints();
        hcc1.setPercentWidth(33);
        hcc2.setPercentWidth(67);
        hostChoix.getColumnConstraints().addAll(hcc1, hcc2);
        TextField hostNom = new TextField("Joueur 1");
        TerrainComboBox terrains = new TerrainComboBox();
        terrains.setMaxWidth(Double.MAX_VALUE);

        HBox hostBoutons = new HBox(10);
        Button hostChoixValider = new Button("Lancer l'attente");
        Button hostChoixAnnuler = new Button("Retour");
        hostChoixValider.setMaxWidth(Double.MAX_VALUE);
        hostChoixAnnuler.setMaxWidth(Double.MAX_VALUE);
        hostChoixValider.setOnAction(e -> {
            if (hostNom.getText().length() > 2) {
                sceneJeu.getReseau().d = dialog;
                sceneJeu.getReseau().host(hostNom.getText(), terrains.getTerrainSelectionnePath());
                titre.setText("Partie en réseau: attente");
                contentWrapper.setCenter(hostAttente);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });
        hostChoixAnnuler.setOnAction(e -> {
            titre.setText("Partie en réseau");
            contentWrapper.setCenter(choixType);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });
        HBox.setHgrow(hostChoixAnnuler, Priority.ALWAYS);
        HBox.setHgrow(hostChoixValider, Priority.ALWAYS);
        hostBoutons.getChildren().addAll(hostChoixAnnuler, hostChoixValider);
        hostBoutons.setPadding(new Insets(10, 0, 0, 0));
        Label hostChoixTitre = new Label("Créer une partie");
        hostChoixTitre.setFont(new Font(null, 18));
        hostChoixTitre.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHalignment(hostChoixTitre, HPos.CENTER);

        hostChoix.add(new Label("Nom du joueur"), 0, 0);
        hostChoix.add(hostNom, 1, 0);
        hostChoix.add(new Label("Terrain"), 0, 1);
        hostChoix.add(terrains, 1, 1);
        hostChoix.add(hostBoutons, 0, 2, 2, 1);

        clientChoix.setHgap(14);
        clientChoix.setVgap(10);
        ColumnConstraints ccc1 = new ColumnConstraints();
        ColumnConstraints ccc2 = new ColumnConstraints();
        ccc1.setPercentWidth(33);
        ccc2.setPercentWidth(67);
        clientChoix.getColumnConstraints().addAll(ccc1, ccc2);
        TextField clientNom = new TextField("Joueur 2");
        TextField ip = new TextField("localhost");

        HBox clientBoutons = new HBox(10);
        Button clientChoixValider = new Button("Connexion");
        Button clientChoixAnnuler = new Button("Retour");
        clientChoixValider.setMaxWidth(Double.MAX_VALUE);
        clientChoixAnnuler.setMaxWidth(Double.MAX_VALUE);
        clientChoixValider.setOnAction(e -> {
            if (ip.getText().length() > 6 && clientNom.getText().length() > 2) {
                sceneJeu.getReseau().d = dialog;
                sceneJeu.getReseau().client(clientNom.getText(), ip.getText());
                titre.setText("Partie en réseau: attente");
                contentWrapper.setCenter(hostAttente);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });
        clientChoixAnnuler.setOnAction(e -> {
            titre.setText("Partie en réseau");
            contentWrapper.setCenter(choixType);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });
        HBox.setHgrow(clientChoixAnnuler, Priority.ALWAYS);
        HBox.setHgrow(clientChoixValider, Priority.ALWAYS);
        clientBoutons.getChildren().addAll(clientChoixAnnuler, clientChoixValider);
        clientBoutons.setPadding(new Insets(10, 0, 0, 0));
        Label clientChoixTitre = new Label("Rejoindre une partie");
        clientChoixTitre.setFont(new Font(null, 18));
        clientChoixTitre.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHalignment(clientChoixTitre, HPos.CENTER);

        clientChoix.add(new Label("IP de l'host"), 0, 0);
        clientChoix.add(ip, 1, 0);
        clientChoix.add(new Label("Nom du joueur"), 0, 1);
        clientChoix.add(clientNom, 1, 1);
        clientChoix.add(clientBoutons, 0, 2, 2, 1);

        Dialogs self = this;
        choixType.setId("reseauChoix");
        choixType.setPadding(new Insets(10));
        Button choixHost = new Button("Créer une partie");
        choixHost.setMaxWidth(Double.MAX_VALUE);
        choixHost.setOnAction(e -> {
            self.route = 1;
            titre.setText("Partie en réseau: créer");
            contentWrapper.setCenter(hostChoix);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });
        Button choixClient = new Button("Rejoindre une partie");
        choixClient.setMaxWidth(Double.MAX_VALUE);
        choixClient.setOnAction(e -> {
            self.route = 2;
            titre.setText("Partie en réseau: rejoindre");
            contentWrapper.setCenter(clientChoix);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });
        Button retourMenu = new Button("Retour au menu");
        retourMenu.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(retourMenu, new Insets(15, 0, 0, 0));
        retourMenu.setOnAction(e -> dialog.close());

        choixType.getChildren().addAll(choixHost, choixClient, retourMenu);

        dialog.setResultConverter(b -> {
            if (
                        sceneJeu.getReseau().getTacheActuelle() == Reseau.Tache.ATTENTE_SERVEUR
                    ||  sceneJeu.getReseau().getTacheActuelle() == Reseau.Tache.ATTENTE_CLIENT
            ) {
                sceneJeu.getReseau().fermerReseau();
            }

            return null;
        });

        titre.getStyleClass().add("titre");
        BorderPane.setAlignment(titre, Pos.CENTER);
        BorderPane.setMargin(titre, new Insets(-10, 0, 12, 0));
        contentWrapper.setTop(titre);
        contentWrapper.setCenter(choixType);
        contentWrapper.setId("dialogReseau");

        dialog.getDialogPane().setContent(contentWrapper);
        dialog.setTitle("Partie en réseau");
        dialog.getDialogPane().getStylesheets().add(Diaballik.class.getResource(Diaballik.CSS_DIALOG).toExternalForm());
        dialog.getDialogPane().setPrefSize(400, 200);
        dialog.showAndWait();
    }
}
