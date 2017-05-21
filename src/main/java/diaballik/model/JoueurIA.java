package diaballik.model;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class JoueurIA extends Joueur {
    private final JoueurIA t;
    private ArrayList<Action> actionsARejouer = new ArrayList<>();

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

                    //for (int i = 0; i < r.nextInt(DEPLACEMENTS_MAX + PASSES_MAX) + 1; i++) {
                    for (int i = 0; i < 3; i++) {
                        jouerFacile();
                        Thread.sleep(1000);
                    }

                    Platform.runLater(t::finTour);
                    return null;
                }
            };
        }
    };

    private Service<Void> sRejouer = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1500);

                    for (Action a : actionsARejouer) {
                        setActionAJouer(a);
                        Platform.runLater(() -> {
                            t.jouer();
                            t.finAction();
                        });
                        Thread.sleep(1000);
                    }

                    Platform.runLater(t::finTour);
                    actionsARejouer.clear();
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

    public void rejouer(ArrayList<Action> actions) {
        actionsARejouer.addAll(actions);

        sRejouer.restart();
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

    private void enumConfigs(Terrain configCourante) {
        //ArrayList<Pion> pp;
        int couleur = getCouleur();
        HashMap<String, String> H;
        Terrain c3, c2, configPossible;
        ArrayList<Pion> pionsNonTraites = new ArrayList<>(Arrays.asList(configCourante.getPionsDe(couleur)));

        for (Pion p1 : pionsNonTraites) {
            if (p1.aLaBalle()) {
                for (Pion i : configCourante.getPassesPossibles(p1)) {
                    configPossible = new Terrain(configCourante);
                    passe(p1, i, configPossible);
                    //ajouter(H,L,configPossible)
                    c2 = new Terrain(configPossible);
                    for (Pion p2 : c2.getPionsDe(couleur)) {
                        for (Case m : configPossible.getDeplacementsPossibles(p2)) {
                            c2 = new Terrain(configPossible);
                            deplacement(p2, m, c2);
                            //ajouter(H,L,c2)
                            for (Pion p3 : c2.getPionsDe(couleur)) {
                                for (Case m2 : c2.getDeplacementsPossibles(p3)) {
                                    c3 = new Terrain(c2);
                                    deplacement(p3, m2, c3);
                                    //ajouter(H,L,c3)
                                }
                            }
                        }
                    }
                }
            } else {
                pionsNonTraites.remove(p1);
                for (Case m : configCourante.getDeplacementsPossibles(p1)) {
                    configPossible = new Terrain(configCourante);
                    deplacement(p1, m, configPossible);
                    //ajouter(H,L,configPossible)
                    if (configPossible.getPassesPossibles(configPossible.getPionALaBalle(couleur)).contains(p1)) {
                        c2 = new Terrain(configPossible);
                        passe(configPossible.getPionALaBalle(couleur), p1, c2);
                        //ajouter(H,L,c2)
                    }
                    for (Pion p2 : pionsNonTraites) {
                        for (Case m2 : configPossible.getDeplacementsPossibles(p2)) {
                            c2 = new Terrain(configPossible);
                            deplacement(p2, m2, c2);
                            //ajouter(H,L,c2)
                            if (c2.getPassesPossibles(c2.getPionALaBalle(couleur)).contains(p2)) {
                                c3 = new Terrain(c2);
                                passe(c2.getPionALaBalle(couleur), p2, c3);
                                //ajouter(H,L,c3)
                            }
                        }
                    }
                }
            }
        }
    }

    private void ajouter() {
        /// ??
    }

    private void evalPosition() {
        int[] vals = {5, 12, 21, 32, 45, 60, 77};
        int valTotal = 0;

        Case c;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                c = jeu.getTerrain().getCaseSur(new Point(i, j));

                if (c.pionPresent()) {
                    valTotal += vals[i];
                } else {
                    valTotal += vals[i];
                    valTotal *= 1.2;
                }
            }
        }
    }

    private void passe(Pion e, Pion r, Terrain config) {
        Pion eDansConfig = config.getCaseSur(e.getPosition().getPoint()).getPion();
        Pion rDansConfig = config.getCaseSur(r.getPosition().getPoint()).getPion();

        eDansConfig.passe(rDansConfig);
    }

    private void deplacement(Pion p, Case c, Terrain config) {
        Pion pDansConfig = config.getCaseSur(p.getPosition().getPoint()).getPion();
        Case cDansConfig = config.getCaseSur(c.getPoint());

        pDansConfig.deplacer(cDansConfig);
    }
}
