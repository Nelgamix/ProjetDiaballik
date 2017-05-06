package diaballik.controller;

import diaballik.Diaballik;
import diaballik.model.Case;
import diaballik.model.Jeu;
import diaballik.model.Pion;
import diaballik.model.Point;
import diaballik.view.TerrainView;

import java.util.ArrayList;

public class TerrainController {
    public final Diaballik diaballik;

    private final Jeu jeu;
    private final TerrainView terrainView;
    private Case caseSelectionne;
    private ArrayList<Case> casesMarquees = new ArrayList<>();
    private ArrayList<Pion> pionsMarques = new ArrayList<>();

    public TerrainController(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.terrainView = new TerrainView(this);
    }

    public boolean caseEstMarquee(Case c) {
        return casesMarquees.contains(c);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public TerrainView getTerrainView() {
        return terrainView;
    }

    // TODO: rework ça
    public void mouseClicked(Point point) {
        Case caseCliquee = this.getJeu().getTerrain().getCaseAt(point);
        if (this.caseSelectionne == null) {
            if (caseCliquee.getPion() != null && caseCliquee.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) {
                this.caseSelectionne = caseCliquee;
                this.caseSelectionne.getPion().setSelectionne(true);
                marquerAll(caseCliquee);
            }
        } else {
            if (caseCliquee.getPion() != null) {
                if (caseCliquee.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) {
                    if (caseCliquee == this.caseSelectionne) {
                        this.caseSelectionne.getPion().setSelectionne(false);
                        finSelection();
                    } else if (this.caseSelectionne.getPion().aLaBalle()) {
                        this.caseSelectionne.getPion().setSelectionne(false);
                        this.jeu.passe(this.caseSelectionne.getPion(), caseCliquee);
                        finSelection();
                    } else {
                        this.caseSelectionne.getPion().setSelectionne(false);
                        finSelection();
                        this.caseSelectionne = caseCliquee;
                        this.caseSelectionne.getPion().setSelectionne(true);
                        marquerAll(caseCliquee);
                    }
                }
            } else {
                this.caseSelectionne.getPion().setSelectionne(false);
                this.jeu.deplacement(this.caseSelectionne.getPion(), caseCliquee);
                finSelection();
            }
        }
    }

    private void marquerAll(Case caseCliquee) {
        if (!caseCliquee.getPion().aLaBalle()) {
            casesMarquees = jeu.getDeplacementsPossibles(caseCliquee.getPion());
            for (Case c : casesMarquees) c.setMarque(true);
        } else {
            pionsMarques = jeu.getPassesPossibles(caseCliquee.getPion());
            for (Pion p : pionsMarques) p.setMarque(true);
        }
    }

    private void finSelection() {
        this.caseSelectionne = null;
        for (Case c : casesMarquees) c.setMarque(false);
        for (Pion p : pionsMarques) p.setMarque(false);
        casesMarquees.clear();
        pionsMarques.clear();
    }
}
