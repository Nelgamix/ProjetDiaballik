package diaballik.controller;

import diaballik.model.Jeu;
import diaballik.view.AffichageView;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 03.
 * Created by Nico (22:18).
 */
public class AffichageController {
    private final Jeu jeu;
    private final AffichageView affichageView;

    public AffichageController(Jeu jeu) {
        this.jeu = jeu;
        this.affichageView = new AffichageView(this);
    }

    public Jeu getJeu() {
        return jeu;
    }

    public AffichageView getAffichageView() {
        return affichageView;
    }
}
