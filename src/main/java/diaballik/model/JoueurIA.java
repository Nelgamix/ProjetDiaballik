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

        Integer[] eval = new Integer[Jeu.NOMBRE_JOUEURS];

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
            int pts = 0;
            Point pBalle, pCase1, pCase2, pCase3;

            if (couleur == Joueur.VERT) {
                pBalle = terrain.getPionALaBalle(couleur).getPosition().getPoint();
                pCase1 = new Point(pBalle.getX(), Terrain.HAUTEUR - 1);
                pCase2 = new Point(pBalle.getX() - (Terrain.HAUTEUR - 1 - pBalle.getY()), Terrain.HAUTEUR - 1);
                pCase3 = new Point(pBalle.getX() + (Terrain.HAUTEUR - 1 - pBalle.getY()), Terrain.HAUTEUR - 1);
            } else {
                pBalle = terrain.getPionALaBalle(couleur).getPosition().getPoint();
                pCase1 = new Point(pBalle.getX(), 0);
                pCase2 = new Point(pBalle.getX() + pBalle.getY(), 0);
                pCase3 = new Point(pBalle.getX() - pBalle.getY(), 0);
            }

            // pour pCase1
            if (
                        terrain.passePossible(pBalle, pCase1, couleur)
                    &&  checkPorteeDe(pCase1, couleur)
            ) {
                //System.out.println("Attaque case " + pCase1);
                pts = 150;
            }

            if (
                        pCase2.estDansTerrain()
                    &&  terrain.passePossible(pBalle, pCase2, couleur)
                    &&  checkPorteeDe(pCase2, couleur)
            ) {
                //System.out.println("Attaque case " + pCase2);
                pts = 150;
            }

            if (
                        pCase3.estDansTerrain()
                    &&  terrain.passePossible(pBalle, pCase3, couleur)
                    &&  checkPorteeDe(pCase3, couleur)
            ) {
                //System.out.println("Attaque case " + pCase3);
                pts = 150;
            }

            return pts;
        }

        private boolean aPorteeDe(Pion pion, Point point, int couleur) {
            Point pPion = pion.getPosition().getPoint();
            Case objectif = terrain.getCaseSur(point);
            int distance = Math.abs(point.getX() - pPion.getX()) + Math.abs(point.getY() - pPion.getY());

            switch (distance) {
                case 0:
                    return true;
                case 1:
                    return !(objectif.getPion() != null && objectif.getPion().getCouleur() != couleur);
                case 2:
                    if (objectif.getPion() != null && objectif.getPion().getCouleur() != couleur) return false;

                    Point tmp = new Point(pPion);
                    ArrayList<Point> pts = new ArrayList<>();

                    if (tmp.getX() > point.getX()) {
                        pts.add(new Point(tmp.getX() - 1, tmp.getY()));
                    } else if (tmp.getX() < point.getX()) {
                        pts.add(new Point(tmp.getX() + 1, tmp.getY()));
                    }

                    if (tmp.getY() > point.getY()) {
                        pts.add(new Point(tmp.getX(), tmp.getY() - 1));
                    } else if (tmp.getY() < point.getY()) {
                        pts.add(new Point(tmp.getY(), tmp.getY() + 1));
                    }

                    for (Point p : pts)
                        if (terrain.getCaseSur(p).getPion() == null)
                            return true;
                    return false;
                default: // Distance > 2
                    return false;
            }
        }
        private boolean checkPorteeDe(Point point, int couleur) {
            if (terrain.getCaseSur(point).getPion() != null && terrain.getCaseSur(point).getPion().getCouleur() != couleur)
                return false;

            for (Pion p : terrain.getPionsDe(couleur)) {
                if (!p.aLaBalle() && aPorteeDe(p, point, couleur)) {
                    return true;
                }
            }

            return false;
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

        int getEval(int couleur) {
            if (eval[couleur] == null)
                return (eval[couleur] = eval(couleur));
            else return eval[couleur];
        }

        @Override
        public boolean equals(Object obj) {
            return terrain.equals(((Configuration)obj).terrain);
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
        if (!sJouerFacile.isRunning())
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
                // minimax
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
        Configuration c = new Configuration(jeu.getTerrain());

        HashSet<Configuration> cs = enumAll(c);
        ArrayList<Configuration> max = new ArrayList<>();
        int evalMax = Integer.MIN_VALUE;
        int evalAct;
        for (Configuration ct : cs) {
            if (ct.gagne(Joueur.ROUGE)) {
                max.clear();
                max.add(ct);

                break;
            }

            evalAct = evalConfig(ct, getCouleur());
            if (evalAct > evalMax) {
                max.clear();
                max.add(ct);
                evalMax = evalAct;
            } else if (evalAct == evalMax)
                max.add(ct);
        }

        Configuration confChoisie = max.get(r.nextInt(max.size()));

        /*System.out.println("** Meilleur config = " + evalMax);
        System.out.println(confChoisie);*/

        for (Action a : confChoisie.actions) {
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
        Configuration c = new Configuration(jeu.getTerrain());

        System.out.println("Début minimax w/ alpha-beta cutoff...");
        Configuration cMax;
        cMax = max(c, 0, 2, Integer.MAX_VALUE, (getCouleur()+1)%2);
        System.out.println(cMax);
        System.out.println("End.");

        int n = 0;
        for (Action a : cMax.actions) {
            if (n++ == 3) break;

            setActionAJouer(convert(a, jeu.getTerrain()));
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

    private int evalConfig(Configuration c, int couleur) {
        int eval;
        int couleurAdv = (couleur + 1) % 2;

        eval = c.eval(couleur);
        eval -= c.eval(couleurAdv);
        eval += c.attaque(couleur);
        eval -= (c.attaque(couleurAdv) * 3);

        return eval;
    }
    private Configuration min(Configuration config, int depth, int maxDepth, int valMin, int couleur) {
        Configuration min = null, tmp;
        int evalMin = Integer.MAX_VALUE, evalAct;
        int couleurAct = (couleur+1)%2;

        if (depth == maxDepth || config.gagne(couleurAct))
            return config;

        for (Configuration c : enumAll(config)) {
            tmp = max(c, depth + 1, maxDepth, evalMin, couleurAct);
            evalAct = evalConfig(tmp, couleurAct);

            if (evalAct < evalMin) { // si on trouve une config avec une valeur inférieure
                evalMin = evalAct;
                min = tmp;
            }

            if (evalMin < valMin) { // cutoff si la val trouvée est inférieure au min
                //System.out.println("Cutoff min");
                return min;
            }
        }

        return min;
    }
    private Configuration max(Configuration config, int depth, int maxDepth, int valMax, int couleur) {
        Configuration max = null, tmp;
        int evalMax = Integer.MIN_VALUE, evalAct;
        int couleurAct = (couleur+1)%2;

        if (depth == maxDepth || config.gagne(couleurAct))
            return config;

        for (Configuration c : enumAll(config)) {
            tmp = min(c, depth + 1, maxDepth, evalMax, couleurAct);
            evalAct = evalConfig(tmp, couleurAct);

            if (evalAct > evalMax) {
                evalMax = evalAct;
                max = tmp;
            }

            if (valMax < evalMax) {
                //System.out.println("Cutoff max");
                return max;
            }
        }

        return max;
    }

    private HashSet<Configuration> enumAll(Configuration config) {
        HashSet<Configuration> H = new HashSet<>();
        H.add(config);

        // Déplacement en 1
        for (Configuration c : enumDeplacements(config)) {
            H.add(c);

            // Passe en 2
            for (Configuration c2 : enumPasses(c)) {
                H.add(c2);

                // Déplacement en 3
                H.addAll(enumDeplacements(c2));
            }

            // Déplacement en 2
            for (Configuration c2 : enumDeplacements(c)) {
                H.add(c2);

                // Passe en 3
                H.addAll(enumPasses(c2));
            }
        }

        // Passe en 1
        for (Configuration c : enumPasses(config)) {
            H.add(c);

            // Déplacement en 2
            for (Configuration c2 : enumDeplacements(c)) {
                H.add(c2);

                // Déplacement en 3
                H.addAll(enumDeplacements(c2));
            }
        }

        //System.out.println(H.size() + " configurations trouvées");
        /*for (Configuration c : H) {
            System.out.println(c);
        }*/

        return H;
    }
    private ArrayList<Configuration> enumDeplacements(Configuration config) {
        Terrain terrain = config.terrain;
        ArrayList<Configuration> tmp = new ArrayList<>();

        Configuration c;
        for (Pion p : terrain.getPionsDe(getCouleur())) {
            if (!p.aLaBalle()) {
                for (Case m : terrain.getDeplacementsPossibles(p)) {
                    c = new Configuration(config);
                    c.addAction(new Action(p.getPosition(), Action.DEPLACEMENT, m));
                    c.deplacement();
                    tmp.add(c);
                }
            }
        }

        return tmp;
    }
    private ArrayList<Configuration> enumPasses(Configuration config) {
        Terrain terrain = config.terrain;
        ArrayList<Configuration> tmp = new ArrayList<>();

        Configuration c;
        for (Pion p : terrain.getPionsDe(getCouleur())) {
            if (p.aLaBalle()) {
                for (Pion p2 : terrain.getPassesPossibles(p)) {
                    c = new Configuration(config);
                    c.addAction(new Action(p.getPosition(), Action.PASSE, p2.getPosition()));
                    c.passe();
                    tmp.add(c);
                }
            }
        }

        return tmp;
    }
}
