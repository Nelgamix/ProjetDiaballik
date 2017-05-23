package diaballik.model;

import diaballik.scene.SceneJeu;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class Joueur {
    protected final Jeu jeu;
    private String nom;
    private int couleur;
    private int deplacementsRestants;
    private int passesRestantes;

    final static int DEPLACEMENTS_MAX = 2;
    final static int PASSES_MAX = 1;

    public final static int NOMBRE_PIONS = 7;

    int type; // 0 = humain

    Action actionAJouer;

    // Joueurs et couleurs
    public final static int VERT = 0; // J vert
    public final static int ROUGE = 1; // J rouge

    // Constructeurs
    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.couleur = couleur;
        this.nom = "";
        this.type = 0;

        reset_actions();
    }
    public Joueur(int couleur, BufferedReader br) {
        this(null, couleur, br);
    }
    public Joueur(Jeu jeu, int couleur, BufferedReader br) {
        this(jeu, couleur);

        String sCurrentLine, parts[];
        try {
            if ((sCurrentLine = br.readLine()) != null) {
                parts = sCurrentLine.split(":");

                this.setNom(parts[0]);
                this.setType(Integer.parseInt(parts[1]));
            }
        } catch (IOException ioe) {}
    }

    // Nom
    boolean setNom(String nom) {
        if (nom.length() > 30) {
            System.err.println("(Joueur.setNom) Nom \"" + nom.substring(0, 10) + "...\" trop long (> 30 char.)");
            return false;
        } else if (nom.length() < 3) {
            System.err.println("(Joueur.setNom) Nom \"" + nom + "\" trop court (< 3 char.)");
            return false;
        }

        this.nom = nom;
        return true;
    }
    public String getNom() {
        return nom;
    }

    public int getCouleur() {
        return this.couleur;
    }

    // Le joueur peut il déplacer un pion?
    public boolean peutDeplacer() {
        return this.deplacementsRestants > 0;
    }
    // Le joueur peut il FAIRE UNE PASSE? (pas passer le tour)
    public boolean peutPasser() {
        return this.passesRestantes > 0;
    }

    boolean actionPossible(Action action) {
        switch (action.getAction()) {
            case Action.PASSE:
                return this.peutPasser();
            case Action.DEPLACEMENT:
                return this.peutDeplacer();
            default:
                System.err.println("Action non reconnue");
                return false;
        }
    }

    public boolean estUneIA() {
        return false;
    }
    public boolean estUnJoueurReseau() {
        return false;
    }

    public abstract boolean preparerJouer();
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

        if (succes && jeu.getJoueurAdverse().estUnJoueurReseau())
            getSceneJeu().getReseau().envoyerAction(actionAJouer);

        finAction();

        return succes;
    }
    private void finAction() {
        if (jeu.getTerrain().partieTerminee(couleur)) {
            // la partie est terminée (le vainqueur est joueurActuel())
            getSceneJeu().finJeu(jeu.getJoueurActuel(), Jeu.VICTOIRE_NORMALE);
        }
    }

    public void finTour() {
        jeu.avancerTour();
    }

    public void setActionAJouer(Action actionAJouer) {
        this.actionAJouer = actionAJouer;
    }

    // décrémente le compteur correspondant à action.
    // si aucun compteur > 0 à la fin, renvoie false sinon renvoie true
    void moinsAction(Action action) {
        moinsAction(action.getAction());
    }
    void plusAction(Action action) {
        plusAction(action.getAction());
    }

    void moinsAction(int action) {
        switch (action) {
            case Action.PASSE:
                this.passesRestantes--;
                break;
            case Action.DEPLACEMENT:
                this.deplacementsRestants--;
                break;
            default:
                System.err.println("Action non reconnue");
        }
    }
    void plusAction(int action) {
        switch (action) {
            case Action.PASSE:
                this.passesRestantes++;
                break;
            case Action.DEPLACEMENT:
                this.deplacementsRestants++;
                break;
            default:
                System.err.println("Action non reconnue");
        }
    }

    void reset_actions() {
        this.deplacementsRestants = DEPLACEMENTS_MAX;
        this.passesRestantes = PASSES_MAX;
    }

    public int getDeplacementsRestants() {
        return deplacementsRestants;
    }
    public int getPassesRestantes() {
        return passesRestantes;
    }

    void setType(int type) {
        this.type = type;
    }
    int getType() {
        return type;
    }

    SceneJeu getSceneJeu() {
        return jeu.getSceneJeu();
    }

    String getSaveString() {
        return this.getNom() + ":" + this.getType() + "\n";
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "nom='" + nom + '\'' +
                ", couleur=" + couleur +
                ", deplacementsRestants=" + deplacementsRestants +
                ", passesRestantes=" + passesRestantes +
                ", type=" + type +
                '}';
    }
}
