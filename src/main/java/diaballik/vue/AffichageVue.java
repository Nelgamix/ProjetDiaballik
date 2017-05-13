package diaballik.vue;

import diaballik.controleur.AffichageControleur;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.Observable;
import java.util.Observer;

public class AffichageVue extends BorderPane implements Observer {
    private final AffichageControleur affichageControleur;
    private final Jeu jeu;
    private final Label joueurActuel;

    public AffichageVue(AffichageControleur affichageControleur) {
        super();

        this.affichageControleur = affichageControleur;
        this.jeu = affichageControleur.getJeu();
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
        this.getStyleClass().add(jeu.getJoueurActuel().getCouleur() == Joueur.VERT ? "couleurJoueurVert" : "couleurJoueurRouge");
        //this.setId(jeu.getTour() % 2 == 0 ? "affichageViewRed" : "affichageViewGreen");
    }
}
