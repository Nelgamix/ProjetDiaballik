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

    public final static int ATTENTE_ACTION = 1000;

    private boolean defaire;

    private Service<Void> sJouerFacile = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    jouerFacile();

                    Platform.runLater(() -> {
                        jeu.antijeu();
                        t.finTour();
                    });

                    return null;
                }
            };
        }
    };

    private Service<Void> sJouerMoyen = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    jouerMoyen();

                    Platform.runLater(() -> {
                        jeu.antijeu();
                        t.finTour();
                    });

                    return null;
                }
            };
        }
    };

    private Service<Void> sJouerDifficile = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    jouerDifficile();

                    Platform.runLater(() -> {
                        jeu.antijeu();
                        t.finTour();
                    });

                    return null;
                }
            };
        }
    };

    private Service<Void> sFaire = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int n = jeu.getHistorique().nombreActions(jeu.getTour()) + 1;
                    int i = 0;
                    while (i++ < n) {
                        Thread.sleep(ATTENTE_ACTION);
                        Platform.runLater(defaire ? jeu::defaire : jeu::refaire);
                    }

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

    public void refaire() {
        defaire = false;
        sFaire.restart();
    }
    public void defaire() {
        defaire = true;
        sFaire.restart();
    }

    @Override
    public boolean estUneIA() {
        return true;
    }

    @Override
    public boolean preparerJouer() {
        if (!sJouerFacile.isRunning() && !sJouerMoyen.isRunning() && !sJouerDifficile.isRunning() && !sFaire.isRunning() && jeu.isRunning())
            jouerIA();
        else
            return false;

        return true;
    }

    private void jouerIA() {
        switch (type) {
            case DIFFICULTE_FACILE:
                sJouerFacile.restart();
                break;
            case DIFFICULTE_MOYEN:
                sJouerMoyen.restart();
                break;
            case DIFFICULTE_DIFFICILE:
                sJouerDifficile.restart();
                break;
            default:
                System.err.println("(JoueurIA.jouerIA) Difficulté non reconnue.");
                break;
        }
    }

    private void jouerFacile() {
        Pion a;
        Case b;
        int action;

        for (int i = 0; i < 3; i++) {
            a = jeu.getTerrain().getPionDe(getCouleur(), r.nextInt(NOMBRE_PIONS));
            if (a.aLaBalle()) {
                ArrayList<Pion> pp = jeu.getPassesPossibles(a);
                if (pp.size() < 1) continue;

                b = pp.get(r.nextInt(pp.size())).getPosition();
                action = Action.PASSE;
            } else {
                ArrayList<Case> pp = jeu.getDeplacementsPossibles(a);
                if (pp.size() < 1) continue;

                b = pp.get(r.nextInt(pp.size()));
                action = Action.DEPLACEMENT;
            }

            setActionAJouer(new Action(a.getPosition(), action, b, jeu.getTour()));

            Platform.runLater(t::jouer);

            try {
                Thread.sleep(ATTENTE_ACTION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void jouerMoyen() {
        ConfigurationTerrain c = new ConfigurationTerrain(jeu.getTerrain());

        ConfigurationTerrain confChoisie = IA.meilleurTour(c, getCouleur());

        /*System.out.println("** Meilleur config = " + evalMax);
        System.out.println(confChoisie);*/

        for (Action a : confChoisie.getActions()) {
            setActionAJouer(convert(a, jeu.getTerrain()));
            Platform.runLater(t::jouer);

            try {
                Thread.sleep(ATTENTE_ACTION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void jouerDifficile() {
        ConfigurationTerrain c = new ConfigurationTerrain(jeu.getTerrain());

        System.out.println("Début minimax w/ alpha-beta cutoff...");
        ConfigurationTerrain cMax;
        cMax = IA.minimax(c, getCouleur());
        System.out.println(cMax);
        System.out.println("End.");

        Action converted;
        for (Action a : cMax.getActions()) {
            converted = convert(a, jeu.getTerrain());

            if (converted.getCaseAvant().getPion().getCouleur() != getCouleur()) break;

            setActionAJouer(converted);
            Platform.runLater(t::jouer);

            try {
                Thread.sleep(ATTENTE_ACTION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Action convert(Action action, Terrain terrain) {
        Case av = terrain.getCaseSur(action.getCaseAvant().getPoint());
        Case ap = terrain.getCaseSur(action.getCaseApres().getPoint());

        return new Action(av, action.getAction(), ap, jeu.getTour());
    }
}
