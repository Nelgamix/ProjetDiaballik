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
        this.tour = 0;

        this.joueurs[0] = new Joueur(this, Joueur.COULEUR_VERT);
        this.joueurs[1] = new Joueur(this, Joueur.COULEUR_ROUGE);

        classNotifyObservers();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    private void classNotifyObservers() {
        this.setChanged();
        this.notifyObservers();
    }
}
