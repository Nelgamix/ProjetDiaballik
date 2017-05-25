package diaballik.model;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigurationTerrain {
    private Terrain terrain;
    private ArrayList<Action> actions;

    private Integer[] eval = new Integer[Jeu.NOMBRE_JOUEURS];
    private Integer[] attaque = new Integer[Jeu.NOMBRE_JOUEURS];

    private Integer[] fullEval = new Integer[Jeu.NOMBRE_JOUEURS];

    private Boolean gagne = null;

    ConfigurationTerrain(ConfigurationTerrain configuration) {
        terrain = new Terrain(configuration.terrain);
        actions = new ArrayList<>();

        for (Action a : configuration.actions) {
            addAction(a);
        }
    }
    public ConfigurationTerrain(Terrain terrain) {
        this.terrain = terrain;
        this.actions = new ArrayList<>();
    }
    ConfigurationTerrain(Terrain terrain, Action action) {
        this(terrain);
        addAction(action);
    }

    boolean gagne(int couleur) {
        if (gagne == null) return (gagne = terrain.partieTerminee(couleur));
        else return gagne;
    }

    private int eval(int couleur) {
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
    private int attaque(int couleur) {
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
    private void deplacement(Action action) {
        Pion p = action.getCaseAvant().getPion();

        p.deplacer(action.getCaseApres());
    }

    void passe() {
        passe(actions.get(actions.size() - 1));
    }
    private void passe(Action action) {
        Pion p = action.getCaseAvant().getPion();
        Pion p2 = action.getCaseApres().getPion();

        p.passe(p2);
    }

    private int getEval(int couleur) {
        if (eval[couleur] == null)
            return (eval[couleur] = eval(couleur));
        else return eval[couleur];
    }
    private int getAttaque(int couleur) {
        if (attaque[couleur] == null)
            return (attaque[couleur] = attaque(couleur));
        else return attaque[couleur];
    }

    int fullEval(int couleur) {
        if (fullEval[couleur] == null) {
            int eval;
            int couleurAdv = (couleur+1)%2;

            eval = getEval(couleur);
            eval -= getEval(couleurAdv);
            eval += getAttaque(couleur);
            eval -= (getAttaque(couleurAdv) * 3);

            return (fullEval[couleur] = eval);
        } else {
            return fullEval[couleur];
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }
    public ArrayList<Action> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigurationTerrain && terrain.equals(((ConfigurationTerrain) obj).terrain);
    }

    @Override
    public int hashCode() {
        return terrain.hashCode();
    }

    @Override
    public String toString() {
        return "Eval " + fullEval(Joueur.ROUGE) + "\n" + terrain.toString() + Arrays.toString(actions.toArray());
    }
}
