package diaballik.model;

public class JoueurReseau extends Joueur {
    public final static int TYPE_RESEAU = 4;

    public JoueurReseau(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }

    public JoueurReseau(int couleur, String line) {
        super(couleur, line);
    }

    public JoueurReseau(Jeu jeu, int couleur, String line) {
        super(jeu, couleur, line);
    }

    @Override
    public boolean preparerJouer() {
        jeu.diaballik.reseau.recevoirAction();
        return false;
    }

    @Override
    public boolean jouer() {
        boolean succes = super.jouer();
        finAction();
        return succes;
    }
}
