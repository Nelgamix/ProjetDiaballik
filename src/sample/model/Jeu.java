package sample.model;

import java.util.Observable;

public class Jeu extends Observable {
    private final Terrain terrain;

    public final Joueur[] joueurs;

    public final static int NOMBRE_JOUEURS = 2;
    public int tour;

    public Jeu() {
        this.terrain = new Terrain();
        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.tour = 0;

        this.joueurs[0] = new Joueur(this, Joueur.COULEUR_VERT);
        this.joueurs[1] = new Joueur(this, Joueur.COULEUR_ROUGE);

        tamer();
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    private void tamer() {
        this.setChanged();
        this.notifyObservers();
    }

    public void deplacerPion(Pion pion, Point point) {
        this.terrain.getCase(point).setPion(null);
        this.terrain.getCase(point).setPion(pion);

        tamer();
    }
}
