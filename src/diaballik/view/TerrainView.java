package diaballik.view;

import diaballik.controller.TerrainController;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Point;
import diaballik.model.Terrain;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;

public class TerrainView extends Pane implements Observer {
    private final TerrainController terrainController;
    private final Terrain terrain;

    private final CaseView[][] cases; // Représentation visuelle du terrain (l'UI)
    private final PionView[][] pions;

    public TerrainView(TerrainController terrainController) {
        super();

        this.terrainController = terrainController;
        this.terrain = terrainController.getJeu().getTerrain();
        terrainController.getJeu().addObserver(this);
        this.setId("terrainView");
        this.setMaxWidth(CaseView.LARGEUR * Terrain.LARGEUR + 4);
        this.setMaxHeight(CaseView.HAUTEUR * Terrain.HAUTEUR + 4);

        this.cases = new CaseView[Terrain.HAUTEUR][Terrain.LARGEUR];
        this.pions = new PionView[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        int a = 0;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                CaseView cv = new CaseView(this, terrain.getCaseAt(new Point(i, j)));

                if (a++ % 2 == 0)
                    cv.getStyleClass().add("couleurCasePair");
                else
                    cv.getStyleClass().add("couleurCaseImpair");

                this.cases[i][j] = cv;

                this.getChildren().add(cv);
            }
        }

        PionView pv;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                pv = new PionView(this, this.terrain.getPionOf(i, j));
                this.pions[i][j] = pv;
                this.getChildren().add(pv);
            }
        }

        update(null, null);
    }

    public CaseView getCaseAt(Point p) {
        return cases[p.getY()][p.getX()];
    }

    public TerrainController getTerrainController() {
        return terrainController;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (terrainController.getJeu().getJoueurActuel().getCouleur() == Joueur.JOUEUR_VERT) {
            for (PionView p : pions[Joueur.JOUEUR_ROUGE]) p.disable();
            for (PionView p : pions[Joueur.JOUEUR_VERT]) p.enable();
        } else {
            for (PionView p : pions[Joueur.JOUEUR_ROUGE]) p.enable();
            for (PionView p : pions[Joueur.JOUEUR_VERT]) p.disable();
        }
    }
}
