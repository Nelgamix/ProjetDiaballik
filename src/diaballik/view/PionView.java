package diaballik.view;

import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Observable;
import java.util.Observer;

public class PionView extends Circle implements Observer {
    private final Pion pion;
    private CaseView caseView;
    private TerrainView terrainView;

    private boolean enabled = true;
    private boolean hover = false;

    private final static double RAYON = CaseView.HAUTEUR / 4;
    private final static double RAYON_BALLE = RAYON * 1.5;

    public PionView(TerrainView terrainView, Pion pion) {
        super(RAYON);

        this.pion = pion;
        this.caseView = terrainView.getCaseAt(pion.getPosition().getPoint());
        this.terrainView = terrainView;
        pion.addObserver(this);

        this.setStroke(Color.BLACK);

        this.update();
    }

    public void update() {
        this.update(null, null);
    }

    public void hover(boolean enter) {
        hover = enter;
        updateStyleClass();
    }

    public void resetState() {
        this.getPion().setMarque(false);
        this.hover = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        Point p = this.pion.getPosition().getPoint();

        this.caseView.setPionView(null);
        this.caseView = this.terrainView.getCaseAt(p);
        this.caseView.setPionView(this);

        this.updateStyleClass();
        this.setRadius(getRayon());
    }

    private double getRayon() {
        return (this.pion.aLaBalle() ? RAYON_BALLE : RAYON);
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (!enabled) {
            this.getStyleClass().add("couleurAdversaire");
        } else if (hover) {
            this.getStyleClass().add("couleurSurvol");
        } else if (pion.isSelectionne()) {
            this.getStyleClass().add("couleurSelection");
        } else if (pion.isMarque()) {
            this.getStyleClass().add("couleurMarquage");
        } else {
            if (pion.getCouleur() == Joueur.JOUEUR_VERT)
                this.getStyleClass().add("couleurJoueurVert");
            else
                this.getStyleClass().add("couleurJoueurRouge");
        }
    }

    public void disable() {
        this.enabled = false;
        resetState();
        update();
    }

    public void enable() {
        this.enabled = true;
        update();
    }

    public Pion getPion() {
        return pion;
    }
}
