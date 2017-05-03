package sample.controller;

import sample.model.Jeu;
import sample.model.Pion;
import sample.model.Point;
import sample.model.Terrain;
import sample.view.TerrainView;

public class TerrainController {
    private final Jeu jeu;
    private final TerrainView terrainView;
    private Pion pionSelected;

    public TerrainController(Jeu jeu) {
        this.jeu = jeu;
        this.terrainView = new TerrainView(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public TerrainView getTerrainView() {
        return terrainView;
    }

    public void mouseClick(int i, int j) {
        System.out.println("Click on " + i + " " + j);
    }

    public void setPionSelected(Pion pion) {
        this.pionSelected = pion;
    }

    public void caseClicked(Point point) {
        if (this.pionSelected == null) {
            System.out.println("Pas de pion sélectionné");
            return;
        }

        if (this.jeu.getTerrain().getCase(point).getPion() == null) {
            System.out.println("Changement de coordonnées");

            this.jeu.deplacerPion(pionSelected, point);
            this.setPionSelected(null);
        } else {
            System.out.println("Pion déjà présent");
        }
    }
}
