package diaballik.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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
public class ActionsView extends BorderPane implements Observer {
    private final ActionsController actionsController;
    private final Jeu jeu;

    /*private Label deplacements = new Label("Deplacement");
    private Label passe = new Label("Passe");*/
    private final Label depl;
    private final Label pass;

    public ActionsView(ActionsController actionsController) {
        super();

        // Infos
        VBox vBoxInfos = new VBox(10);
        vBoxInfos.setAlignment(Pos.CENTER);
        vBoxInfos.setPadding(new Insets(20));

        VBox vBoxInfosDepl = new VBox();
        VBox vBoxInfosPass = new VBox();
        vBoxInfosDepl.getStyleClass().add("vBoxIndicateurs");
        vBoxInfosPass.getStyleClass().add("vBoxIndicateurs");
        depl = new Label("2");
        depl.getStyleClass().add("indicateurActionsRestantes");
        Label deplInd = new Label("déplacements");
        pass = new Label("1");
        pass.getStyleClass().add("indicateurActionsRestantes");
        Label passInd = new Label("passes");

        vBoxInfosDepl.getChildren().add(depl);
        vBoxInfosDepl.getChildren().add(deplInd);
        vBoxInfosPass.getChildren().add(pass);
        vBoxInfosPass.getChildren().add(passInd);

        vBoxInfos.getChildren().add(vBoxInfosDepl);
        vBoxInfos.getChildren().add(vBoxInfosPass);

        VBox vBoxActions = new VBox(10);
        vBoxActions.setAlignment(Pos.CENTER);
        vBoxActions.setPadding(new Insets(20));

        this.setId("actionsView");

        this.actionsController = actionsController;
        this.jeu = actionsController.getJeu();

        this.jeu.addObserver(this);

        Button passerTour = new Button("Passer");
        passerTour.setOnAction(e -> jeu.changerTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);
        passerTour.setId("passerTour");

        Button rollwack = new Button("Rollwack");
        rollwack.setOnAction(e -> jeu.rollwack());
        rollwack.setMaxWidth(Double.MAX_VALUE);

        Button save = new Button("Save");
        save.setOnAction(e -> actionsController.saveGame());
        save.setMaxWidth(Double.MAX_VALUE);

        Button menu = new Button("Menu");
        menu.setOnAction(e -> actionsController.menu());
        menu.setMaxWidth(Double.MAX_VALUE);

        vBoxActions.getChildren().add(passerTour);
        vBoxActions.getChildren().add(rollwack);
        vBoxActions.getChildren().add(save);
        vBoxActions.getChildren().add(menu);

        this.setTop(vBoxInfos);
        this.setBottom(vBoxActions);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        depl.setText(jeu.getJoueurActuel().getDeplacementsRestants() + "");
        pass.setText(jeu.getJoueurActuel().getPassesRestantes() + "");
    }
}
