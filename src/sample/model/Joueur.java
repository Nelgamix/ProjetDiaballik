package sample.model;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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

    public final static int COULEUR_VERT = 0;
    public final static int COULEUR_ROUGE = 1;

    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.terrain = jeu.getTerrain();
        this.couleur = couleur;
        this.nom = "";

        reset_actions();
    }

    public void setNom(String nom) {
        this.nom = nom;
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
}
