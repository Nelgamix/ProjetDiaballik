package diaballik.model;

public class JoueurIA extends Joueur {
    public final static int DIFFICULTE_FACILE = 1;
    public final static int DIFFICULTE_MOYEN = 2;
    public final static int DIFFICULTE_DIFFICILE = 3;

    public JoueurIA(Jeu jeu, int couleur, int difficulte) {
        super(jeu, couleur);
        this.setType(difficulte);
    }

    public static String parseDifficulte(int difficulte) {
        switch (difficulte) {
            case DIFFICULTE_FACILE:
                return "facile";
            case DIFFICULTE_MOYEN:
                return "moyen";
            case DIFFICULTE_DIFFICILE:
                return "difficile";
            default:
                return "inconnu";
        }
    }

    @Override
    public boolean preparerJouer() {
        return false;
    }

    @Override
    public boolean jouer() {
        return false;
    }
}
