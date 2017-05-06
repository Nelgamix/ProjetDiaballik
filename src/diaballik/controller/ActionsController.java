package diaballik.controller;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.view.ActionsView;
import diaballik.view.Dialogs;
import javafx.stage.FileChooser;

import java.io.File;

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
        if (Dialogs.confirmByDialog("Vous allez quitter le jeu. La partie sera perdue. Voulez-vous continuer?")) {
            diaballik.showSceneMenu();
        }
    }

    public void saveGame(String directory) {
        String filename;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(directory));
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

    public void antijeu() {
        System.out.println("Antijeu");
    }

    public void rollback() {
        jeu.rollback();
    }
}
