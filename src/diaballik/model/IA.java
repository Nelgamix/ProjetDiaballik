package diaballik.model;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 05.
 * Created by Nico (21:41).
 */
public class IA extends Joueur {
    public final static int DIFFICULTE_FACILE = 1;
    public final static int DIFFICULTE_MOYEN = 2;
    public final static int DIFFICULTE_DIFFICILE = 3;

    public IA(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }
}
