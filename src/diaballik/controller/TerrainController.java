package diaballik.controller;

import diaballik.Diaballik;
import diaballik.model.Case;
import diaballik.model.Jeu;
import diaballik.model.Point;
import diaballik.view.TerrainView;

public class TerrainController {
    private final Diaballik diaballik;

    private final Jeu jeu;
    private final TerrainView terrainView;
    public Case caseSelectionne;

    public TerrainController(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
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
            if (caseCliquee.getPion() != null && caseCliquee.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) {
                this.caseSelectionne = caseCliquee;
                this.caseSelectionne.getPion().setSelectionne(true);
            }
        } else {
            if (caseCliquee.getPion() != null) {
                if (caseCliquee.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) {
                    if (caseCliquee == this.caseSelectionne) {
                        this.caseSelectionne.getPion().setSelectionne(false);
                        this.caseSelectionne = null;
                        return;
                    }

                    this.caseSelectionne.getPion().setSelectionne(false);
                    this.jeu.passe(this.caseSelectionne.getPion(), caseCliquee);
                    this.caseSelectionne = null;
                }
            } else {
                this.caseSelectionne.getPion().setSelectionne(false);
                this.jeu.deplacement(this.caseSelectionne.getPion(), caseCliquee);
                this.caseSelectionne = null;
            }
        }
    }
}
