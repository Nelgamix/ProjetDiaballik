package sample.model;

public class Joueur {
    private Terrain terrain;
    private Jeu jeu;
    private int couleur;
    private int actionsRestantes;

    public final static int NOMBRE_ACTIONS_MAX = 3;
    public final static int NOMBRE_PIONS = 7;

    public final static int COULEUR_VERT = 0;
    public final static int COULEUR_ROUGE = 1;

    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.terrain = jeu.getTerrain();
        this.couleur = couleur;
        this.actionsRestantes = NOMBRE_ACTIONS_MAX;
    }

    public int getCouleur() {
        return this.couleur;
    }

    public boolean moinsAction() {
        this.actionsRestantes--;
        if (this.actionsRestantes == 0) {
            this.actionsRestantes = NOMBRE_ACTIONS_MAX;
            return false;
        } else
            return true;
    }
}
