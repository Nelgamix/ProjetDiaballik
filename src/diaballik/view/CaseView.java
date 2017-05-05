package diaballik.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import diaballik.controller.TerrainController;
import diaballik.model.Point;

public class CaseView extends Rectangle {
    private final TerrainController terrainController;
    private final Point point;

    public final static int HAUTEUR = 80;
    public final static int LARGEUR = 80;

    public final static int DECALAGE_HAUT = 2;
    public final static int DECALAGE_GAUCHE = 2;

    public CaseView(TerrainController terrainController, Point point) {
        super(HAUTEUR, LARGEUR);
        this.terrainController = terrainController;
        this.point = point;

        this.setX(DECALAGE_GAUCHE + HAUTEUR * point.getX());
        this.setY(DECALAGE_HAUT + LARGEUR * point.getY());
        this.setOnMouseClicked(e -> terrainController.mouseClicked(point));
    }
}
