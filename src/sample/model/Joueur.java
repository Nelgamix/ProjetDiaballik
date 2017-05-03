package sample.model;

import java.util.ArrayList;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:47).
 */
public class Joueur {
    private Terrain terrain;
    private Jeu jeu;
    private int couleur;
    private int actionsRestantes;

    public final static int NOMBRE_PIONS = 7;

    public final static int COULEUR_VERT = 0;
    public final static int COULEUR_ROUGE = 1;

    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.terrain = jeu.getTerrain();
        this.couleur = couleur;
        this.actionsRestantes = 3;
    }
    public int getCouleur() {
        return this.couleur;
    }
}
