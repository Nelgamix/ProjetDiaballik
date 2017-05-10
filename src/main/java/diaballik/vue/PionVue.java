package diaballik.vue;

import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

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

    private final TranslateTransition transitionDeplacement;

    public PionVue(TerrainVue terrainVue, Pion pion) {
        super(RAYON);

        this.pion = pion;
        this.caseVue = terrainVue.getCaseSur(pion.getPosition().getPoint());
        this.caseVue.setPionVue(this);
        this.terrainVue = terrainVue;
        pion.addObserver(this);

        this.transitionDeplacement = new TranslateTransition(Duration.millis(200), this);
        this.transitionDeplacement.setAutoReverse(false);

        this.setStroke(Color.BLACK);

        this.update(this.pion, Jeu.CHANGEMENT_INIT);
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
        if ((int)arg == Jeu.CHANGEMENT_POSITION)
            deplacerPionAnimated();

        this.updateStyleClass();
        this.setRayon(getRayon());
    }

    private void setRayon(double rayon) {
        if (rayon != this.getRadius()) {
            final Timeline transitionTaille = new Timeline();
            transitionTaille.setAutoReverse(false);
            transitionTaille.setCycleCount(1);

            final KeyValue kv = new KeyValue(this.radiusProperty(), rayon);
            final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);

            transitionTaille.getKeyFrames().clear();
            transitionTaille.getKeyFrames().addAll(kf);

            transitionTaille.play();
        }
    }

    private void deplacerPionAnimated() {
        Point destination = this.pion.getPosition().getPoint();
        Point source = this.caseVue.getCase().getPoint();
        CaseVue cvDestination = this.terrainVue.getCaseSur(destination);

        double byX = (destination.getX() - source.getX()) * CaseVue.LARGEUR;
        double byY = (destination.getY() - source.getY()) * CaseVue.HAUTEUR;
        cvDestination.reinitialiserEtat();
        this.caseVue.toFront();
        transitionDeplacement.setByX(byX);
        transitionDeplacement.setByY(byY);
        transitionDeplacement.setOnFinished(e -> {
            deplacerPion(cvDestination);
            this.setTranslateX(0);
            this.setTranslateY(0);
            terrainVue.getTerrainControleur().diaballik.setCurseurNormal(terrainVue.getTerrainControleur().diaballik.getSceneJeu());
        });

        transitionDeplacement.play();
    }

    private void deplacerPion(CaseVue destination) {
        this.caseVue.setPionVue(null);
        this.caseVue = destination;
        this.caseVue.setPionVue(this);
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (!actif) {
            this.getStyleClass().add("couleurAdversaire");
        } else if (survol) {
            this.getStyleClass().add("couleurSurvol");
        } else if (isSelectionne()) {
            this.getStyleClass().add("couleurSelection");
        } else if (isMarque() && terrainVue.getTerrainControleur().getJeu().cp.aidePasse) {
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
        update(this.pion, Jeu.CHANGEMENT_GLOBAL);
    }

    public void activer() {
        this.actif = true;
        update(this.pion, Jeu.CHANGEMENT_GLOBAL);
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
