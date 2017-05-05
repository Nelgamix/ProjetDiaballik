package diaballik.model;

import javafx.scene.paint.Color;

public class Joueur {
    private Terrain terrain;
    private Jeu jeu;
    private String nom;
    private int couleur;
    private int deplacementsRestants;
    private int passesRestantes;

    public final static int NOMBRE_DEPLACEMENTS_MAX = 2;
    public final static int NOMBRE_PASSES_MAX = 1;

    public final static int ACTION_PASSE = 0;
    public final static int ACTION_DEPLACEMENT = 1;

    public final static int NOMBRE_PIONS = 7;

    // Joueurs et couleurs
    public final static int JOUEUR_VERT = 0; // J vert
    public final static int JOUEUR_ROUGE = 1; // J rouge
    public final static Color COULEUR_VERT = Color.GREEN; // Couleur du J vert
    public final static Color COULEUR_ROUGE = Color.RED; // Couleur du J rouge

    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.terrain = jeu.getTerrain();
        this.couleur = couleur;
        this.nom = "";

        reset_actions();
    }

    public boolean setNom(String nom) {
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

    public boolean actionPossible(int action) {
        switch (action) {
            case ACTION_PASSE:
                return this.passesRestantes > 0;
            case ACTION_DEPLACEMENT:
                return this.deplacementsRestants > 0;
            default:
                System.err.println("Action non reconnue");
                return false;
        }
    }

    // décrémente le compteur correspondant à action.
    // si aucun compteur > 0 à la fin, renvoie false sinon renvoie true
    public boolean moinsAction(int action) {
        switch (action) {
            case ACTION_PASSE:
                this.passesRestantes--;
                break;
            case ACTION_DEPLACEMENT:
                this.deplacementsRestants--;
                break;
            default:
                System.err.println("Action non reconnue");
        }

        if (this.deplacementsRestants + this.passesRestantes > 0)
            return true;
        else {
            return false;
        }
    }

    public void plusAction(int action) {
        switch (action) {
            case ACTION_PASSE:
                this.passesRestantes++;
                break;
            case ACTION_DEPLACEMENT:
                this.deplacementsRestants++;
                break;
            default:
                System.err.println("Action non reconnue");
        }
    }

    public void setDeplacementsRestants(int deplacementsRestants) {
        this.deplacementsRestants = deplacementsRestants;
    }

    public void setPassesRestantes(int passesRestantes) {
        this.passesRestantes = passesRestantes;
    }

    public void reset_actions() {
        this.deplacementsRestants = NOMBRE_DEPLACEMENTS_MAX;
        this.passesRestantes = NOMBRE_PASSES_MAX;
    }

    public int getDeplacementsRestants() {
        return deplacementsRestants;
    }

    public int getPassesRestantes() {
        return passesRestantes;
    }

    public String getSaveString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getNom()).append(":").append(this.getDeplacementsRestants()).append(":").append(this.getPassesRestantes()).append("\n");

        return sb.toString();
    }
}
