package diaballik.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import diaballik.controller.TerrainController;
import diaballik.model.Point;

public class CaseView extends Rectangle {
    private final TerrainController terrainController;
    private final Point point;

    public final static int HAUTEUR = 60;
    public final static int LARGEUR = 60;

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
