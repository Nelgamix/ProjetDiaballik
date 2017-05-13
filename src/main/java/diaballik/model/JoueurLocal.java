package diaballik.model;

public class JoueurLocal extends Joueur {
    private Action actionAJouer;

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
        boolean succes = false;

        if (actionPossible(actionAJouer)) {
            if (actionAJouer.getAction() == Action.PASSE) {
                if (jeu.passe(actionAJouer)) {
                    succes = true;
                }
            } else {
                if (jeu.deplacement(actionAJouer)) {
                    succes = true;
                }
            }
        }

        return succes;
    }

    public void setActionAJouer(Action actionAJouer) {
        this.actionAJouer = actionAJouer;
    }
}
