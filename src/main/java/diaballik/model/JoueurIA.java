package diaballik.model;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class JoueurIA extends Joueur {
    public class Configuration {
        Terrain terrain;
        ArrayList<Action> actions;

        Configuration(Configuration configuration) {
            terrain = new Terrain(configuration.terrain);
            actions = new ArrayList<>();

            for (Action a : configuration.actions) {
                addAction(a);
            }
        }
        Configuration(Terrain terrain) {
            this.terrain = terrain;
            this.actions = new ArrayList<>();
        }
        Configuration(Terrain terrain, Action action) {
            this(terrain);
            addAction(action);
        }

        boolean gagne(int couleur) {
            return terrain.partieTerminee(couleur);
        }

        int eval(int couleur) {
            int[] vals = {5, 12, 21, 32, 45, 60, 77};
            int valTotal = 0;

            if (couleur == Joueur.ROUGE) {
                for (int i = 0; i < vals.length / 2; i++) { // reverse because the red players want to go UP
                    int temp = vals[i];
                    vals[i] = vals[vals.length - i - 1];
                    vals[vals.length - i - 1] = temp;
                }
                // ici, vals vaut
                //int[] vals = {77, 60, 45, 32, 21, 12, 5};
            }

            for (Pion p : terrain.getPionsDe(couleur)) {
                int v = vals[p.getPosition().getPoint().getY()];
                valTotal += (p.aLaBalle() ? v * 1.2 : v);
            }

            return valTotal;
        }

        int attaque(int couleur) {
            if (couleur == Joueur.VERT) {
                Point pBalle = terrain.getPionALaBalle(couleur).getPosition().getPoint();
                Point pCase1 = new Point(pBalle.getX(), Terrain.HAUTEUR - 1);
                Point pCase2 = new Point(pBalle.getX() + (Terrain.HAUTEUR - 1 - pBalle.getY()), Terrain.HAUTEUR - 1);
                Point pCase3 = new Point(pBalle.getX() + (Terrain.HAUTEUR - 1 + pBalle.getY()), Terrain.HAUTEUR - 1);

                // pour pCase1
                if (terrain.passePossible(pBalle, pCase1, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase1)) {
                            System.out.println("Attaque Vert détectée");
                            return 150;
                        }
                    }
                }

                if (pCase2.estDansTerrain() && terrain.passePossible(pBalle, pCase2, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase2)) {
                            System.out.println("Attaque Vert détectée");
                            return 150;
                        }
                    }
                }

                if (pCase3.estDansTerrain() && terrain.passePossible(pBalle, pCase3, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase3)) {
                            System.out.println("Attaque Vert détectée");
                            return 150;
                        }
                    }
                }

                return 0;
            } else {
                Point pBalle = terrain.getPionALaBalle(couleur).getPosition().getPoint();
                Point pCase1 = new Point(pBalle.getX(), 0);
                Point pCase2 = new Point(pBalle.getY() + pBalle.getX(), 0);
                Point pCase3 = new Point(pBalle.getY() - pBalle.getX(), 0);

                // pour pCase1
                if (terrain.passePossible(pBalle, pCase1, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase1)) {
                            System.out.println("Attaque Rouge détectée");
                            return 150;
                        }
                    }
                }

                if (pCase2.estDansTerrain() && terrain.passePossible(pBalle, pCase2, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase2)) {
                            System.out.println("Attaque Rouge détectée");
                            return 150;
                        }
                    }
                }

                if (pCase3.estDansTerrain() && terrain.passePossible(pBalle, pCase3, couleur)) {
                    for (Pion p : terrain.getPionsDe(couleur)) {
                        if (aPorteeDe(p, pCase3)) {
                            System.out.println("Attaque Rouge détectée");
                            return 150;
                        }
                    }
                }

                return 0;
            }
        }

        private boolean aPorteeDe(Pion pion, Point point) {
            Point ptmp = pion.getPosition().getPoint();
            return Math.abs((point.getX() - ptmp.getX()) + (point.getY() - ptmp.getY())) <= 2;
        }

        void addAction(Action action) {
            if (action.getAction() == Action.FINTOUR) return;
            Case av = terrain.getCaseSur(action.getCaseAvant().getPoint());
            Case ap = terrain.getCaseSur(action.getCaseApres().getPoint());
            actions.add(new Action(av, action.getAction(), ap));
        }

        void deplacement() {
            deplacement(actions.get(actions.size() - 1));
        }
        void deplacement(Action action) {
            Pion p = action.getCaseAvant().getPion();

            p.deplacer(action.getCaseApres());
        }

        void passe() {
            passe(actions.get(actions.size() - 1));
        }
        void passe(Action action) {
            Pion p = action.getCaseAvant().getPion();
            Pion p2 = action.getCaseApres().getPion();

            p.passe(p2);
        }

        @Override
        public int hashCode() {
            return terrain.hashCode();
        }

        @Override
        public String toString() {
            return terrain.toString() + Arrays.toString(actions.toArray());
        }
    }

    private final JoueurIA t;
    private ArrayList<Action> actionsAJouer = new ArrayList<>();

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
                    for (int i = 0; i < 3; i++) {
                        jouerFacile();
                        Thread.sleep(ATTENTE_ACTION);
                    }

                    Platform.runLater(t::finTour);
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

                    Platform.runLater(t::finTour);
                    return null;
                }
            };
        }
    };

    private Service<Void> sJouer = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (Action a : actionsAJouer) {
                        System.out.println(a);
                        setActionAJouer(a);
                        Platform.runLater(() -> {
                            t.jouer();
                            t.finAction();
                        });
                        Thread.sleep(ATTENTE_ACTION);
                    }

                    Platform.runLater(t::finTour);
                    actionsAJouer.clear();
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
                    while (jeu.getJoueurActuel().estUneIA()) {
                        Platform.runLater(defaire ? jeu::defaire : jeu::refaire);
                        Thread.sleep(ATTENTE_ACTION);
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

    public void jouer(ArrayList<Action> actions) {
        actionsAJouer.addAll(actions);

        sJouer.restart();
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
                sJouerDifficile.restart();
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
    private boolean jouerDifficile() {
        Configuration c = new Configuration(jeu.getTerrain(), new Action(Action.FINTOUR));

        HashSet<Configuration> cs = enumAll(c);
        Configuration max = null;
        int evalMax = Integer.MIN_VALUE;
        int evalAct;
        for (Configuration ct : cs) {
            if (ct.gagne(Joueur.ROUGE)) {
                max = ct;

                break;
            }

            evalAct = ct.eval(Joueur.ROUGE);
            evalAct -= ct.eval(Joueur.VERT);
            evalAct += ct.attaque(Joueur.ROUGE);
            evalAct -= (ct.attaque(Joueur.VERT) * 3);
            if (evalAct > evalMax) {
                max = ct;
                evalMax = evalAct;
            }
        }

        if (max == null) return false;

        System.out.println("** Meilleur config = " + evalMax);
        System.out.println(max);
            System.out.println(max.attaque(Joueur.VERT));

        for (Action a : max.actions) {
            Platform.runLater(() -> {
                setActionAJouer(convert(a, jeu.getTerrain()));
                t.jouer();
                t.finAction();
            });

            try {
                Thread.sleep(ATTENTE_ACTION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    Action convert(Action action, Terrain terrain) {
        Case av = terrain.getCaseSur(action.getCaseAvant().getPoint());
        Case ap = terrain.getCaseSur(action.getCaseApres().getPoint());

        return new Action(av, action.getAction(), ap, jeu.getTour());
    }

    private HashSet<Configuration> enumAll(Configuration config) {
        HashSet<Configuration> H = new HashSet<>();
        ajouter(H, config);

        // Déplacement en 1
        for (Configuration c : enumDeplacements(config)) {
            ajouter(H, c);

            // Passe en 2
            for (Configuration c2 : enumPasses(c)) {
                ajouter(H, c2);

                // Déplacement en 3
                for (Configuration c3 : enumDeplacements(c2)) {
                    ajouter(H, c3);
                }
            }

            // Déplacement en 2
            for (Configuration c2 : enumDeplacements(c)) {
                ajouter(H, c2);

                // Passe en 3
                for (Configuration c3 : enumPasses(c2)) {
                    ajouter(H, c3);
                }
            }
        }

        // Passe en 1
        for (Configuration c : enumPasses(config)) {
            ajouter(H, c);

            // Déplacement en 2
            for (Configuration c2 : enumDeplacements(c)) {
                ajouter(H, c2);

                // Déplacement en 3
                for (Configuration c3 : enumDeplacements(c2)) {
                    ajouter(H, c3);
                }
            }
        }

        System.out.println(H.size() + " configurations trouvées");

        return H;
    }

    private HashSet<Configuration> enumDeplacements(Configuration config) {
        Terrain terrain = config.terrain;
        HashSet<Configuration> tmp = new HashSet<>();

        Configuration c;
        for (Pion p : terrain.getPionsDe(getCouleur())) {
            if (!p.aLaBalle()) {
                for (Case m : terrain.getDeplacementsPossibles(p)) {
                    c = new Configuration(config);
                    c.addAction(new Action(p.getPosition(), Action.DEPLACEMENT, m));
                    c.deplacement();
                    ajouter(tmp, c);
                }
            }
        }

        return tmp;
    }
    private HashSet<Configuration> enumPasses(Configuration config) {
        Terrain terrain = config.terrain;
        HashSet<Configuration> tmp = new HashSet<>();

        Configuration c;
        for (Pion p : terrain.getPionsDe(getCouleur())) {
            if (p.aLaBalle()) {
                for (Pion p2 : terrain.getPassesPossibles(p)) {
                    c = new Configuration(config);
                    c.addAction(new Action(p.getPosition(), Action.PASSE, p2.getPosition()));
                    c.passe();
                    ajouter(tmp, c);
                }
            }
        }

        return tmp;
    }

    private void ajouter(HashSet<Configuration> H, Configuration c) {
        if (!H.contains(c)) {
            System.out.print(c);
            H.add(c);
        }
    }
}
