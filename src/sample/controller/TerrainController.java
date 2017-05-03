package sample.controller;

import sample.model.*;
import sample.view.TerrainView;

public class TerrainController {
    private final Jeu jeu;
    private final TerrainView terrainView;
    public Case caseSelectionne;

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

    public void mouseClicked(Point point) {
        Case caseCliquee = this.getJeu().getTerrain().getCaseAt(point);
        if (this.caseSelectionne == null) {
            // pas de pion encore sélectionné, la case qu'on a sélectionné en contient un
            if (caseCliquee.getPion() != null) {
                this.caseSelectionne = caseCliquee;
                this.caseSelectionne.getPion().setSelectionne(true);
                System.out.println("Pion sélectionné");
            } else {
                System.out.println("Case vide");
            }
        } else {
            if (caseCliquee.getPion() != null) {
                System.out.println("Case déjà occupée");
            } else {
                System.out.println("Déplacement");
                this.caseSelectionne.getPion().setSelectionne(false);
                this.jeu.deplacement(this.caseSelectionne.getPion(), caseCliquee);
                //this.caseSelectionne.getPion().deplacer(caseCliquee);
                this.caseSelectionne = null;
            }
        }
    }
}
