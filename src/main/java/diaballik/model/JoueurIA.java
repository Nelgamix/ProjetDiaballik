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

                    for (int i = 0; i < r.nextInt(DEPLACEMENTS_MAX + PASSES_MAX) + 1; i++) {
                        jouerFacile();
                        Thread.sleep(1000);
                    }

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
        else
            return false;

        return true;
    }

    void jouerIA() {
        switch (type) {
            case DIFFICULTE_FACILE:
                sJouerFacile.restart();
                break;
            case DIFFICULTE_MOYEN:
                // ??
                break;
            case DIFFICULTE_DIFFICILE:
                // minimax
                break;
            default:
                System.err.println("(JoueurIA.jouerIA) Difficulté non reconnue.");
                break;
        }
    }

    private boolean jouerFacile() {
        boolean valide = false;
        Pion a = null;
        Case b;
        int aType;

        while (!valide) {
            a = jeu.getTerrain().getPionDe(getCouleur(), r.nextInt(NOMBRE_PIONS));
            valide = true;
            if (a.aLaBalle() && !peutPasser()) {
                valide = false;
            } else if (!a.aLaBalle() && !peutDeplacer()) {
                valide = false;
            }
        }

        if (a.aLaBalle()) {
            ArrayList<Pion> pp = jeu.getPassesPossibles(a);
            b = pp.get(r.nextInt(pp.size())).getPosition();
            aType = Action.PASSE;
        } else {
            ArrayList<Case> pp = jeu.getDeplacementsPossibles(a);
            b = pp.get(r.nextInt(pp.size()));
            aType = Action.DEPLACEMENT;
        }

        Action action = new Action(a.getPosition(), aType, b, jeu.getTour());

        setActionAJouer(action);

        Platform.runLater(() -> {
            t.jouer();
            t.finAction();
        });

        return true;
    }
}
