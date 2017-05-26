package diaballik.controleur;

import diaballik.model.*;
import diaballik.scene.SceneJeu;
import diaballik.vue.CaseVue;
import diaballik.vue.PionVue;
import diaballik.vue.TerrainVue;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.HashMap;

public class TerrainControleur {
    public final SceneJeu sceneJeu;

    private final TerrainVue terrainVue;

    private PionVue pionSelectionne;
    private boolean modeActionDeplacement = false; // false si le mode est un deplacement, true si c'est une passe

    private boolean animationEnCours = false;

    private HashMap<Point, Deplacement> casesPossibles = new HashMap<>();
    private ArrayList<CaseVue> casesMarquees = new ArrayList<>();
    private ArrayList<PionVue> pionsMarques = new ArrayList<>();

    public TerrainControleur(SceneJeu sceneJeu) {
        this.sceneJeu = sceneJeu;
        this.terrainVue = new TerrainVue(this);
    }

    public boolean caseEstMarquee(CaseVue c) {
        return casesMarquees.contains(c);
    }

    public Jeu getJeu() {
        return sceneJeu.getJeu();
    }

    public TerrainVue getTerrainVue() {
        return terrainVue;
    }

    public void clicSouris(CaseVue caseCliquee) {
        if (animationEnCours()) return;

        PionVue pionVueCorrespondant = caseCliquee.getPionVue();

        if (pionSelectionne == null) { // Si on avait pas mémorisé de pion déjà sélectionnée
            if (pionVueCorrespondant != null) { // il y a un pion sur la case
                if (pionVueCorrespondant.getPion().getCouleur() == getJeu().getJoueurActuel().getCouleur()) { // pion de la bonne couleur
                    modeActionDeplacement = pionVueCorrespondant.getPion().aLaBalle(); // on set le bon mode
                    selectionPion(pionVueCorrespondant); // on le sélectionne
                }
            }
        } else { // sinon
            if (caseCliquee == this.pionSelectionne.getCaseVue()) { // l'utilisateur a cliqué sur la même case
                finSelection();
            } else if (modeActionDeplacement) { // si on attend une passe, il faut un pion
                if (pionVueCorrespondant != null) {
                    if (pionVueCorrespondant.getPion().getCouleur() == getJeu().getJoueurActuel().getCouleur()) { // de la bonne couleur
                        Action a = new Action(pionSelectionne.getPion().getPosition(), Action.PASSE, caseCliquee.getCase(), getJeu().getTour());
                        //this.jeu.passe(pionSelectionne.getPion(), pionVueCorrespondant.getPion());
                        JoueurLocal j = (JoueurLocal)getJeu().getJoueurActuel();
                        j.setActionAJouer(a);
                        getJeu().getJoueurActuel().jouer();
                    }
                }

                finSelection();
            } else { // on attend un déplacement, donc vers une case
                if (pionVueCorrespondant != null) {
                    if (pionVueCorrespondant.getPion().getCouleur() == getJeu().getJoueurActuel().getCouleur()) {
                        finSelection();
                        modeActionDeplacement = pionVueCorrespondant.getPion().aLaBalle(); // on set le bon mode
                        selectionPion(pionVueCorrespondant); // on le sélectionne
                    }
                } else {
                    pionVueCorrespondant = this.pionSelectionne;

                    Deplacement t = casesPossibles.get(caseCliquee.getCase().getPoint());
                    if (t != null) {
                        Action a = new Action(pionSelectionne.getPion().getPosition(), Action.DEPLACEMENT, caseCliquee.getCase(), getJeu().getTour(), t.getDistance());
                        JoueurLocal j = (JoueurLocal)getJeu().getJoueurActuel();
                        j.setActionAJouer(a);

                        if (j.jouer()) { // on tente le déplacement, si on a réussi
                            if (getJeu().getJoueurActuel().peutDeplacer() && getJeu().getConfigurationPartie().isAutoSelectionPion()) {
                                finSelection();
                                selectionPion(pionVueCorrespondant);
                            } else {
                                finSelection();
                            }
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

    void montrerMeilleurCoup() {
        Joueur ja = getJeu().getJoueurActuel();
        ConfigurationTerrain act = new ConfigurationTerrain(getJeu().getTerrain());
        ConfigurationTerrain ct = IA.meilleurTour(act, ja.getCouleur(), ja.getDeplacementsRestants(), ja.getPassesRestantes());

        if (ct.getActions().size() > 0) {
            finSelection();

            Action a = ct.getActions().get(0);
            Deplacement d = new Deplacement(a.getCaseApres(), 1);
            casesPossibles.put(d.getCase().getPoint(), d);

            this.modeActionDeplacement = (a.getAction() == Action.PASSE);

            PionVue pv = getTerrainVue().getCaseSur(a.getCaseAvant().getPoint()).getPionVue();
            pv.setSelectionne(true);
            this.pionSelectionne = pv;

            if (!this.modeActionDeplacement) {
                CaseVue cv = getTerrainVue().getCaseSur(ct.getActions().get(0).getCaseApres().getPoint());
                casesMarquees.add(cv);
                cv.setMarque(true);
            } else {
                PionVue pv2 = getTerrainVue().getCaseSur(a.getCaseApres().getPoint()).getPionVue();
                pionsMarques.add(pv2);
                pv2.setMarque(true);
            }
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Aucune action restante");
            a.setHeaderText(null);
            a.setContentText("Il ne reste aucune action optimale à effectuer.\nVous pouvez passer votre tour, ou défaire votre dernière action.");
            a.showAndWait();
        }
    }

    private void calculActionsPossibles(Pion pionClique) {
        if (!modeActionDeplacement) {
            ArrayList<Deplacement> values = getJeu().getDeplacementsPossibles(pionClique);

            for (Deplacement t : values)
                casesPossibles.put(t.getCase().getPoint(), t);

            for (Deplacement c : casesPossibles.values()) casesMarquees.add(getTerrainVue().getCaseSur(c.getCase().getPoint()));
            for (CaseVue c : casesMarquees) c.setMarque(true);
        } else {
            ArrayList<Pion> pionsPossibles = getJeu().getPassesPossibles(pionClique);

            for (Pion p : pionsPossibles) pionsMarques.add(getTerrainVue().getCaseSur(p.getPosition().getPoint()).getPionVue());
            for (PionVue p : pionsMarques) p.setMarque(true);
        }
    }

    public void finSelection() {
        if (this.pionSelectionne != null) {
            this.pionSelectionne.setSelectionne(false);
            this.pionSelectionne = null;
        }

        if (!casesPossibles.isEmpty()) casesPossibles.clear();

        for (CaseVue c : casesMarquees) c.setMarque(false);
        for (PionVue p : pionsMarques) p.setMarque(false);
        casesMarquees.clear();
        pionsMarques.clear();
    }

    public void setAnimationEnCours(boolean anim) {
        this.animationEnCours = anim;
    }
    boolean animationEnCours() {
        return animationEnCours;
    }
}
