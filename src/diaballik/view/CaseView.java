package diaballik.view;

import diaballik.model.Case;
import diaballik.model.Point;
import javafx.scene.shape.Rectangle;

import java.util.Observable;
import java.util.Observer;

public class CaseView extends Rectangle implements Observer {
    private final TerrainView terrainView;
    private final Case c;
    private final Point point;
    private PionView pionView;

    public final static int HAUTEUR = 80;
    public final static int LARGEUR = 80;

    public final static int DECALAGE_HAUT = 2;
    public final static int DECALAGE_GAUCHE = 2;

    public CaseView(TerrainView terrainView, Case c) {
        super(HAUTEUR, LARGEUR);
        this.terrainView = terrainView;
        this.c = c;
        this.point = c.getPoint();
        c.addObserver(this);
        this.pionView = null;

        this.setX(DECALAGE_GAUCHE + HAUTEUR * point.getX());
        this.setY(DECALAGE_HAUT + LARGEUR * point.getY());
        this.setOnMouseClicked(e -> terrainView.getTerrainController().mouseClicked(point));
    }

    public void setPionView(PionView pionView) {
        this.pionView = pionView;
    }

    public PionView getPionView() {
        return pionView;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (c.isMarque()) {
            this.getStyleClass().add("couleurMarquage");
        } else {
            this.getStyleClass().remove("couleurMarquage");
        }
    }
}
