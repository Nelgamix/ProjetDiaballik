package diaballik.model;

import java.util.ArrayList;
import java.util.Observable;

public class Jeu extends Observable {
    private class Action {
        Pion pion;
        int action;
        Case caseAvant;

        public Action(Pion pion, int action, Case caseAvant) {
            this.pion = pion;
            this.action = action;
            this.caseAvant = caseAvant;
        }

        public Pion getPion() {
            return pion;
        }

        public int getAction() {
            return action;
        }

        public Case getCaseAvant() {
            return caseAvant;
        }

        @Override
        public String toString() {
            return action + ": " + pion + " était sur " + caseAvant;
        }
    }

    private final Terrain terrain;
    private final Joueur[] joueurs;

    private final ArrayList<Action> historique = new ArrayList<>();

    private int tour;
    private int joueurActuel;

    public final static int NOMBRE_JOUEURS = 2;

    public Jeu() {
        this.terrain = new Terrain("terrain.txt");
        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.tour = 1;
        this.joueurActuel = 0;

        this.joueurs[0] = new Joueur(this, Joueur.JOUEUR_VERT);
        this.joueurs[0].setNom("Espece de FDP");
        this.joueurs[1] = new Joueur(this, Joueur.JOUEUR_ROUGE);
        this.joueurs[1].setNom("Pavid la dute");

        updateListeners();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    // Effectue un déplacement (le pion p se déplace sur la case c)
    public void deplacement(Pion p, Case c) {
        if (!p.aLaBalle() && deplacementPossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_DEPLACEMENT)) {
            System.out.println("Déplacement!");

            Case cv = p.getPosition();

            p.deplacer(c);

            historique.add(new Action(p, Joueur.ACTION_DEPLACEMENT, cv));

            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_DEPLACEMENT)) {
                changerTour();
            }

            updateListeners();
        }
    }

    // Vérifie la possibilité d'un déplacement (uniquement par rapport aux coordonnées)
    private boolean deplacementPossible(Case c1, Case c2) {
        Point p = c1.getPoint();
        Point p2 = c2.getPoint();

        // On cherche un point commun (x ou y)
        if (p.getY() == p2.getY()) {
            if (Math.abs(p.getX() - p2.getX()) == 1) { // il faut un différentiel de 1 pour qu'il soit à côté
                return true;
            }
        } else if (p.getX() == p2.getX()) {
            if (Math.abs(p.getY() - p2.getY()) == 1) { // il faut un différentiel de 1 pour qu'il soit à côté
                return true;
            }
        }

        return false;
    }

    // Vérifie la possibilité d'une passe (uniquement dans les axes, la couleur n'est pas vérifiée, ...)
    private boolean passePossible(Case c1, Case c2) {
        Point p = c1.getPoint();
        Point p2 = c2.getPoint();

        // check alignement
        if (p.getX() == p2.getX()) { // en ligne
            int yMax = Math.max(p.getY(), p2.getY());
            int yMin = Math.min(p.getY(), p2.getY());

            for (int y = yMax - 1; y > yMin; y--) {
                Pion pionPresent = getTerrain().getCaseAt(new Point(p.getX(), y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (p.getY() == p2.getY()) { // colonne
            int xMax = Math.max(p.getX(), p2.getX());
            int xMin = Math.min(p.getX(), p2.getX());

            for (int x = xMax - 1; x > xMin; x--) {
                Pion pionPresent = getTerrain().getCaseAt(new Point(x, p.getY())).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (Math.abs(p.getX() - p2.getX()) == Math.abs(p.getY() - p2.getY())) { // diagonale
            int xMax = Math.max(p.getX(), p2.getX());
            int xMin = Math.min(p.getX(), p2.getX());
            int yMax = Math.max(p.getY(), p2.getY());
            //int yMin = Math.min(p.getY(), p2.getY());

            int y = yMax;
            for (int x = xMax - 1; x > xMin; x--) {
                y--;
                Pion pionPresent = getTerrain().getCaseAt(new Point(x, y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    // Effectue une passe d'un pion p vers un pion positionné sur une case c
    public void passe(Pion p, Case c) {
        if (p.aLaBalle() && passePossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_PASSE)) {
            System.out.println("Passe!");

            Case cv = p.getPosition();

            p.passe(c);

            historique.add(new Action(c.getPion(), Joueur.ACTION_PASSE, cv));

            if (partieTerminee()) {
                // la partie est terminée (le vainqueur est joueurActuel())
            }

            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_PASSE)) {
                changerTour();
            }

            updateListeners();
        }
    }

    // retourne vrai si la partie est terminée (le vainqueur est le joueur actuel)
    private boolean partieTerminee() {
        return false;
    }

    // Change le tour actuel (change aussi le joueur actuel)
    public void changerTour() {
        historique.clear();
        getJoueurActuel().reset_actions();
        this.tour++;
        joueurActuel = (joueurActuel + 1 >= Jeu.NOMBRE_JOUEURS ? 0 : joueurActuel + 1);
        updateListeners();
        //System.out.println("Tour " + tour + ": " + getJoueurActuel().getNom());
    }

    public Joueur getJoueurActuel() {
        return this.joueurs[(tour-1) % NOMBRE_JOUEURS];
    }

    public ArrayList<Action> getHistorique() {
        return historique;
    }

    private void updateListeners() {
        this.setChanged();
        this.notifyObservers();
    }

    public int getTour() {
        return tour;
    }

    public void rollwack() {
        if (historique.size() == 0) return;

        Action a = historique.get(historique.size() - 1);
        historique.remove(historique.size() - 1);

        switch (a.getAction()) {
            case Joueur.ACTION_DEPLACEMENT:
                a.getPion().deplacer(a.getCaseAvant());
                getJoueurActuel().plusAction(Joueur.ACTION_DEPLACEMENT);
                break;
            case Joueur.ACTION_PASSE:
                a.getPion().passe(a.getCaseAvant());
                getJoueurActuel().plusAction(Joueur.ACTION_PASSE);

                break;
            default:
                System.err.println("Action non reconnue");
        }

        updateListeners();
    }
}
