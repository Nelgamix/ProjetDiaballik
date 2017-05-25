package diaballik.controleur;

import diaballik.Diaballik;
import diaballik.model.ConfigurationTerrain;
import diaballik.model.IA;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.scene.SceneJeu;
import diaballik.vue.ActionsVue;
import diaballik.vue.Dialogs;

import java.io.File;

public class ActionsControleur {
    private final ActionsVue actionsVue;
    private final SceneJeu sceneJeu;

    public ActionsControleur(SceneJeu sceneJeu) {
        this.sceneJeu = sceneJeu;
        this.actionsVue = new ActionsVue(this);
    }

    public Jeu getJeu() {
        return sceneJeu.getJeu();
    }

    public ActionsVue getActionsVue() {
        return actionsVue;
    }

    public void actionFinTour() {
        getJeu().getHistorique().ecraserFinHistorique();
        getJeu().getJoueurActuel().finTour();
    }
    public void actionAccueil() {
        if (Dialogs.dialogConfirmation("Vous allez quitter le jeu.\nLa partie sera perdue.\nVoulez-vous revenir à l'accueil?")) {
            sceneJeu.retourMenu();
        }
    }
    public void actionSauvegarderJeu(String filename) {
        final File saveDir = new File(Diaballik.DOSSIER_SAUVEGARDES);
        if (!saveDir.exists())
            saveDir.mkdir();

        if (!filename.endsWith(Diaballik.EXTENSION_SAUVEGARDE))
            filename += Diaballik.EXTENSION_SAUVEGARDE;

        System.out.println("Sauvegarde vers " + filename);
        getJeu().sauvegarde(Diaballik.DOSSIER_SAUVEGARDES + "/" + filename);
    }
    public void actionAntijeu() {
        String resAJ = getJeu().antijeu();
        if (!resAJ.isEmpty())
            Dialogs.montrerAntijeu(resAJ);
    }
    public void actionParametres() {
        actionsVue.montrerPopupParametres();
    }
    public void actionDefaire() {
        getJeu().defaire();
    }
    public void actionRefaire() {
        getJeu().refaire();
    }
    public void actionMeilleurCoup() {
        Joueur ja = getJeu().getJoueurActuel();
        ConfigurationTerrain act = new ConfigurationTerrain(getJeu().getTerrain());
        ConfigurationTerrain ct = IA.meilleurTour(act, ja.getCouleur(), ja.getDeplacementsRestants(), ja.getPassesRestantes());

        if (ct.getActions().size() > 0)
            System.out.println("La meilleure action est " + ct.getActions().get(0));
        else
            System.out.println("Aucune action restante, passer le tour.");
    }
}
