package diaballik.model;

public class IA extends Joueur {
    public final static int DIFFICULTE_FACILE = 1;
    public final static int DIFFICULTE_MOYEN = 2;
    public final static int DIFFICULTE_DIFFICILE = 3;

    public IA(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }
}
