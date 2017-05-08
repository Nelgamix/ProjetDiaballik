package diaballik.vue;

import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Observable;
import java.util.Observer;

public class PionVue extends Circle implements Observer {
    private final static double RAYON = CaseVue.HAUTEUR / 4;
    private final static double RAYON_BALLE = RAYON * 1.5;

    private final TerrainVue terrainVue;
    private final Pion pion;

    private CaseVue caseVue;

    private boolean actif = true;
    private boolean selectionne = false;
    private boolean marque = false;
    private boolean survol = false;

    public PionVue(TerrainVue terrainVue, Pion pion) {
        super(RAYON);

        this.pion = pion;
        this.caseVue = terrainVue.getCaseSur(pion.getPosition().getPoint());
        this.terrainVue = terrainVue;
        pion.addObserver(this);

        this.setStroke(Color.BLACK);

        this.update();
    }

    public void update() {
        this.update(null, null);
    }

    public void survoler(boolean enter) {
        survol = enter;
        updateStyleClass();
    }

    public void reinitialiserStatut() {
        setMarque(false);
        this.survol = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        Point p = this.pion.getPosition().getPoint();

        this.caseVue.setPionVue(null);
        this.caseVue = this.terrainVue.getCaseSur(p);
        this.caseVue.setPionVue(this);

        this.updateStyleClass();
        this.setRadius(getRayon());
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (!actif) {
            this.getStyleClass().add("couleurAdversaire");
        } else if (survol) {
            this.getStyleClass().add("couleurSurvol");
        } else if (isSelectionne()) {
            this.getStyleClass().add("couleurSelection");
        } else if (isMarque()) {
            this.getStyleClass().add("couleurMarquage");
        } else {
            if (pion.getCouleur() == Joueur.JOUEUR_VERT)
                this.getStyleClass().add("couleurJoueurVert");
            else
                this.getStyleClass().add("couleurJoueurRouge");
        }
    }

    public void desactiver() {
        this.actif = false;
        reinitialiserStatut();
        update();
    }

    public void activer() {
        this.actif = true;
        update();
    }

    // GETTERS objets
    public Pion getPion() {
        return pion;
    }

    public CaseVue getCaseVue() {
        return caseVue;
    }

    private double getRayon() {
        return (this.pion.aLaBalle() ? RAYON_BALLE : RAYON);
    }

    // GETTERS booleens
    public boolean isMarque() {
        return marque;
    }

    public boolean isSelectionne() {
        return selectionne;
    }

    // SETTERS
    public void setActif(boolean actif) {
        if (actif != this.actif) {
            this.actif = actif;
            updateStyleClass();
        }
    }

    public void setSelectionne(boolean selectionne) {
        if (selectionne != this.selectionne) {
            this.selectionne = selectionne;
            updateStyleClass();
        }
    }

    public void setMarque(boolean marque) {
        if (marque != this.marque) {
            this.marque = marque;
            updateStyleClass();
        }
    }
}
