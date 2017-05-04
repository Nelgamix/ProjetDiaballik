package diaballik.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import diaballik.controller.TerrainController;
import diaballik.model.Joueur;
import diaballik.model.Pion;
import diaballik.model.Point;

import java.util.Observable;
import java.util.Observer;

public class PionView extends Circle implements Observer {
    private Pion pion;
    private TerrainController terrainController;

    private final static int RAYON = CaseView.HAUTEUR / 5;
    private final static int RAYON_BALLE = RAYON * 2;

    public PionView(TerrainController terrainController, Pion pion) {
        super(RAYON);

        this.pion = pion;
        this.terrainController = terrainController;
        pion.addObserver(this);

        this.setFill(getColor());
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
        this.setCenterX(p.getX() * CaseView.HAUTEUR + CaseView.HAUTEUR / 2);
        this.setCenterY(p.getY() * CaseView.LARGEUR + CaseView.LARGEUR / 2);
        this.setFill(getColor());
        this.setRadius(getRayon());
    }

    private int getRayon() {
        return (this.pion.aLaBalle() ? RAYON_BALLE : RAYON);
    }

    private Color getColor() {
        Color sf;

        if (pion.isSelectionne())
            sf = Color.GOLD;
        else
            if (pion.getCouleur() == Joueur.JOUEUR_VERT)
                sf = Joueur.COULEUR_VERT;
            else
                sf = Joueur.COULEUR_ROUGE;

        return sf;
    }
}
