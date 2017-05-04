package diaballik.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import diaballik.controller.ActionsController;
import diaballik.model.Jeu;

import java.util.Observable;
import java.util.Observer;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:09).
 */
public class ActionsView extends VBox implements Observer {
    private final ActionsController actionsController;
    private final Jeu jeu;

    private Label deplacements = new Label("Deplacement");
    private Label passe = new Label("Passe");

    public ActionsView(ActionsController actionsController) {
        super(10);

        this.setId("actionsView");

        this.actionsController = actionsController;
        this.jeu = actionsController.getJeu();

        this.jeu.addObserver(this);

        Button passerTour = new Button("Passer");
        passerTour.setOnAction(e -> jeu.changerTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);

        Button rollwack = new Button("Rollwack");
        rollwack.setOnAction(e -> jeu.rollwack());
        rollwack.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().add(deplacements);
        this.getChildren().add(passe);
        this.getChildren().add(passerTour);
        this.getChildren().add(rollwack);

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        deplacements.setText(jeu.getJoueurActuel().getDeplacementsRestants() + " depl.");
        passe.setText(jeu.getJoueurActuel().getPassesRestantes() + " pas.");
    }
}
