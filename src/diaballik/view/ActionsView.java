package diaballik.view;

import diaballik.Diaballik;
import diaballik.controller.ActionsController;
import diaballik.model.Jeu;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;

public class ActionsView extends BorderPane implements Observer {
    private final ActionsController actionsController;
    private final Jeu jeu;

    private final Label depl;
    private final Label pass;

    private final Label deplInd;
    private final Label passInd;

    public ActionsView(ActionsController actionsController) {
        super();

        // Infos
        VBox vBoxInfos = new VBox(20);
        vBoxInfos.setAlignment(Pos.CENTER);
        vBoxInfos.setPadding(new Insets(20));

        VBox vBoxInfosDepl = new VBox();
        VBox vBoxInfosPass = new VBox();
        vBoxInfosDepl.getStyleClass().add("vBoxIndicateurs");
        vBoxInfosPass.getStyleClass().add("vBoxIndicateurs");
        depl = new Label();
        depl.getStyleClass().add("indicateurActionsRestantes");
        deplInd = new Label();
        pass = new Label();
        pass.getStyleClass().add("indicateurActionsRestantes");
        passInd = new Label();

        vBoxInfosDepl.getChildren().add(depl);
        vBoxInfosDepl.getChildren().add(deplInd);
        vBoxInfosPass.getChildren().add(pass);
        vBoxInfosPass.getChildren().add(passInd);

        Label sectionInd = new Label("Actions restantes");
        sectionInd.setStyle("-fx-font-size: 20px; -fx-font-style: italic");
        vBoxInfos.getChildren().add(sectionInd);
        vBoxInfos.getChildren().add(vBoxInfosDepl);
        vBoxInfos.getChildren().add(vBoxInfosPass);

        VBox vBoxActions = new VBox(10);
        vBoxActions.setAlignment(Pos.CENTER);
        vBoxActions.setPadding(new Insets(20));

        this.setId("actionsView");

        this.actionsController = actionsController;
        this.jeu = actionsController.getJeu();

        this.jeu.addObserver(this);

        Button passerTour = new Button("Fin tour");
        passerTour.setOnAction(e -> jeu.changerTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);
        passerTour.setId("passerTour");

        Button rollback = new Button("Rollback");
        rollback.setOnAction(e -> actionsController.rollback());
        rollback.setMaxWidth(Double.MAX_VALUE);

        Button antijeu = new Button("Antijeu");
        antijeu.setOnAction(e -> System.out.println("Antijeu?"));
        antijeu.setMaxWidth(Double.MAX_VALUE);

        Button save = new Button("Sauvegarder");
        save.setOnAction(e -> actionsController.saveGame(Diaballik.SAVES_DIRECTORY));
        save.setMaxWidth(Double.MAX_VALUE);

        Button menu = new Button("Menu");
        menu.setOnAction(e -> actionsController.menu());
        menu.setMaxWidth(Double.MAX_VALUE);

        vBoxActions.getChildren().add(passerTour);
        vBoxActions.getChildren().add(rollback);
        vBoxActions.getChildren().add(antijeu);
        vBoxActions.getChildren().add(save);
        vBoxActions.getChildren().add(menu);

        this.setTop(vBoxInfos);
        this.setBottom(vBoxActions);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        int deplRest = jeu.getJoueurActuel().getDeplacementsRestants();
        int passRest = jeu.getJoueurActuel().getPassesRestantes();

        if (deplRest > 1) deplInd.setText("déplacements");
        else deplInd.setText("déplacement");

        if (passRest > 1) passInd.setText("passes");
        else passInd.setText("passe");

        depl.setText(deplRest + "");
        pass.setText(passRest + "");
    }
}
