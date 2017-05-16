package diaballik.vue;

import diaballik.Diaballik;
import diaballik.Reseau;
import diaballik.Utils;
import diaballik.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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
        //boutonJouer.setDisable(true);

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
        //ccJoueur.setHalignment(HPos.CENTER);
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
        /*l = new Label("JoueurIA");
        l.getStyleClass().add("dialogNewGameHeader");
        configJoueurs.add(l, 2, 0);*/

        // Row joueur 1
        TextField nomJoueur1 = new TextField("Joueur 1");
        nomJoueur1.setPromptText("Nom");
        nomJoueur1.textProperty().addListener((o, ov, nv) -> {
            boutonJouer.setDisable(nv.trim().length() < 3);
        });
        ComboBox<String> iaJoueur1 = new ComboBox<>(iaDifficultes);
        iaJoueur1.getSelectionModel().select(0);
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
        nomJoueur2.textProperty().addListener((o, ov, nv) -> {
            boutonJouer.setDisable(nv.trim().length() < 3);
        });
        ComboBox<String> iaJoueur2 = new ComboBox<>(iaDifficultes);
        iaJoueur2.getSelectionModel().select(0);
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

        // autre
        // row noms aléatoires
        /*Label nomsAleatoires = new Label("Noms aléatoires");
        autre.add(nomsAleatoires, 0, 1);
        CheckBox checkNomsAleatoires = new CheckBox();
        checkNomsAleatoires.setDisable(true);
        checkNomsAleatoires.selectedProperty().addListener((o, ov, nv) -> {
            nomJoueur1.setDisable(nv);
            nomJoueur2.setDisable(nv);
            boutonJouer.setDisable(!nv);
        });
        autre.add(checkNomsAleatoires, 1, 1);*/

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
                //if (checkNomsAleatoires.isSelected()) {
                    //return new ConfigurationPartie(ia1, ia2, terrains.getTerrainSelectionnePath());
                //} else {
                    if (!Utils.nomValide(nomJoueur1.getText()) || !Utils.nomValide(nomJoueur2.getText()))
                        return null;
                    else
                        return new ConfigurationPartie(nomJoueur1.getText(), nomJoueur2.getText(), ia1, ia2, terrains.getTerrainSelectionnePath());
                //}
            }

            return null;
        });

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
        ObservableList<String> obs = FXCollections.observableArrayList(Utils.getFichiersDansDossier(directory, Diaballik.EXTENSION_SAUVEGARDE, false));

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Charger une partie");

        ButtonType boutonOuvrirType = new ButtonType("Ouvrir", ButtonBar.ButtonData.OK_DONE);
        ButtonType boutonAnnulerType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonOuvrirType, boutonAnnulerType);
        Node boutonJouer = dialog.getDialogPane().lookupButton(boutonOuvrirType);
        Node boutonAnnuler = dialog.getDialogPane().lookupButton(boutonAnnulerType);
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
        //infosSave.setPadding(new Insets(0, 10, 0, 10));
        ColumnConstraints ccis = new ColumnConstraints();
        ccis.setPercentWidth(40);
        infosSave.getColumnConstraints().addAll(ccis);

        Label labelInfoTour = new Label(),
                labelInfoNomJoueur1 = new Label(),
                labelInfoNomJoueur2 = new Label();
        TerrainApercu ta = new TerrainApercu();
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
        ListView<String> filesView = new ListView<>(obs);
        filesView.setMaxWidth(Double.MAX_VALUE);
        filesView.setMaxHeight(Double.MAX_VALUE);
        filesView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                dialog.setResult(filesView.getSelectionModel().getSelectedItem() + Diaballik.EXTENSION_SAUVEGARDE);
                dialog.close();
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

        // setup content
        content.setId("dialogLoadName");
        content.add(new Label("Sauvegardes"), 0, 0);
        content.add(filesView, 0, 1);
        content.add(new Label("Sauvegarde sélectionnée"), 1, 0);
        content.add(infosSave, 1, 1);
        //content.setPadding(new Insets(10));

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

    public static void montrerParametres(ConfigurationPartie cp) {
        Dialog<Void> parametres = new Dialog<>();
        parametres.setTitle("Paramètres");

        StackPane content = new StackPane();
        content.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(85);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(15);
        grid.getColumnConstraints().addAll(cc1, cc2);

        CheckBox parametre1 = new CheckBox();
        parametre1.setSelected(cp.isAideDeplacement());

        CheckBox parametre2 = new CheckBox();
        parametre2.setSelected(cp.isAidePasse());

        CheckBox parametre3 = new CheckBox();
        parametre3.setSelected(cp.isAutoSelectionPion());

        Label labelParametre1 = new Label("Aide au déplacement des pions");
        Label labelParametre2 = new Label("Aide aux passes");
        Label labelParametre3 = new Label("Auto sélectionner le meme pion\nsi déplacement restant");
        labelParametre3.setWrapText(true);
        labelParametre3.setMaxHeight(Double.MAX_VALUE);

        grid.add(labelParametre1, 0, 0);
        grid.add(parametre1, 1, 0);
        grid.add(labelParametre2, 0, 1);
        grid.add(parametre2, 1, 1);
        grid.add(labelParametre3, 0, 2);
        grid.add(parametre3, 1, 2);

        content.getChildren().add(grid);

        parametres.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        parametres.getDialogPane().setContent(content);
        parametres.getDialogPane().setPrefSize(320, 160);

        parametres.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                cp.setAideDeplacement(parametre1.isSelected());
                cp.setAidePasse(parametre2.isSelected());
                cp.setAutoSelectionPion(parametre3.isSelected());

                cp.writeProperties();
            }

            return null;
        });

        parametres.showAndWait();
    }

    public static void montrerReseau(Diaballik diaballik) {
        Dialogs d = new Dialogs();
        d.getReseau(diaballik);
    }

    private void getReseau(Diaballik diaballik) {
        Dialog<Void> dialog = new Dialog<>();
        VBox choixType = new VBox(12);

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
        Label hostLabel = new Label("Adresse IP (locale): " + adresseLocale + "\nAdresse IP (externe): " + adresseExterne);
        BorderPane.setAlignment(hostAttente, Pos.CENTER);
        Button quitter = new Button("Annuler");
        quitter.setMaxWidth(Double.MAX_VALUE);
        quitter.setOnAction(e -> dialog.close());
        hostAttente.setCenter(hostLabel);
        hostAttente.setBottom(quitter);

        GridPane hostChoix = new GridPane();
        hostChoix.setHgap(14);
        hostChoix.setVgap(10);
        ColumnConstraints hcc1 = new ColumnConstraints();
        ColumnConstraints hcc2 = new ColumnConstraints();
        hcc1.setPercentWidth(33);
        hcc2.setPercentWidth(67);
        hostChoix.getColumnConstraints().addAll(hcc1, hcc2);
        //hostChoix.setPadding(new Insets(16, 32, 16, 32));
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
                diaballik.reseau.d = dialog;
                diaballik.reseau.host(hostNom.getText(), terrains.getTerrainSelectionnePath());
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
        //hostChoix.getChildren().addAll(new Label("Choix de l'host"), serveurNom, terrains, serveurChoixValider);
        Label hostChoixTitre = new Label("Créer une partie");
        hostChoixTitre.setFont(new Font(null, 18));
        hostChoixTitre.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHalignment(hostChoixTitre, HPos.CENTER);

        //hostChoix.add(hostChoixTitre, 0, 0, 2, 1);
        hostChoix.add(new Label("Nom du joueur"), 0, 0);
        hostChoix.add(hostNom, 1, 0);
        hostChoix.add(new Label("Terrain"), 0, 1);
        hostChoix.add(terrains, 1, 1);
        hostChoix.add(hostBoutons, 0, 2, 2, 1);

        GridPane clientChoix = new GridPane();
        clientChoix.setHgap(14);
        clientChoix.setVgap(10);
        ColumnConstraints ccc1 = new ColumnConstraints();
        ColumnConstraints ccc2 = new ColumnConstraints();
        ccc1.setPercentWidth(33);
        ccc2.setPercentWidth(67);
        clientChoix.getColumnConstraints().addAll(ccc1, ccc2);
        //clientChoix.setPadding(new Insets(16, 32, 16, 32));
        TextField clientNom = new TextField("Joueur 2");
        TextField ip = new TextField("localhost");

        HBox clientBoutons = new HBox(10);
        Button clientChoixValider = new Button("Connexion");
        Button clientChoixAnnuler = new Button("Retour");
        clientChoixValider.setMaxWidth(Double.MAX_VALUE);
        clientChoixAnnuler.setMaxWidth(Double.MAX_VALUE);
        clientChoixValider.setOnAction(e -> {
            if (ip.getText().length() > 6 && clientNom.getText().length() > 2) {
                diaballik.reseau.d = dialog;
                diaballik.reseau.client(clientNom.getText(), ip.getText());
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
        //clientChoix.getChildren().addAll(new Label("Choix de l'host"), ip, clientNom, clientChoixValider);
        Label clientChoixTitre = new Label("Rejoindre une partie");
        clientChoixTitre.setFont(new Font(null, 18));
        clientChoixTitre.setPadding(new Insets(0, 0, 10, 0));
        GridPane.setHalignment(clientChoixTitre, HPos.CENTER);

        //clientChoix.add(clientChoixTitre, 0, 0, 2, 1);
        clientChoix.add(new Label("IP de l'host"), 0, 0);
        clientChoix.add(ip, 1, 0);
        clientChoix.add(new Label("Nom du joueur"), 0, 1);
        clientChoix.add(clientNom, 1, 1);
        clientChoix.add(clientBoutons, 0, 2, 2, 1);

        choixType.setId("reseauChoix");
        choixType.setPadding(new Insets(10));
        Button choixHost = new Button("Créer une partie");
        choixHost.setMaxWidth(Double.MAX_VALUE);
        choixHost.setOnAction(e -> {
            titre.setText("Partie en réseau: créer");
            contentWrapper.setCenter(hostChoix);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });
        Button choixClient = new Button("Rejoindre une partie");
        choixClient.setMaxWidth(Double.MAX_VALUE);
        choixClient.setOnAction(e -> {
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
                        diaballik.reseau.getTacheActuelle() == Reseau.Tache.ATTENTE_SERVEUR
                    ||  diaballik.reseau.getTacheActuelle() == Reseau.Tache.ATTENTE_CLIENT
            ) {
                diaballik.reseau.fermerReseau();
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
        //dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialog.showAndWait();
    }
}
