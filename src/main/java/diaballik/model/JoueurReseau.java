package diaballik.model;

public class JoueurReseau extends Joueur {
    final static int TYPE_RESEAU = 4;

    JoueurReseau(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }

    @Override
    public boolean preparerJouer() {
        getSceneJeu().getReseau().recevoirAction();
        return false;
    }

    @Override
    public boolean estUnJoueurReseau() {
        return true;
    }
}
