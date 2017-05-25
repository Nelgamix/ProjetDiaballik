package diaballik.vue;

import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;
import diaballik.model.SignalUpdate;
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
    private double rayonBase;

    private final TerrainVue terrainVue;
    private final Pion pion;
    private boolean aLaBalle;

    private CaseVue caseVue;

    private boolean actif = true;
    private boolean selectionne = false;
    private boolean marque = false;
    private boolean survol = false;

    private final TranslateTransition transitionDeplacement;
    private final TranslateTransition transitionPasse;

    private final static int DUREE_TRANSITION_PASSE = 500;
    private final static int DUREE_TRANSITION_DEPLACEMENT = 500;

    PionVue(TerrainVue terrainVue, Pion pion) {
        super(20);

        this.pion = pion;
        this.aLaBalle = pion.aLaBalle();
        this.caseVue = terrainVue.getCaseSur(pion.getPosition().getPoint());
        this.caseVue.setPionVue(this);
        this.terrainVue = terrainVue;
        pion.addObserver(this);

        caseVue.heightProperty().addListener((o, ov, nv) -> this.setRayonFromCaseView(nv.doubleValue() / 4));

        this.transitionDeplacement = new TranslateTransition(Duration.millis(DUREE_TRANSITION_DEPLACEMENT), this);
        this.transitionDeplacement.setAutoReverse(false);

        this.transitionPasse = new TranslateTransition(Duration.millis(DUREE_TRANSITION_PASSE));
        this.transitionPasse.setAutoReverse(false);

        this.setStroke(Color.BLACK);

        this.update(this.pion, SignalUpdate.INIT);
    }

    // Gestion du survol à la souris
    void survoler(boolean enter) {
        survol = enter;
        updateStyleClass();
    }

    private void reinitialiserStatut() {
        setMarque(false);
        this.survol = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == SignalUpdate.POSITION)
            deplacerPionAnimated();

        this.updateStyleClass();

        if (arg == SignalUpdate.PASSE)
            passe();
        else
            this.setRayon();
    }

    private void setRayonFromCaseView(double rayon) {
        rayonBase = rayon;
        setRayon();
    }

    private void passe() {
        PionVue envoyeur = terrainVue.getPionVueBalle(pion.getCouleur());

        Point destination = this.pion.getPosition().getPoint();
        Point source = envoyeur.getPion().getPosition().getPoint();

        double byX = (destination.getX() - source.getX()) * caseVue.getWidth();
        double byY = (destination.getY() - source.getY()) * caseVue.getHeight();

        Circle tr = new Circle(this.getRadius());
        tr.getStyleClass().add(pion.getCouleur() == Joueur.VERT ? "couleurJoueurVert" : "couleurJoueurRouge");

        envoyeur.getCaseVue().getChildren().add(tr);
        envoyeur.getCaseVue().toFront();

        transitionPasse.setNode(tr);
        transitionPasse.setByX(byX);
        transitionPasse.setByY(byY);

        final Timeline transitionTaille = new Timeline();
        transitionTaille.setAutoReverse(false);
        transitionTaille.setCycleCount(1);

        final KeyValue kv = new KeyValue(envoyeur.radiusProperty(), rayonBase);

        aLaBalle = true;
        final KeyValue kv2 = new KeyValue(this.radiusProperty(), getRayon());

        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        final KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);

        transitionTaille.getKeyFrames().clear();
        transitionTaille.getKeyFrames().addAll(kf, kf2);

        transitionTaille.setOnFinished(e -> envoyeur.aLaBalle = false);

        transitionPasse.setOnFinished(e -> envoyeur.getCaseVue().getChildren().remove(tr));

        transitionPasse.play();
        transitionTaille.play();
    }

    // Set le rayon en fonction de la propriété aLaBalle du pion
    private void setRayon() {
        double r = getRayon();

        if (r > 0 && r != this.getRadius()) {
            setRadius(r);
        }
    }

    // Déplacer le pionVue de manière animée
    private void deplacerPionAnimated() {
        Point destination = this.pion.getPosition().getPoint();
        Point source = this.caseVue.getCase().getPoint();
        CaseVue cvDestination = this.terrainVue.getCaseSur(destination);

        double byX = (destination.getX() - source.getX()) * caseVue.getWidth();
        double byY = (destination.getY() - source.getY()) * caseVue.getHeight();
        cvDestination.reinitialiserEtat();
        this.caseVue.toFront();

        transitionDeplacement.setByX(byX);
        transitionDeplacement.setByY(byY);
        transitionDeplacement.setOnFinished(e -> {
            deplacerPion(cvDestination);
            this.setTranslateX(0);
            this.setTranslateY(0);
            terrainVue.getTerrainControleur().sceneJeu.getDiaballik().setCurseurNormal();
        });

        transitionDeplacement.play();
    }

    // Déplacer le pionVue vers la case destination
    private void deplacerPion(CaseVue destination) {
        this.caseVue.setPionVue(null);
        this.caseVue = destination;
        this.caseVue.setPionVue(this);
    }

    // Update le style de la classe en fonction des propriétés
    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (!actif) {
            this.getStyleClass().add(pion.getCouleur() == Joueur.VERT ? "couleurJoueurVertInactif" : "couleurJoueurRougeInactif");
        } else if (survol) {
            if (isSelectionne())
                this.getStyleClass().add("couleurSelectionSurvol");
            else if (isMarque())
                this.getStyleClass().add("couleurMarquageSurvol");
            else
                this.getStyleClass().add(pion.getCouleur() == Joueur.VERT ? "couleurJoueurVertSurvol" : "couleurJoueurRougeSurvol");
        } else if (isSelectionne()) {
            this.getStyleClass().add("couleurSelection");
        } else if (isMarque() && terrainVue.getTerrainControleur().getJeu().getConfigurationPartie().isAidePasse()) {
            this.getStyleClass().add("couleurMarquage");
        } else {
            if (pion.getCouleur() == Joueur.VERT)
                this.getStyleClass().add("couleurJoueurVert");
            else
                this.getStyleClass().add("couleurJoueurRouge");
        }
    }

    // Actif / inactif (quand les pions ne sont pas jouables)
    void desactiver() {
        this.actif = false;
        reinitialiserStatut();
        update(this.pion, SignalUpdate.GLOBAL);
    }
    void activer() {
        this.actif = true;
        update(this.pion, SignalUpdate.GLOBAL);
    }

    // GETTERS objets
    public Pion getPion() {
        return pion;
    }
    public CaseVue getCaseVue() {
        return caseVue;
    }
    private double getRayon() {
        return (this.pion.aLaBalle() ? rayonBase * 1.5 : rayonBase);
    }

    // GETTERS booleens
    private boolean isMarque() {
        return marque;
    }
    private boolean isSelectionne() {
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

    public boolean aLaBalle() {
        return aLaBalle;
    }
}
