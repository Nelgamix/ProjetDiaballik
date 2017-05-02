package sample.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.controller.TerrainController;
import sample.model.Joueur;
import sample.model.Pion;
import sample.model.Terrain;

import java.util.Observable;
import java.util.Observer;

public class TerrainView extends Pane implements Observer {
    private final TerrainController terrainController;
    private final Terrain terrain;

    private final Rectangle[][] rectangle; // Représentation visuelle du terrain (l'UI)
    private final Rectangle[][] pions; // Les pions

    public TerrainView(TerrainController terrainController) {
        this.terrainController = terrainController;
        this.terrain = terrainController.getTerrain();
        this.terrain.addObserver(this);

        this.rectangle = new Rectangle[Terrain.HAUTEUR][Terrain.LARGEUR];
        this.pions = new Rectangle[Terrain.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        int a = 0;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                final int fi = i;
                final int fj = j;
                this.rectangle[i][j] = new Rectangle(50, 50);
                this.rectangle[i][j].setFill((a++ % 2 == 0 ? Color.BLUE : Color.ORANGE));
                this.rectangle[i][j].setX(50 * i);
                this.rectangle[i][j].setY(50 * j);
                this.rectangle[i][j].setOnMouseClicked(e -> mouseClick(fi, fj));

                this.getChildren().add(this.rectangle[i][j]);
            }
        }

        Color c;
        for (int i = 0; i < Terrain.NOMBRE_JOUEURS; i++) {
            c = (i == 0 ? Color.GREEN : Color.RED);
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                this.pions[i][j] = new Rectangle(25, 25);
                this.pions[i][j].setFill(c);

                this.getChildren().add(this.pions[i][j]);
            }
        }

        update(null, null);
    }

    public void update(Observable observable, Object o) {
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                Pion p = this.terrain.cases[i][j].getPion();
                if (p != null) this.placerPion(p, i, j);
            }
        }
    }

    private void mouseClick(int i, int j) {
        System.out.println("Click on " + i + " " + j);
    }

    public void placerPion(Pion p, int x, int y) {
        this.pions[p.getCouleur()][p.getNumero()].setX(12 + x * 50);
        this.pions[p.getCouleur()][p.getNumero()].setY(12 + y * 50);
    }

}
