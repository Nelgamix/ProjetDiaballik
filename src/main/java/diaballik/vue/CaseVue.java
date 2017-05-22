package diaballik.vue;

import diaballik.Diaballik;
import diaballik.model.Case;
import diaballik.model.ConfigurationPartie;
import diaballik.model.Point;
import diaballik.model.SignalUpdate;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Observable;
import java.util.Observer;

public class CaseVue extends StackPane implements Observer {
    private final TerrainVue terrainVue;
    private final Case c;
    private PionVue pionVue;

    private final boolean pair;

    private final BorderPane notationCase;

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

        notationCase = new BorderPane();
        char ligne = (char)(65 + getCase().getPoint().getY());
        Label caseN = new Label(ligne + "-" + (getCase().getPoint().getX() + 1));
        notationCase.setBottom(caseN);
        terrainVue.getTerrainControleur().getJeu().getConfigurationPartie().addObserver(this);

        Point point = c.getPoint();
        pair = Math.abs(point.getX() - point.getY()) % 2 == 0;

        this.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.setOnMouseClicked(e -> terrainVue.getTerrainControleur().clicSouris(this));
        this.setOnMouseEntered(e -> survoler(true));
        this.setOnMouseExited(e -> survoler(false));

        update(terrainVue.getTerrainControleur().getJeu().getConfigurationPartie(), SignalUpdate.INIT);
    }

    public void setPionVue(PionVue pionVue) {
        if (pionVue != null) {
            this.getChildren().add(pionVue);
            if (survol)
                survoler(true);
        } else {
            this.getChildren().remove(this.pionVue);
        }
        this.pionVue = pionVue;
    }

    public void reinitialiserEtat() {
        this.setMarque(false);
        this.survol = false;
        updateStyleClass();
    }

    private void survoler(boolean enter) {
        // selection pion alliés, puis cases dispos
        Diaballik d = terrainVue.getTerrainControleur().sceneJeu.getDiaballik();

        if (this.pionVue != null && terrainVue.getTerrainControleur().getJeu().pionAllie(pionVue.getPion())) {
            this.pionVue.survoler(enter);
            if (enter)
                d.setCurseurSelection();
            else
                d.setCurseurNormal();
            survol = false;
        } else if (terrainVue.getTerrainControleur().caseEstMarquee(this)) {
            survol = enter;
            if (enter) {
                d.setCurseurSelection();
                this.toFront();
            } else {
                d.setCurseurNormal();
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
        setMontrerNumCase(((ConfigurationPartie)o).isNotationsCase());
    }

    private void updateStyleClass() {
        this.getStyleClass().clear();

        if (survol)
            this.getStyleClass().add("couleurMarquageSurvol");
        else if (isMarque() && terrainVue.getTerrainControleur().getJeu().getConfigurationPartie().isAideDeplacement())
            this.getStyleClass().add("couleurMarquage");
        else if (pair)
            this.getStyleClass().add("couleurCasePair");
        else
            this.getStyleClass().add("couleurCaseImpair");
    }

    public void setMontrerNumCase(boolean in) {
        if (in && !this.getChildren().contains(notationCase)) {
            this.getChildren().add(notationCase);
        } else if (!in && this.getChildren().contains(notationCase)) {
            this.getChildren().remove(notationCase);
        }
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