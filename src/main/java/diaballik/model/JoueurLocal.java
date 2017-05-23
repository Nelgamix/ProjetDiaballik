package diaballik.model;

import java.io.BufferedReader;

public class JoueurLocal extends Joueur {
    public JoueurLocal(Jeu jeu, int couleur) {
        super(jeu, couleur);
    }
    public JoueurLocal(int couleur, BufferedReader br) {
        super(couleur, br);
    }

    @Override
    public boolean preparerJouer() {
        return false;
    }

    @Override
    public void finTour() {
        if (jeu.getConfigurationPartie().estMultijoueur())
            getSceneJeu().getReseau().envoyerAction(new Action(Action.FINTOUR));

        super.finTour();
    }
}
