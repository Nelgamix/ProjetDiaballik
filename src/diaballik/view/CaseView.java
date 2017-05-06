package diaballik.view;

import diaballik.model.Case;
import diaballik.model.Point;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;

import java.util.Observable;
import java.util.Observer;

public class CaseView extends StackPane implements Observer {
    private final TerrainView terrainView;
    private final Case c;
    private final Point point;
    private PionView pionView;

    private final boolean pair;
    private boolean hover = false;

    public final static int HAUTEUR = 80;
    public final static int LARGEUR = 80;

    public CaseView(TerrainView terrainView, Case c) {
        super();

        this.terrainView = terrainView;
        this.c = c;
        this.point = c.getPoint();
        c.addObserver(this);
        this.pionView = null;

        pair = Math.abs(point.getX() - point.getY()) % 2 == 0;

        this.setMaxWidth(LARGEUR);
        this.setMaxHeight(HAUTEUR);
        this.setWidth(LARGEUR);
        this.setHeight(HAUTEUR);
        this.setMinSize(LARGEUR, HAUTEUR);

        this.setOnMouseClicked(e -> terrainView.getTerrainController().mouseClicked(point));
        this.setOnMouseEntered(e -> hover(true));
        this.setOnMouseExited(e -> hover(false));

        update(null, null);
    }

    public void setPionView(PionView pionView) {
        this.pionView = pionView;
        if (pionView != null) {
            this.getChildren().add(pionView);
            if (hover)
                hover(true);
        } else {
            this.getChildren().clear();
        }
    }

    public void hover(boolean enter) {
        // selection pion alliés, puis cases dispos

        if (this.pionView != null && terrainView.getTerrainController().getJeu().pionAllie(pionView.getPion())) {
            this.pionView.hover(enter);
            if (enter)
                terrainView.getTerrainController().diaballik.getSceneJeu().setCursor(Cursor.HAND);
            else
                terrainView.getTerrainController().diaballik.getSceneJeu().setCursor(Cursor.DEFAULT);
            hover = false;
        } else if (terrainView.getTerrainController().caseEstMarquee(this.c)) {
            hover = enter;
            if (enter)
                terrainView.getTerrainController().diaballik.getSceneJeu().setCursor(Cursor.HAND);
            else
                terrainView.getTerrainController().diaballik.getSceneJeu().setCursor(Cursor.DEFAULT);
        } else {
            hover = false;
        }

        updateStyleClass();
    }

    public PionView getPionView() {
        return pionView;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateStyleClass();
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (hover)
            this.getStyleClass().add("couleurSurvol");
        else if (c.isMarque())
            this.getStyleClass().add("couleurMarquage");
        else if (pair)
            this.getStyleClass().add("couleurCasePair");
        else
            this.getStyleClass().add("couleurCaseImpair");
    }
}