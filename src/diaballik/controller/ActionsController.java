package diaballik.controller;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.view.ActionsView;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:00).
 */
public class ActionsController {
    private final Jeu jeu;
    private final ActionsView actionsView;
    private final Diaballik diaballik;

    public ActionsController(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.actionsView = new ActionsView(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public ActionsView getActionsView() {
        return actionsView;
    }

    public void menu() {
        diaballik.showSceneMenu();
    }

    public void saveGame() {
        String filename;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Diaballik Sauvegarde", "*.txt"));
        File file = fileChooser.showSaveDialog(this.diaballik.stage);
        if (file != null) {
            filename = file.getAbsolutePath();

            if (!filename.endsWith(".txt"))
                filename += ".txt";

            System.out.println("Save to " + filename);
            this.jeu.save(filename);
        }
    }
}
