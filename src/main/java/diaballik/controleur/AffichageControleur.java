package diaballik.controleur;

import diaballik.model.Jeu;
import diaballik.scene.SceneJeu;
import diaballik.vue.AffichageVue;

public class AffichageControleur {
    private final SceneJeu sceneJeu;
    private final AffichageVue affichageVue;

    public AffichageControleur(SceneJeu sceneJeu) {
        this.sceneJeu = sceneJeu;
        this.affichageVue = new AffichageVue(this);
    }

    public Jeu getJeu() {
        return sceneJeu.getJeu();
    }

    public AffichageVue getAffichageVue() {
        return affichageVue;
    }

    public void finTour() {
        if (!getJeu().getJoueurActuel().estUnJoueurReseau())
            getJeu().getJoueurActuel().finTour();
    }
}
