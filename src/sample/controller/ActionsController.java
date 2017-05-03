package sample.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sample.model.Jeu;
import sample.view.ActionsView;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:00).
 */
public class ActionsController {
    private final Jeu jeu;
    private final ActionsView actionsView;

    public ActionsController(Jeu jeu) {
        this.jeu = jeu;
        this.actionsView = new ActionsView(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public ActionsView getActionsView() {
        return actionsView;
    }
}
