package diaballik.vue;

import diaballik.Utils;
import diaballik.controleur.ActionsControleur;
import diaballik.model.Jeu;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Supplier;

public class ActionsVue extends BorderPane implements Observer {
    private final ActionsControleur actionsControleur;
    private final Jeu jeu;

    private final Label depl;
    private final Label pass;

    private final Label deplInd;
    private final Label passInd;

    private final Button annuler;
    private final Button refaire;

    private final Button sauvegarde;

    private PopOver p;
    private boolean saveExists = false;

    private final ValidationSupport validationSupport = new ValidationSupport();

    private ValidationResult checkFileExists(Control control, String filename) {
        saveExists = Utils.saveExists(filename);
        return ValidationResult.fromMessageIf(control, "file exists", Severity.ERROR, saveExists);
    }

    private PopOver getSauvegarderPopover() {
        if (p != null) return p;

        p = new PopOver();
        p.setDetachable(false);
        p.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        BorderPane contentSave = new BorderPane();
        BorderPane contentValider = new BorderPane();
        BorderPane contentFin = new BorderPane();

        // Déclarations
        // contentSave
        TextField chemin = new TextField();
        Button valider = new Button("Valider");

        // contentValider
        Label contentValiderLabel = new Label();
        HBox boutonsChoix = new HBox(5);
        Button choixValider = new Button("Ecraser");
        Button choixAnnuler = new Button("Annuler");

        // contentFin
        Label contentFinLabel = new Label("Sauvegarde effectuée.");

        // Utils
        Timeline t = new Timeline(new KeyFrame(
                Duration.seconds(2),
                e -> p.setContentNode(contentSave)
        ));

        Supplier<Void> sp = () -> {
            if (saveExists) {
                contentValiderLabel.setText("Le fichier de sauvegarde " + chemin.getText() + ".txt existe déjà.\n" +
                        "Voulez-vous le remplacer?");
                p.setContentNode(contentValider);
            } else {
                actionsControleur.actionSauvegarderJeu(chemin.getText());
                p.setContentNode(contentFin);
                t.play();
            }

            return null;
        };

        // Fonction
        BorderPane.setMargin(chemin, new Insets(10, 0, 10, 0));
        validationSupport.registerValidator(chemin, false, this::checkFileExists);
        chemin.setText("Partie 1");
        chemin.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                sp.get();
        });

        BorderPane.setAlignment(valider, Pos.CENTER);
        valider.setOnAction(e -> sp.get());

        contentSave.setPadding(new Insets(5));
        contentSave.setTop(new Label("Nom de la sauvegarde :"));
        contentSave.setCenter(chemin);
        contentSave.setBottom(valider);

        BorderPane.setAlignment(contentValiderLabel, Pos.CENTER);

        choixValider.setOnAction(e -> {
            actionsControleur.actionSauvegarderJeu(chemin.getText());
            p.setContentNode(contentFin);
            t.play();
        });

        choixAnnuler.setOnAction(e -> p.setContentNode(contentSave));

        boutonsChoix.getChildren().addAll(choixAnnuler, choixValider);
        boutonsChoix.setAlignment(Pos.CENTER);
        boutonsChoix.setPadding(new Insets(10, 0, 5, 0));

        contentValider.setPadding(new Insets(10));
        contentValider.setCenter(contentValiderLabel);
        contentValider.setBottom(boutonsChoix);

        BorderPane.setAlignment(contentFinLabel, Pos.CENTER);

        contentFin.setPadding(new Insets(10));
        contentFin.setCenter(contentFinLabel);

        p.setContentNode(contentSave);

        return p;
    }

    public ActionsVue(ActionsControleur actionsControleur) {
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

        sauvegarde = new Button("Sauvegarder");
        sauvegarde.setOnAction(e -> {
            montrerPopupSauvegarde();
            //actionsControleur.actionSauvegarderJeu(Diaballik.DOSSIER_SAUVEGARDES)
        });
        sauvegarde.setMaxWidth(Double.MAX_VALUE);
        gpActions.add(sauvegarde, 0, 3, 2, 1);

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

    public void montrerPopupSauvegarde() {
        getSauvegarderPopover().show(sauvegarde);
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
