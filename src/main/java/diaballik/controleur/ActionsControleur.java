package diaballik.controleur;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.vue.ActionsVue;
import diaballik.vue.Dialogs;
import javafx.stage.FileChooser;

import java.io.File;

public class ActionsControleur {
    private final Jeu jeu;
    private final ActionsVue actionsVue;
    private final Diaballik diaballik;

    public ActionsControleur(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.actionsVue = new ActionsVue(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public ActionsVue getActionsVue() {
        return actionsVue;
    }

    public void actionMenu() {
        if (Dialogs.dialogConfirmation("Vous allez quitter le jeu. La partie sera perdue. Voulez-vous continuer?")) {
            diaballik.showSceneMenu();
        }
    }

    public void actionSauvegarderJeu(String directory) {
        String filename;
        final FileChooser fileChooser = new FileChooser();

        final File saveDir = new File("." + directory);
        if (!saveDir.exists())
            saveDir.mkdir();

        fileChooser.setInitialDirectory(saveDir);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Diaballik Sauvegarde", "*.txt"));
        File file = fileChooser.showSaveDialog(this.diaballik.stage);
        if (file != null) {
            filename = file.getAbsolutePath();

            if (!filename.endsWith(".txt"))
                filename += ".txt";

            System.out.println("Sauvegarde vers " + filename);
            this.jeu.sauvegarde(filename);
        }
    }

    public void actionAntijeu() {
        String resAJ = jeu.antijeu();
        if (!resAJ.isEmpty())
            Dialogs.montrerAntijeu(resAJ);
    }

    public void actionParametres() {
        Dialogs.montrerParametres(jeu.cp);
    }

    public void actionDefaire() {
        jeu.defaire();
    }

    public void actionRefaire() {
        jeu.refaire();
    }
}
