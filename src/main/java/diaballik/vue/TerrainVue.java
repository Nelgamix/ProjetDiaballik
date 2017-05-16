package diaballik.vue;

import diaballik.Diaballik;
import diaballik.controleur.TerrainControleur;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Point;
import diaballik.model.Terrain;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Observable;
import java.util.Observer;

public class TerrainVue extends StackPane implements Observer {
    private final TerrainControleur terrainControleur;
    private final Terrain terrain;

    private final FadeTransition ft;

    private final CaseVue[][] cases; // Représentation visuelle du terrain (l'UI)
    private final PionVue[][] pions;

    private final StackPane tourAdverseReseau, tourAdverseIA;

    public TerrainVue(TerrainControleur terrainControleur) {
        super();

        ft = new FadeTransition(Duration.millis(400));

        GridPane root = new GridPane();

        // setup le fond quand le joueur adverse jouera
        tourAdverseReseau = new StackPane();
        tourAdverseReseau.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.15), CornerRadii.EMPTY, Insets.EMPTY)));
        Label l = new Label("Tour du joueur adverse");
        l.setFont(new Font("Open Sans", 26));
        StackPane.setAlignment(l, Pos.CENTER);
        tourAdverseReseau.getChildren().add(l);

        tourAdverseIA = new StackPane();
        tourAdverseIA.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.15), CornerRadii.EMPTY, Insets.EMPTY)));
        Label lia = new Label("Tour de l'IA");
        lia.setFont(new Font("Open Sans", 26));
        StackPane.setAlignment(lia, Pos.CENTER);
        tourAdverseIA.getChildren().add(lia);

        this.terrainControleur = terrainControleur;
        this.terrain = terrainControleur.getJeu().getTerrain();
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
                pv = new PionVue(this, this.terrain.getPionDe(i, j));
                this.pions[i][j] = pv;
            }
        }

        this.getChildren().add(root);

        update(null, null);
    }

    public CaseVue getCaseSur(Point p) {
        return cases[p.getY()][p.getX()];
    }

    public TerrainControleur getTerrainControleur() {
        return terrainControleur;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (terrainControleur.getJeu().joueurActuelReseau() || terrainControleur.getJeu().getJoueurActuel().estUneIA()) {
            for (PionVue p : pions[Joueur.ROUGE]) p.desactiver();
            for (PionVue p : pions[Joueur.VERT]) p.desactiver();
            if (terrainControleur.getJeu().joueurActuelReseau()) fade(true, tourAdverseReseau);
            if (terrainControleur.getJeu().getJoueurActuel().estUneIA()) fade(true, tourAdverseIA);
        } else {
            fade(false, tourAdverseReseau);
            fade(false, tourAdverseIA);
            if (terrainControleur.getJeu().getJoueurActuel().getCouleur() == Joueur.VERT) {
                for (PionVue p : pions[Joueur.ROUGE]) p.desactiver();
                for (PionVue p : pions[Joueur.VERT]) p.activer();
            } else {
                for (PionVue p : pions[Joueur.ROUGE]) p.activer();
                for (PionVue p : pions[Joueur.VERT]) p.desactiver();
            }
        }

        int changed_type = (arg != null ? (int)arg : 0);
        Diaballik d = getTerrainControleur().diaballik;
        if (d.getSceneJeu() != null) {
            switch (changed_type) {
                case Jeu.CHANGEMENT_TOUR:
                    d.setCurseurNormal(d.getSceneJeu());
                    terrainControleur.finSelection();
                    break;
                case Jeu.CHANGEMENT_GLOBAL:
                    terrainControleur.finSelection();
                    break;
                default:
                    break;
            }
        }
    }

    private void fade(boolean in, StackPane s) {
        if (in && !this.getChildren().contains(s)) {
            s.setOpacity(0);
            getChildren().add(s);

            ft.setNode(s);
            ft.setToValue(1);
            ft.setOnFinished(e -> s.setOpacity(1));

            ft.play();
        } else if (!in && this.getChildren().contains(s)) {
            ft.setNode(s);
            ft.setToValue(0);
            ft.setOnFinished(e -> getChildren().remove(s));

            ft.play();
        }
    }
}
