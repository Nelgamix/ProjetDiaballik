package sample.model;

import java.util.Observable;

public class Jeu extends Observable {
    private final Terrain terrain;
    private final Joueur[] joueurs;

    private int tour;
    private int joueurActuel;

    public final static int NOMBRE_JOUEURS = 2;

    public Jeu() {
        this.terrain = new Terrain();
        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.tour = 1;
        this.joueurActuel = 0;

        this.joueurs[0] = new Joueur(this, Joueur.COULEUR_VERT);
        this.joueurs[0].setNom("Joueur A");
        this.joueurs[1] = new Joueur(this, Joueur.COULEUR_ROUGE);
        this.joueurs[1].setNom("Joueur B");

        updateListeners();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void deplacement(Pion p, Case c) {
        if (deplacementPossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_DEPLACEMENT)) {
            System.out.println("Déplacement!");

            p.deplacer(c);
            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_DEPLACEMENT)) {
                changerTour();
            }
        }
    }

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
            int yMin = Math.min(p.getY(), p2.getY());

            for (int x = xMax - 1; x > xMin; x--) {
                for (int y = yMax - 1; y > yMin; y--) {
                    Pion pionPresent = getTerrain().getCaseAt(new Point(x, y)).getPion();
                    if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void passe(Pion p, Case c) {
        if (p.aLaBalle() && passePossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_PASSE)) {
            System.out.println("Passe!");

            p.passe(c);
            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_PASSE)) {
                changerTour();
            }
        }
    }

    public void changerTour() {
        getJoueurActuel().reset_actions();
        this.tour++;
        joueurActuel = (joueurActuel + 1 >= Jeu.NOMBRE_JOUEURS ? 0 : joueurActuel + 1);
        updateListeners();
        //System.out.println("Tour " + tour + ": " + getJoueurActuel().getNom());
    }

    public Joueur getJoueurActuel() {
        return this.joueurs[(tour-1) % NOMBRE_JOUEURS];
    }

    private void updateListeners() {
        this.setChanged();
        this.notifyObservers();
    }
}
