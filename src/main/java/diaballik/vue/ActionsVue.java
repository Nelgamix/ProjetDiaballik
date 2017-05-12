package diaballik.vue;

import diaballik.Diaballik;
import diaballik.controleur.ActionsControleur;
import diaballik.model.Jeu;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.util.Observable;
import java.util.Observer;

public class ActionsVue extends BorderPane implements Observer {
    private final ActionsControleur actionsControleur;
    private final Jeu jeu;

    private final Label depl;
    private final Label pass;

    private final Label deplInd;
    private final Label passInd;

    public final Button annuler;
    public final Button refaire;

    private PopOver getSauvegarderPopover() {
        PopOver p = new PopOver();

        BorderPane b = new BorderPane();

        b.setTop(new Label("Nom de la sauvegarde :"));
        TextField chemin = new TextField("Partie 1");
        BorderPane.setMargin(chemin, new Insets(10, 0, 10, 0));
        b.setCenter(chemin);
        Button valider = new Button("Valider");
        valider.setAlignment(Pos.CENTER);
        
        b.setBottom(valider);
        b.setPadding(new Insets(5));

        p.setContentNode(b);

        return p;
    }

    public ActionsVue(ActionsControleur actionsControleur) {
        super();

        PopOver popSauvegarder = getSauvegarderPopover();

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

        GridPane gpActions = new GridPane();
        gpActions.setAlignment(Pos.CENTER);
        gpActions.setVgap(10);
        gpActions.setHgap(5);
        gpActions.setPadding(new Insets(20));
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        cc1.setPercentWidth(50);
        cc2.setPercentWidth(50);
        gpActions.getColumnConstraints().addAll(cc1, cc2);

        VBox vBoxActions = new VBox(10);
        vBoxActions.setAlignment(Pos.CENTER);
        vBoxActions.setPadding(new Insets(20));

        this.setId("actionsView");

        this.actionsControleur = actionsControleur;
        this.jeu = actionsControleur.getJeu();

        this.jeu.addObserver(this);

        Button passerTour = new Button("Fin tour");
        passerTour.setOnAction(e -> jeu.avancerTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);
        passerTour.setId("passerTour");
        gpActions.add(passerTour, 0, 0, 2, 1);

        Button antijeu = new Button("Antijeu");
        antijeu.setOnAction(e -> actionsControleur.actionAntijeu());
        antijeu.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(antijeu, 0, 1, 2, 1);

        Glyph undo = new Glyph("FontAwesome", FontAwesome.Glyph.UNDO);
        undo.setFontSize(22f);
        annuler = new Button("", undo);
        annuler.setOnAction(e -> actionsControleur.actionDefaire());
        annuler.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(annuler, 0, 2);

        Glyph repeat = new Glyph("FontAwesome", FontAwesome.Glyph.REPEAT);
        repeat.setFontSize(22f);
        refaire = new Button("", repeat);
        refaire.setOnAction(e -> actionsControleur.actionRefaire());
        refaire.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(refaire, 1, 2);

        Button sauvegarder = new Button("Sauvegarder");
        sauvegarder.setOnAction(e -> {
            popSauvegarder.show(sauvegarder);
            //actionsControleur.actionSauvegarderJeu(Diaballik.DOSSIER_SAUVEGARDES)
        });
        sauvegarder.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(sauvegarder, 0, 3, 2, 1);

        Glyph roue = new Glyph("FontAwesome", FontAwesome.Glyph.COG);
        roue.setFontSize(22f);
        Button parametres = new Button("", roue);
        parametres.setOnAction(e -> actionsControleur.actionParametres());
        parametres.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(parametres, 0, 4);

        Button menu = new Button("Menu");
        menu.setOnAction(e -> actionsControleur.actionMenu());
        menu.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(menu, 1, 4);

        this.setTop(vBoxInfos);
        this.setBottom(gpActions);

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
        if (deplRest < 1) depl.setTextFill(Color.RED);
        else depl.setTextFill(Color.BLACK);

        pass.setText(passRest + "");
        if (passRest < 1) pass.setTextFill(Color.RED);
        else pass.setTextFill(Color.BLACK);

        annuler.setDisable(!jeu.historique.peutDefaire());
        refaire.setDisable(!jeu.historique.peutRefaire());
    }
}
