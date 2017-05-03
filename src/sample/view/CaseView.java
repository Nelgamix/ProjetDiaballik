package sample.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.controller.TerrainController;
import sample.model.Pion;
import sample.model.Point;

import java.util.Observable;
import java.util.Observer;

public class CaseView extends Rectangle {
    private final TerrainController terrainController;
    private final Point point;

    private final static int HAUTEUR = 50;
    private final static int LARGEUR = 50;

    public CaseView(TerrainController terrainController, Point point, Color color) {
        super(HAUTEUR, LARGEUR);
        this.terrainController = terrainController;
        this.point = point;

        this.setX(HAUTEUR * point.getX());
        this.setY(LARGEUR * point.getY());
        this.setFill(color);
        this.setOnMouseClicked(e -> terrainController.mouseClicked(point));
    }
}
