package diaballik.controller;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.view.AffichageView;

public class AffichageController {
    private final Diaballik diaballik;

    private final Jeu jeu;
    private final AffichageView affichageView;

    public AffichageController(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.jeu = diaballik.getJeu();
        this.affichageView = new AffichageView(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public AffichageView getAffichageView() {
        return affichageView;
    }
}
