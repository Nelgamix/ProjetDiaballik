package diaballik.model;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Random;

public class JoueurIA extends Joueur {
    private final JoueurIA t;

    private final Random r = new Random();

    public final static int DIFFICULTE_FACILE = 1;
    public final static int DIFFICULTE_MOYEN = 2;
    public final static int DIFFICULTE_DIFFICILE = 3;

    private Service<Void> sJouerFacile = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1500);
                    jouerFacile();
                    Thread.sleep(500);
                    Platform.runLater(t::finTour);
                    return null;
                }
            };
        }
    };

    public JoueurIA(Jeu jeu, int couleur, int difficulte) {
        super(jeu, couleur);
        t = this;
        this.setType(difficulte);
    }

    public static String parseDifficulte(int difficulte) {
        switch (difficulte) {
            case DIFFICULTE_FACILE:
                return "facile";
            case DIFFICULTE_MOYEN:
                return "moyen";
            case DIFFICULTE_DIFFICILE:
                return "difficile";
            default:
                return "inconnu";
        }
    }

    @Override
    public boolean estUneIA() {
        return true;
    }

    @Override
    public boolean preparerJouer() {
        if (!sJouerFacile.isRunning())
            jouerIA();
        return false;
    }

    void jouerIA() {
        sJouerFacile.restart();
    }

    private boolean jouerFacile() {
        Case c = jeu.getTerrain().getPionDe(getCouleur(), r.nextInt(NOMBRE_PIONS)).getPosition();
        Case d;
        int aType;
        if (c.getPion().aLaBalle()) {
            ArrayList<Pion> pp = jeu.getPassesPossibles(c.getPion());
            d = pp.get(r.nextInt(pp.size())).getPosition();
            aType = Action.PASSE;
        } else {
            ArrayList<Case> pp = jeu.getDeplacementsPossibles(c.getPion());
            d = pp.get(r.nextInt(pp.size()));
            aType = Action.DEPLACEMENT;
        }

        Action a = new Action(c, aType, d, jeu.getTour());

        setActionAJouer(a);
        Platform.runLater(t::jouer);

        return true;
    }
}
