package diaballik.vue;

import diaballik.Diaballik;
import diaballik.Utils;
import diaballik.controleur.ActionsControleur;
import diaballik.model.ConfigurationPartie;
import diaballik.model.Jeu;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
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

    private final Button passerTour;
    private final Button antijeu;

    private final Button sauvegarde;
    private final Button parametres;

    private PopOver popOverSauvegarde;
    private boolean saveExists = false;

    private PopOver popOverParametres;

    private final ValidationSupport validationSupport = new ValidationSupport();

    private ValidationResult checkFileExists(Control control, String filename) {
        saveExists = Utils.saveExists(filename);
        return ValidationResult.fromMessageIf(control, "file exists", Severity.ERROR, saveExists);
    }

    private PopOver getSauvegarderPopover() {
        if (popOverSauvegarde != null) return popOverSauvegarde;

        BorderPane contentSave = new BorderPane();
        BorderPane contentValider = new BorderPane();
        BorderPane contentFin = new BorderPane();

        popOverSauvegarde = new PopOver();
        popOverSauvegarde.setDetachable(false);
        popOverSauvegarde.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOverSauvegarde.setOnHidden(e -> popOverSauvegarde.setContentNode(contentSave));

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
                e -> popOverSauvegarde.hide()
        ));

        Supplier<Void> sp = () -> {
            if (saveExists) {
                contentValiderLabel.setText("Le fichier de sauvegarde " + chemin.getText() + ".txt existe déjà.\n" +
                        "Voulez-vous le remplacer?");
                popOverSauvegarde.setContentNode(contentValider);
            } else {
                actionsControleur.actionSauvegarderJeu(chemin.getText());
                popOverSauvegarde.setContentNode(contentFin);
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

        contentSave.setPadding(new Insets(10));
        contentSave.setTop(new Label("Nom de la sauvegarde"));
        contentSave.setCenter(chemin);
        contentSave.setBottom(valider);

        BorderPane.setAlignment(contentValiderLabel, Pos.CENTER);

        choixValider.setOnAction(e -> {
            actionsControleur.actionSauvegarderJeu(chemin.getText());
            popOverSauvegarde.setContentNode(contentFin);
            t.play();
        });

        choixAnnuler.setOnAction(e -> popOverSauvegarde.setContentNode(contentSave));

        boutonsChoix.getChildren().addAll(choixAnnuler, choixValider);
        boutonsChoix.setAlignment(Pos.CENTER);
        boutonsChoix.setPadding(new Insets(10, 0, 5, 0));

        contentValider.setPadding(new Insets(10));
        contentValider.setCenter(contentValiderLabel);
        contentValider.setBottom(boutonsChoix);

        BorderPane.setAlignment(contentFinLabel, Pos.CENTER);

        contentFin.setPadding(new Insets(10));
        contentFin.setCenter(contentFinLabel);

        popOverSauvegarde.setContentNode(contentSave);

        return popOverSauvegarde;
    }

    private PopOver getParametresPopover() {
        if (popOverParametres != null) return popOverParametres;

        final ConfigurationPartie cp = actionsControleur.getJeu().cp;

        CheckBox parametre1 = new CheckBox();
        CheckBox parametre2 = new CheckBox();
        CheckBox parametre3 = new CheckBox();

        popOverParametres = new PopOver();
        popOverParametres.setDetachable(false);
        popOverParametres.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOverParametres.setOnHidden(e -> {
            parametre1.setSelected(cp.isAideDeplacement());
            parametre2.setSelected(cp.isAidePasse());
            parametre3.setSelected(cp.isAutoSelectionPion());
        });

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(15));

        GridPane grid = new GridPane();
        grid.setPrefWidth(300);
        grid.setHgap(10);
        grid.setVgap(10);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(85);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(15);
        grid.getColumnConstraints().addAll(cc1, cc2);

        parametre1.setSelected(cp.isAideDeplacement());
        parametre2.setSelected(cp.isAidePasse());
        parametre3.setSelected(cp.isAutoSelectionPion());

        Label labelParametre1 = new Label("Aide au déplacement des pions");
        labelParametre1.setMaxWidth(Double.MAX_VALUE);
        Label labelParametre2 = new Label("Aide aux passes");
        Label labelParametre3 = new Label("Auto sélectionner le meme pion\nsi déplacement restant");

        grid.add(labelParametre1, 0, 0);
        grid.add(parametre1, 1, 0);
        grid.add(labelParametre2, 0, 1);
        grid.add(parametre2, 1, 1);
        grid.add(labelParametre3, 0, 2);
        grid.add(parametre3, 1, 2);

        content.setCenter(grid);

        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");
        annuler.setOnAction(e -> popOverParametres.hide());
        valider.setOnAction(e -> {
            cp.setAideDeplacement(parametre1.isSelected());
            cp.setAidePasse(parametre2.isSelected());
            cp.setAutoSelectionPion(parametre3.isSelected());

            cp.writeProperties();

            popOverParametres.hide();
        });

        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));
        buttonBar.getChildren().addAll(annuler, valider);
        buttonBar.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(buttonBar, Pos.CENTER);

        content.setBottom(buttonBar);

        popOverParametres.setContentNode(content);

        return popOverParametres;
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
        sectionInd.setStyle("-fx-font-size: 18px; -fx-font-style: italic");
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

        passerTour = new Button("Fin tour");
        passerTour.setOnAction(e -> actionsControleur.actionFinTour());
        passerTour.setMaxWidth(Double.MAX_VALUE);
        passerTour.setId("passerTour");
        gpActions.add(passerTour, 0, 0, 2, 1);

        antijeu = new Button("Antijeu");
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
        if (actionsControleur.getJeu().cp.multijoueur) sauvegarde.setDisable(true);
        gpActions.add(sauvegarde, 0, 3, 2, 1);

        Glyph roue = new Glyph("FontAwesome", FontAwesome.Glyph.COG);
        roue.setFontSize(22f);
        parametres = new Button("", roue);
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
        ((Parent)popOverSauvegarde.getSkin().getNode()).getStylesheets()
                .add(getClass().getResource(Diaballik.CSS_POPOVER).toExternalForm());
    }

    public void montrerPopupParametres() {
        getParametresPopover().show(parametres);
        ((Parent)popOverParametres.getSkin().getNode()).getStylesheets()
                .add(getClass().getResource(Diaballik.CSS_POPOVER).toExternalForm());
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

        boolean jaReseau = jeu.joueurActuelReseau();
        boolean jaIA = jeu.getJoueurActuel().estUneIA();

        passerTour.setDisable(jaReseau || jaIA);
        antijeu.setDisable(jaReseau || jaIA);

        annuler.setDisable(jaReseau || jaIA || !jeu.historique.peutDefaire());
        refaire.setDisable(jaReseau || jaIA || !jeu.historique.peutRefaire());
    }
}
