package diaballik.vue;

import diaballik.Diaballik;
import diaballik.controleur.TerrainControleur;
import diaballik.model.*;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Observable;
import java.util.Observer;

public class TerrainVue extends StackPane implements Observer {
    private final TerrainControleur terrainControleur;

    private final FadeTransition ft;

    private final CaseVue[][] cases; // Représentation visuelle du terrain (l'UI)
    private final PionVue[][] pions;

    private final StackPane tourAdverse;
    private boolean tourAdverseVisible;

    public TerrainVue(TerrainControleur terrainControleur) {
        super();

        GridPane root = new GridPane();

        // setup le fond quand le joueur adverse jouera
        tourAdverseVisible = false;
        tourAdverse = new StackPane();

        Label l = new Label();
        if (terrainControleur.getJeu().getConfigurationPartie().estMultijoueur())
            l.setText("Tour du joueur adverse");
        else
            l.setText("Tour de l'IA");

        StackPane.setAlignment(l, Pos.CENTER);
        tourAdverse.setId("tourAdverse");
        tourAdverse.getChildren().add(l);

        ft = new FadeTransition(Duration.millis(400));
        ft.setNode(tourAdverse);

        this.terrainControleur = terrainControleur;
        Terrain terrain = terrainControleur.getJeu().getTerrain();
        terrainControleur.getJeu().addObserver(this);
        root.setId("terrainView");
        root.setPrefSize(Terrain.LARGEUR * 80, Terrain.LARGEUR * 80);

        this.cases = new CaseVue[Terrain.HAUTEUR][Terrain.LARGEUR];
        this.pions = new PionVue[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        ColumnConstraints cc[] = new ColumnConstraints[Terrain.LARGEUR];
        RowConstraints rc[] = new RowConstraints[Terrain.HAUTEUR];

        ColumnConstraints c;
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            c = new ColumnConstraints();
            c.setPercentWidth(100*((double)1/Terrain.LARGEUR));
            cc[i] = c;
        }

        RowConstraints r;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            r = new RowConstraints();
            r.setPercentHeight(100*((double)1/Terrain.HAUTEUR));
            rc[i] = r;
        }

        root.getColumnConstraints().addAll(cc);
        root.getRowConstraints().addAll(rc);

        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                CaseVue cv = new CaseVue(this, terrain.getCaseSur(new Point(j, i)));
                this.cases[i][j] = cv;
                root.add(cv, j, i);
            }
        }

        PionVue pv;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                pv = new PionVue(this, terrain.getPionDe(i, j));
                this.pions[i][j] = pv;
            }
        }

        this.getChildren().addAll(tourAdverse, root);

        update(null, SignalUpdate.INIT);
    }

    public CaseVue getCaseSur(Point p) {
        return cases[p.getY()][p.getX()];
    }

    TerrainControleur getTerrainControleur() {
        return terrainControleur;
    }

    PionVue getPionVueBalle(int couleur) {
        for (PionVue p : this.pions[couleur]) {
            if (p.aLaBalle()) return p;
        }

        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        preparerTour();

        Diaballik d = getTerrainControleur().sceneJeu.getDiaballik();
        if (d.getSceneJeu() != null) {
            switch ((SignalUpdate)arg) {
                case TOUR:
                    d.setCurseurNormal();
                    terrainControleur.finSelection();
                    break;
                case GLOBAL:
                    terrainControleur.finSelection();
                    break;
                default:
                    break;
            }
        }
    }

    private void preparerTour() {
        Joueur jt = terrainControleur.getJeu().getJoueurActuel();
        if (jt.estUnJoueurReseau() || jt.estUneIA()) {
            fade(true);
            for (PionVue p : pions[Joueur.ROUGE]) p.desactiver();
            for (PionVue p : pions[Joueur.VERT]) p.desactiver();
        } else {
            fade(false);
            if (jt.getCouleur() == Joueur.VERT) {
                for (PionVue p : pions[Joueur.ROUGE]) p.desactiver();
                for (PionVue p : pions[Joueur.VERT]) p.activer();
            } else {
                for (PionVue p : pions[Joueur.ROUGE]) p.activer();
                for (PionVue p : pions[Joueur.VERT]) p.desactiver();
            }
        }
    }

    private void fade(boolean in) {
        if (in && !tourAdverseVisible) {
            tourAdverse.setOpacity(0);
            tourAdverse.toFront();
            ft.setToValue(1);
            ft.setOnFinished(e -> terrainControleur.setAnimationEnCours(false));

            terrainControleur.setAnimationEnCours(true);
            ft.play();

            tourAdverseVisible = true;
        } else if (!in && tourAdverseVisible) {
            ft.setToValue(0);
            ft.setOnFinished(e -> {
                tourAdverse.toBack();
                terrainControleur.setAnimationEnCours(false);
            });

            terrainControleur.setAnimationEnCours(true);
            ft.play();

            tourAdverseVisible = false;
        }
    }
}
