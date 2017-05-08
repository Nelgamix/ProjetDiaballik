package diaballik.vue;

import diaballik.Diaballik;
import diaballik.model.Case;
import diaballik.model.Point;
import javafx.scene.layout.StackPane;

import java.util.Observable;
import java.util.Observer;

public class CaseVue extends StackPane implements Observer {
    private final TerrainVue terrainVue;
    private final Case c;
    private PionVue pionVue;

    private final boolean pair;

    private boolean marque = false;
    private boolean survol = false;

    public final static int HAUTEUR = 80;
    public final static int LARGEUR = 80;

    public CaseVue(TerrainVue terrainVue, Case c) {
        super();

        this.terrainVue = terrainVue;
        this.c = c;
        c.addObserver(this);
        this.pionVue = null;

        Point point = c.getPoint();
        pair = Math.abs(point.getX() - point.getY()) % 2 == 0;

        this.setMinSize(LARGEUR, HAUTEUR);

        this.setOnMouseClicked(e -> terrainVue.getTerrainControleur().clicSouris(this));
        this.setOnMouseEntered(e -> survoler(true));
        this.setOnMouseExited(e -> survoler(false));

        update(null, null);
    }

    public void setPionVue(PionVue pionVue) {
        this.pionVue = pionVue;
        if (pionVue != null) {
            this.getChildren().add(pionVue);
            if (survol)
                survoler(true);
        } else {
            this.getChildren().clear();
        }
    }

    private void survoler(boolean enter) {
        // selection pion alliés, puis cases dispos
        Diaballik d = terrainVue.getTerrainControleur().diaballik;

        if (this.pionVue != null && terrainVue.getTerrainControleur().getJeu().pionAllie(pionVue.getPion())) {
            this.pionVue.survoler(enter);
            if (enter)
                d.setCurseurSelection(d.getSceneJeu());
            else
                d.setCurseurNormal(d.getSceneJeu());
            survol = false;
        } else if (terrainVue.getTerrainControleur().caseEstMarquee(this)) {
            survol = enter;
            if (enter) {
                d.setCurseurSelection(d.getSceneJeu());
                this.toFront();
            } else {
                d.setCurseurNormal(d.getSceneJeu());
            }
        } else {
            survol = false;
        }

        updateStyleClass();
    }

    public PionVue getPionVue() {
        return pionVue;
    }

    private boolean isMarque() {
        return marque;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateStyleClass();
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (survol)
            this.getStyleClass().add("couleurSurvol");
        else if (isMarque())
            this.getStyleClass().add("couleurMarquage");
        else if (pair)
            this.getStyleClass().add("couleurCasePair");
        else
            this.getStyleClass().add("couleurCaseImpair");
    }

    public Case getCase() {
        return c;
    }

    public void setMarque(boolean marque) {
        if (marque != this.marque) {
            this.marque = marque;
            this.toFront();
            updateStyleClass();
        }
    }
}