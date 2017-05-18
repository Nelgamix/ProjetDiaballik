package diaballik.scene;

import diaballik.Diaballik;
import diaballik.Reseau;
import diaballik.controleur.ActionsControleur;
import diaballik.controleur.AffichageControleur;
import diaballik.controleur.TerrainControleur;
import diaballik.model.*;
import diaballik.vue.CaseVue;
import diaballik.vue.Dialogs;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

public class SceneJeu {
    private final Diaballik diaballik;

    private Scene scene;
    private Jeu jeu;

    private Reseau reseau;

    private AffichageControleur affichageControleur;
    private ActionsControleur actionsControleur;
    private TerrainControleur terrainControleur;

    private final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN); // sauvegarde
    private final KeyCombination ctrlZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN); // actionDefaire
    private final KeyCombination ctrlShiftZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN); // actionRefaire

    public SceneJeu(Diaballik diaballik) {
        this.diaballik = diaballik;
    }

    public void retourMenu() {
        diaballik.showSceneMenu();
    }

    public void initShow(ConfigurationPartie cp) {
        try {
            jeu = new Jeu(this, cp);
        } catch (OutdatedSave os) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Sauvegarde non conforme.");
            a.showAndWait();
            return;
        } catch (IOException ioe) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Erreur de lecture du fichier.");
            a.showAndWait();
            return;
        }

        terrainControleur = new TerrainControleur(this);
        actionsControleur = new ActionsControleur(this);
        affichageControleur = new AffichageControleur(this);
        BorderPane root = new BorderPane();

        root.setCenter(terrainControleur.getTerrainVue());
        root.setRight(actionsControleur.getActionsVue());
        root.setTop(affichageControleur.getAffichageVue());

        BorderPane.setMargin(terrainControleur.getTerrainVue(), new Insets(10));

        scene = new Scene(root, CaseVue.LARGEUR * Terrain.LARGEUR + 225, CaseVue.HAUTEUR * Terrain.HAUTEUR + 75);
        scene.setOnKeyPressed(k -> {
            if (ctrlS.match(k)) {
                actionsControleur.getActionsVue().montrerPopupSauvegarde();
            } else if (ctrlZ.match(k)) {
                actionsControleur.actionDefaire();
            } else if (ctrlShiftZ.match(k)) {
                actionsControleur.actionRefaire();
            } else if (k.getCode() == KeyCode.SPACE) {
                actionsControleur.actionFinTour();
            }
        });
        scene.getStylesheets().add(getClass().getResource(Diaballik.CSS_JEU).toExternalForm());

        diaballik.showSceneJeu();

        affichageControleur.getAffichageVue().update(null, SignalUpdate.INIT_DONE);
    }

    public void dialogNouveauJeu() {
        Optional<ConfigurationPartie> cp = Dialogs.montrerDialogNouvellePartie(getJeu() != null ? getJeu().getConfigurationPartie() : null);
        cp.ifPresent(this::nouveauJeu);
    }
    public void nouveauJeu(ConfigurationPartie cp) {
        initShow(cp);
    }

    public void dialogChargerJeu() {
        Optional<String> ofilename = Dialogs.montrerDialogChoisirFichier(Diaballik.DOSSIER_SAUVEGARDES);
        if (ofilename != null)
            ofilename.ifPresent(s -> chargerJeu(Diaballik.DOSSIER_SAUVEGARDES + "/" + s));
    }
    public void chargerJeu(String fichier) {
        ConfigurationPartie cp = new ConfigurationPartie(fichier, true);
        initShow(cp);
    }

    public void dialogNouveauJeuReseau() {
        if (reseau == null)
            reseau = new Reseau(this);
        else if (reseau.isRunning()) {
            System.out.println("Le réseau tourne déjà.");
            return;
        }

        Dialogs.montrerReseau(this);
    }
    public void nouveauJeuReseau(int numJoueur, String nomJoueur1, String nomJoueur2, String terrain) {
        ConfigurationPartie cp = new ConfigurationPartie(numJoueur, nomJoueur1, nomJoueur2, terrain);
        initShow(cp);
    }

    public void finJeu(Joueur gagnant, int victoireType) {
        stopReseau();

        Dialogs.montrerFinJeu(gagnant, victoireType);

        retourMenu();
    }

    public void stopReseau() {
        if (reseau != null && reseau.isRunning())
            reseau.fermerReseau();
    }

    public Scene getScene() {
        return scene;
    }
    public Jeu getJeu() {
        return jeu;
    }

    public AffichageControleur getAffichageControleur() {
        return affichageControleur;
    }
    public ActionsControleur getActionsControleur() {
        return actionsControleur;
    }
    public TerrainControleur getTerrainControleur() {
        return terrainControleur;
    }

    public Diaballik getDiaballik() {
        return diaballik;
    }
    public Reseau getReseau() {
        return reseau;
    }
}
