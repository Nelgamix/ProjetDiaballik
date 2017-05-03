package sample.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sample.controller.ActionsController;
import sample.model.Jeu;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:09).
 */
public class ActionsView extends VBox {
    private final ActionsController actionsController;
    private final Jeu jeu;

    public ActionsView(ActionsController actionsController) {
        super();

        this.setId("actionsView");

        this.actionsController = actionsController;
        this.jeu = actionsController.getJeu();

        Button passerTour = new Button("Passer");
        passerTour.setOnAction(e -> jeu.changerTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);
        this.getChildren().add(passerTour);

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
    }
}
