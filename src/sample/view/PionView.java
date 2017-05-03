package sample.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import sample.controller.TerrainController;
import sample.model.Case;
import sample.model.Joueur;
import sample.model.Pion;
import sample.model.Point;

import java.util.Observable;
import java.util.Observer;

public class PionView extends Circle implements Observer {
    private Pion pion;
    private TerrainController terrainController;

    private final static int RAYON = 10;

    public PionView(TerrainController terrainController, Pion pion) {
        super(RAYON);
        this.pion = pion;
        this.terrainController = terrainController;
        pion.addObserver(this);

        this.setFill(getColor());
        this.setOnMouseClicked(e -> terrainController.mouseClicked(pion.getPosition().getPoint()));

        this.update();
    }

    public void update() {
        this.update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        Point p = this.pion.getPosition().getPoint();
        this.setCenterX(p.getX() * 50 + 25);
        this.setCenterY(p.getY() * 50 + 25);
        this.setFill(getColor());
    }

    private Color getColor() {
        Color sf;

        if (pion.isSelectionne())
            sf = Color.GOLD;
        else
            if (pion.getCouleur() == Joueur.COULEUR_VERT)
                sf = Color.GREEN;
            else
                sf = Color.RED;

        return sf;
    }
}
