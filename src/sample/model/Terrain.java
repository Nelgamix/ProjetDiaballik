package sample.model;

import java.util.Observable;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:38).
 */
public class Terrain extends Observable {
    public final Case[][] cases; // Représentation modèle du terrain (Cases)
    public final Joueur[] joueurs;

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    public final static int NOMBRE_JOUEURS = 2;

    public Terrain() {
        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.cases = new Case[HAUTEUR][LARGEUR];

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.cases[i][j] = new Case(null);
            }
        }

        this.joueurs[0] = new Joueur(this, Joueur.COULEUR_VERT);
        this.joueurs[1] = new Joueur(this, Joueur.COULEUR_ROUGE);

        this.setChanged();
        this.notifyObservers();
    }

    public void setPion(Pion pion, int x, int y) {
        this.cases[x][y].setPion(pion);
    }

}
