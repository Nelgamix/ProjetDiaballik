package diaballik.vue;

import diaballik.controleur.AffichageControleur;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.SignalUpdate;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Observable;
import java.util.Observer;

public class AffichageVue extends StackPane implements Observer {
    private final AffichageControleur affichageControleur;
    private final Jeu jeu;
    private final Label joueurActuel;

    private final int dureeTimer; //secondes
    private final TranslateTransition timerMouvement;
    private final Rectangle timerVue;

    public AffichageVue(AffichageControleur affichageControleur) {
        super();

        BorderPane content = new BorderPane();

        this.affichageControleur = affichageControleur;
        this.jeu = affichageControleur.getJeu();
        this.jeu.addObserver(this);

        this.setId("affichageView");

        this.dureeTimer = affichageControleur.getJeu().getConfigurationPartie().getDureeTimer();

        joueurActuel = new Label("Joueur");
        timerVue = new Rectangle();
        timerVue.setFill(Color.GOLD);
        timerVue.setHeight(5);
        timerVue.toFront();
        timerVue.widthProperty().bind(this.widthProperty());
        timerMouvement = new TranslateTransition();
        timerMouvement.setNode(timerVue);
        timerMouvement.setDuration(Duration.seconds(dureeTimer));
        timerMouvement.setInterpolator(Interpolator.LINEAR);
        timerMouvement.setFromX(0);
        timerMouvement.setOnFinished(e -> {
            System.out.println("AffichageVue.affichageControlleur.finTour()");
            affichageControleur.finTour();
            timerVue.setTranslateX(0);
        });

        content.setCenter(joueurActuel);
        if (dureeTimer > 0)
            content.setBottom(timerVue);

        this.getChildren().add(content);

        update(null, SignalUpdate.INIT);
    }

    @Override
    public void update(Observable o, Object arg) {
        joueurActuel.setText(jeu.getJoueurActuel().getNom());
        this.getStyleClass().clear();
        this.getStyleClass().add(jeu.getJoueurActuel().getCouleur() == Joueur.VERT ? "couleurJoueurVert" : "couleurJoueurRouge");

        if (dureeTimer > 0 && (arg == SignalUpdate.TOUR || arg == SignalUpdate.INIT_DONE)) {
            lancerTimerVue();
        }
    }

    private void lancerTimerVue() {
        if (this.getWidth() < 1) return;

        timerMouvement.setToX(this.getWidth() * -1);
        timerMouvement.playFromStart();
    }

    public void stopperTimerVue() {
        timerMouvement.stop();
    }
}
