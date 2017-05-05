package diaballik.view;

import diaballik.controller.AffichageController;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.Observable;
import java.util.Observer;

public class AffichageView extends BorderPane implements Observer {
    private final AffichageController affichageController;
    private final Jeu jeu;
    private final Label joueurActuel;

    public AffichageView(AffichageController affichageController) {
        super();

        this.affichageController = affichageController;
        this.jeu = affichageController.getJeu();
        this.jeu.addObserver(this);

        this.setId("affichageView");

        joueurActuel = new Label("Joueur");
        this.setCenter(joueurActuel);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        joueurActuel.setText(jeu.getJoueurActuel().getNom());
        this.getStyleClass().clear();
        this.getStyleClass().add(jeu.getJoueurActuel().getCouleur() == Joueur.JOUEUR_VERT ? "couleurJoueurVert" : "couleurJoueurRouge");
        //this.setId(jeu.getTour() % 2 == 0 ? "affichageViewRed" : "affichageViewGreen");
    }
}
