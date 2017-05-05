package diaballik.view;

import diaballik.controller.TerrainController;
import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Observable;
import java.util.Observer;

public class PionView extends Circle implements Observer {
    private Pion pion;
    private TerrainController terrainController;

    private final static double RAYON = CaseView.HAUTEUR / 4;
    private final static double RAYON_BALLE = RAYON * 1.5;

    public PionView(TerrainController terrainController, Pion pion) {
        super(RAYON);

        this.pion = pion;
        this.terrainController = terrainController;
        pion.addObserver(this);

        this.setStroke(Color.BLACK);
        this.setOnMouseClicked(e -> terrainController.mouseClicked(pion.getPosition().getPoint()));

        this.update();
    }

    public void update() {
        this.update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        Point p = this.pion.getPosition().getPoint();
        this.setCenterX(CaseView.DECALAGE_GAUCHE + p.getX() * CaseView.HAUTEUR + CaseView.HAUTEUR / 2);
        this.setCenterY(CaseView.DECALAGE_HAUT + p.getY() * CaseView.LARGEUR + CaseView.LARGEUR / 2);
        this.setClass();
        this.setRadius(getRayon());
    }

    private double getRayon() {
        return (this.pion.aLaBalle() ? RAYON_BALLE : RAYON);
    }

    private void setClass() {
        this.getStyleClass().clear();

        if (pion.isSelectionne())
            this.getStyleClass().add("couleurSelection");
        else
        if (pion.getCouleur() == Joueur.JOUEUR_VERT)
            this.getStyleClass().add("couleurJoueurVert");
        else
            this.getStyleClass().add("couleurJoueurRouge");
    }
}
