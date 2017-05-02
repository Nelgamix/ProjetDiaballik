package sample;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:47).
 */
public class Joueur {
    private Pion[] pions;
    private Terrain terrain;
    private int couleur;

    public final static int NOMBRE_PIONS = 7;

    public final static int COULEUR_VERT = 0;
    public final static int COULEUR_ROUGE = 1;

    public Joueur(Terrain terrain, int couleur) {
        this.pions = new Pion[NOMBRE_PIONS];
        this.terrain = terrain;
        this.couleur = couleur;

        for (int i = 0; i < NOMBRE_PIONS; i++) {
            this.pions[i] = new Pion(this.couleur, i);
        }

        init();
    }

    public void init() {
        if (this.couleur == COULEUR_VERT) {
            this.terrain.setPion(this.pions[0], 0, 0);
            this.terrain.setPion(this.pions[1], 1, 0);
            this.terrain.setPion(this.pions[2], 2, 0);
            this.terrain.setPion(this.pions[3], 3, 0);
            this.terrain.setPion(this.pions[4], 4, 0);
            this.terrain.setPion(this.pions[5], 5, 0);
            this.terrain.setPion(this.pions[6], 6, 0);
        } else {
            this.terrain.setPion(this.pions[0], 0, 6);
            this.terrain.setPion(this.pions[1], 1, 6);
            this.terrain.setPion(this.pions[2], 2, 6);
            this.terrain.setPion(this.pions[3], 3, 6);
            this.terrain.setPion(this.pions[4], 4, 6);
            this.terrain.setPion(this.pions[5], 5, 6);
            this.terrain.setPion(this.pions[6], 6, 6);
        }
    }
}
