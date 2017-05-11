package diaballik.controleur;

import diaballik.Diaballik;
import diaballik.model.Case;
import diaballik.model.Jeu;
import diaballik.model.Pion;
import diaballik.vue.CaseVue;
import diaballik.vue.PionVue;
import diaballik.vue.TerrainVue;

import java.util.ArrayList;

public class TerrainControleur {
    public final Diaballik diaballik;

    private final Jeu jeu;
    private final TerrainVue terrainVue;

    private PionVue pionSelectionne;
    private boolean modeActionDeplacement = false; // false si le mode est un deplacement, true si c'est une passe

    private ArrayList<CaseVue> casesMarquees = new ArrayList<>();
    private ArrayList<PionVue> pionsMarques = new ArrayList<>();

    public TerrainControleur(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.terrainVue = new TerrainVue(this);
    }

    public boolean caseEstMarquee(CaseVue c) {
        return casesMarquees.contains(c);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public TerrainVue getTerrainVue() {
        return terrainVue;
    }

    public void clicSouris(CaseVue caseCliquee) {
        PionVue pionVueCorrespondant = caseCliquee.getPionVue();

        if (pionSelectionne == null) { // Si on avait pas mémorisé de pion déjà sélectionnée
            if (pionVueCorrespondant != null) { // il y a un pion sur la case
                if (pionVueCorrespondant.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) { // pion de la bonne couleur
                    modeActionDeplacement = pionVueCorrespondant.getPion().aLaBalle(); // on set le bon mode
                    selectionPion(pionVueCorrespondant); // on le sélectionne
                }
            }
        } else { // sinon
            if (caseCliquee == this.pionSelectionne.getCaseVue()) { // l'utilisateur a cliqué sur la même case
                finSelection();
            } else if (modeActionDeplacement) { // si on attend une passe, il faut un pion
                if (pionVueCorrespondant != null) {
                    if (pionVueCorrespondant.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) { // de la bonne couleur
                        this.jeu.passe(pionSelectionne.getPion(), pionVueCorrespondant.getPion());
                    }
                }

                finSelection();
            } else { // on attend un déplacement, donc vers une case
                if (pionVueCorrespondant != null) {
                    if (pionVueCorrespondant.getPion().getCouleur() == this.jeu.getJoueurActuel().getCouleur()) {
                        finSelection();
                        modeActionDeplacement = pionVueCorrespondant.getPion().aLaBalle(); // on set le bon mode
                        selectionPion(pionVueCorrespondant); // on le sélectionne
                    }
                } else {
                    pionVueCorrespondant = this.pionSelectionne;
                    if (this.jeu.deplacement(pionSelectionne.getPion(), caseCliquee.getCase())) { // on tente le déplacement
                        // si on a réussi
                        if (jeu.getJoueurActuel().getDeplacementsRestants() > 0 && jeu.cp.isAutoSelectionPion()) {
                            finSelection();
                            selectionPion(pionVueCorrespondant);
                        } else {
                            finSelection();
                        }
                    } else {
                        finSelection();
                    }
                }
            }
        }
    }

    private void selectionPion(PionVue pionVue) {
        pionVue.setSelectionne(true);
        this.pionSelectionne = pionVue;
        calculActionsPossibles(pionVue.getPion());
    }

    private void calculActionsPossibles(Pion pionClique) {
        if (!modeActionDeplacement) {
            ArrayList<Case> casesPossibles = jeu.getDeplacementsPossibles(pionClique);

            for (Case c : casesPossibles) casesMarquees.add(getTerrainVue().getCaseSur(c.getPoint()));
            for (CaseVue c : casesMarquees) c.setMarque(true);
        } else {
            ArrayList<Pion> pionsPossibles = jeu.getPassesPossibles(pionClique);

            for (Pion p : pionsPossibles) pionsMarques.add(getTerrainVue().getCaseSur(p.getPosition().getPoint()).getPionVue());
            for (PionVue p : pionsMarques) p.setMarque(true);
        }
    }

    public void finSelection() {
        if (this.pionSelectionne != null) {
            this.pionSelectionne.setSelectionne(false);
            this.pionSelectionne = null;
        }

        for (CaseVue c : casesMarquees) c.setMarque(false);
        for (PionVue p : pionsMarques) p.setMarque(false);
        casesMarquees.clear();
        pionsMarques.clear();
    }
}
