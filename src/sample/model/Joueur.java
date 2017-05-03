package sample.model;

import java.util.ArrayList;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:47).
 */
public class Joueur {
    private ArrayList<Pion> pions;
    private Terrain terrain;
    private Jeu jeu;
    private int couleur;
    private int actionsRestantes;

    public final static int NOMBRE_PIONS = 7;

    public final static int COULEUR_VERT = 0;
    public final static int COULEUR_ROUGE = 1;

    public Joueur(Jeu jeu, int couleur) {
        this.pions = new ArrayList<>();
        this.jeu = jeu;
        this.terrain = jeu.getTerrain();
        this.couleur = couleur;
        this.actionsRestantes = 3;

        for (int i = 0; i < NOMBRE_PIONS; i++) {
            this.pions.add(new Pion(this.couleur, i));
        }

        init();
    }

    private void init() {
        if (this.couleur == COULEUR_VERT) {
            this.terrain.setPionOnCase(new Point(0, 0), this.pions.get(0));
            this.terrain.setPionOnCase(new Point(1, 0), this.pions.get(1));
            this.terrain.setPionOnCase(new Point(2, 0), this.pions.get(2));
            this.terrain.setPionOnCase(new Point(3, 0), this.pions.get(3));
            this.terrain.setPionOnCase(new Point(4, 0), this.pions.get(4));
            this.terrain.setPionOnCase(new Point(5, 0), this.pions.get(5));
            this.terrain.setPionOnCase(new Point(6, 0), this.pions.get(6));
        } else {
            this.terrain.setPionOnCase(new Point(0, 6), this.pions.get(0));
            this.terrain.setPionOnCase(new Point(1, 6), this.pions.get(1));
            this.terrain.setPionOnCase(new Point(2, 6), this.pions.get(2));
            this.terrain.setPionOnCase(new Point(3, 6), this.pions.get(3));
            this.terrain.setPionOnCase(new Point(4, 6), this.pions.get(4));
            this.terrain.setPionOnCase(new Point(5, 6), this.pions.get(5));
            this.terrain.setPionOnCase(new Point(6, 6), this.pions.get(6));
        }
    }

    public int getCouleur() {
        return this.couleur;
    }

    public Pion getPion(int num) {
        return this.pions.get(num);
    }
}
