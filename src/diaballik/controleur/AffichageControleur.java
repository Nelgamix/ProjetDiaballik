package diaballik.controleur;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.vue.AffichageVue;

public class AffichageControleur {
    private final Diaballik diaballik;

    private final Jeu jeu;
    private final AffichageVue affichageVue;

    public AffichageControleur(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.affichageVue = new AffichageVue(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public AffichageVue getAffichageVue() {
        return affichageVue;
    }
}
