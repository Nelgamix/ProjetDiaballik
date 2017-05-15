package diaballik.model;

public class JoueurLocal extends Joueur {
    public JoueurLocal(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }

    public JoueurLocal(int couleur, String line) {
        super(couleur, line);
    }

    public JoueurLocal(Jeu jeu, int couleur, String line) {
        super(jeu, couleur, line);
    }

    @Override
    public boolean preparerJouer() {
        return false;
    }

    @Override
    public boolean jouer() {
        boolean succes = super.jouer();

        if (succes && jeu.cp.multijoueur)
            jeu.diaballik.reseau.envoyerAction(actionAJouer);

        finAction();

        return succes;
    }

    @Override
    public void finTour() {
        if (jeu.cp.multijoueur)
            jeu.diaballik.reseau.envoyerAction(new Action(Action.FINTOUR));

        super.finTour();
    }
}
