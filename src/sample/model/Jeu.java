package sample.model;

import java.util.Observable;

public class Jeu extends Observable {
    private final Terrain terrain;
    private final Joueur[] joueurs;

    private int tour;

    public final static int NOMBRE_JOUEURS = 2;

    public Jeu() {
        this.terrain = new Terrain();
        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.tour = 1;

        this.joueurs[0] = new Joueur(this, Joueur.COULEUR_VERT);
        this.joueurs[1] = new Joueur(this, Joueur.COULEUR_ROUGE);

        classNotifyObservers();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void deplacement(Pion p, Case c) {
        p.deplacer(c);
        if (!this.getJoueurActuel().moinsAction()) {
            this.tour++;
            System.out.println("Tour " + tour);
        }
    }

    private Joueur getJoueurActuel() {
        return this.joueurs[tour-1 % NOMBRE_JOUEURS];
    }

    private void classNotifyObservers() {
        this.setChanged();
        this.notifyObservers();
    }
}
