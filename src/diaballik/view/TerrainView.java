package diaballik.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import diaballik.controller.TerrainController;
import diaballik.model.*;

import java.util.Observable;
import java.util.Observer;

public class TerrainView extends Pane implements Observer {
    private final TerrainController terrainController;
    private final Terrain terrain;

    private final CaseView[][] cases; // Représentation visuelle du terrain (l'UI)
    private final PionView[][] pions;

    public TerrainView(TerrainController terrainController) {
        this.terrainController = terrainController;
        this.terrain = terrainController.getJeu().getTerrain();
        this.terrainController.getJeu().addObserver(this);

        this.cases = new CaseView[Terrain.HAUTEUR][Terrain.LARGEUR];
        this.pions = new PionView[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        int a = 0;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                CaseView cv = new CaseView(this.terrainController, new Point(i, j), (a++ % 2 == 0 ? Color.BEIGE : Color.GRAY));
                this.cases[i][j] = cv;

                this.getChildren().add(cv);
            }
        }

        PionView pv;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                pv = new PionView(this.terrainController, this.terrain.getPionAt(new Point(i, j)));
                this.pions[i][j] = pv;
                this.getChildren().add(pv);
            }
        }

        update();
    }

    public void update() {
        this.update(null, null);
    }

    public void update(Observable observable, Object o) {

    }

}
