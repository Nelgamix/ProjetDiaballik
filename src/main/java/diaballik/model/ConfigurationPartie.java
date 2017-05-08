package diaballik.model;

import diaballik.Diaballik;
import diaballik.Utils;

public class ConfigurationPartie {
    String cheminFichier; // chemin vers le fichier terrain ou sauvegarde
    boolean estUneSauvegarde; // vrai si cheminFichier est un fichier de sauvegarde, faux si simple fichier représentant un terrain

    String nomJoueur1;
    String nomJoueur2;
    int joueur1ia;
    int joueur2ia;

    //public final static String CHEMIN_TERRAIN_DEFAUT = Diaballik.DOSSIER_TERRAINS + "/defaultTerrain.txt";

    public ConfigurationPartie(String nomJoueur1, String nomJoueur2, int joueur1ia, int joueur2ia, String terrain) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;

        this.joueur1ia = joueur1ia;
        this.joueur2ia = joueur2ia;

        this.estUneSauvegarde = false;
        this.cheminFichier = Diaballik.DOSSIER_TERRAINS + "/" + terrain;
    }

    public ConfigurationPartie(int joueur1ia, int joueur2ia, String terrain) {
        this.nomJoueur1 = Utils.getNomAleatoire();
        this.nomJoueur2 = Utils.getNomAleatoire();

        this.joueur1ia = joueur1ia;
        this.joueur2ia = joueur2ia;

        this.estUneSauvegarde = false;
        this.cheminFichier = Diaballik.DOSSIER_TERRAINS + "/" + terrain;
    }

    public ConfigurationPartie(String cheminFichier, boolean estUneSauvegarde) {
        this.cheminFichier = cheminFichier;
        this.estUneSauvegarde = estUneSauvegarde;
    }
}
