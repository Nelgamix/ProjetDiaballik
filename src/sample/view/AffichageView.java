package sample.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import sample.controller.AffichageController;
import sample.model.Jeu;

import java.util.Observable;
import java.util.Observer;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:18).
 */
public class AffichageView extends BorderPane implements Observer {
    private final AffichageController affichageController;
    private final Jeu jeu;
    private final Label joueurActuel;

    public AffichageView(AffichageController affichageController) {
        super();

        this.affichageController = affichageController;
        this.jeu = affichageController.getJeu();
        this.jeu.addObserver(this);

        //this.setId("affichageView");
        this.getStyleClass().add("affichageView");

        joueurActuel = new Label("Joueur");
        this.setCenter(joueurActuel);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        joueurActuel.setText(jeu.getJoueurActuel().getNom());
        this.setId(jeu.getTour() % 2 == 0 ? "affichageViewRed" : "affichageViewGreen");
    }
}
