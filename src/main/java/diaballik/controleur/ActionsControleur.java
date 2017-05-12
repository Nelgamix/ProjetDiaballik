package diaballik.controleur;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.vue.ActionsVue;
import diaballik.vue.Dialogs;

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

    public void actionSauvegarderJeu(String filename) {
        final File saveDir = new File(Diaballik.DOSSIER_SAUVEGARDES);
        if (!saveDir.exists())
            saveDir.mkdir();

        if (!filename.endsWith(Diaballik.EXTENSION_SAUVEGARDE))
            filename += Diaballik.EXTENSION_SAUVEGARDE;

        System.out.println("Sauvegarde vers " + filename);
        this.jeu.sauvegarde(Diaballik.DOSSIER_SAUVEGARDES + "/" + filename);
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
