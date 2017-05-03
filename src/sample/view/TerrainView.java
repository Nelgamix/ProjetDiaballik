package sample.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.controller.TerrainController;
import sample.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class TerrainView extends Pane implements Observer {
    private final TerrainController terrainController;
    private final Terrain terrain;

    private final ArrayList<CaseView> cases = new ArrayList<>(); // Représentation visuelle du terrain (l'UI)
    private final ArrayList<PionView> pions = new ArrayList<>(); // Les pions

    public TerrainView(TerrainController terrainController) {
        this.terrainController = terrainController;
        this.terrain = terrainController.getJeu().getTerrain();
        this.terrainController.getJeu().addObserver(this);

        int a = 0;
        CaseView c;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                c = new CaseView(this.terrainController, new Point(i, j), (a++ % 2 == 0 ? Color.BLUE : Color.ORANGE));
                this.cases.add(c);

                this.getChildren().add(c);
            }
        }

        PionView p;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                p = new PionView(this.terrainController, this.terrainController.getJeu().joueurs[i].getPion(j), null);
                this.pions.add(p);

                this.getChildren().add(p);
            }
        }

        update(null, null);
    }

    public void update(Observable observable, Object o) {
        Point point;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                point = new Point(i, j);
                Pion pion = this.terrain.getCase(point).getPion();
                if (pion != null) this.placerPion(pion, point);
            }
        }
    }

    public void placerPion(Pion pion, Point point) {
        PionView pv = getPionView(pion.getCouleur(), pion.getNumero());
        pv.setX(12 + point.getX() * 50);
        pv.setY(12 + point.getY() * 50);
    }

    public CaseView getCaseView(Point point) {
        return this.cases.get(point.getX() * Terrain.HAUTEUR + point.getY());
    }

    public PionView getPionView(int couleur, int numero) {
        return this.pions.get(couleur * Jeu.NOMBRE_JOUEURS + numero);
    }

}
