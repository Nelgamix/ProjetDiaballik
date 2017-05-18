package diaballik.model;

import diaballik.Diaballik;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationPartie {
    private String cheminFichier; // chemin vers le fichier terrain ou sauvegarde
    private boolean estUneSauvegarde; // vrai si cheminFichier est un fichier de sauvegarde, faux si simple fichier représentant un terrain
    private boolean multijoueur;

    private int dureeTimer;
    private String nomJoueur1;
    private String nomJoueur2;
    private int typeJoueur1;
    private int typeJoueur2;
    private String terrain;

    private boolean aideDeplacement;
    private boolean aidePasse;
    private boolean autoSelectionPion;

    private final Properties properties = new Properties();

    private final static String CONFIG_FILE = "config.properties";

    public ConfigurationPartie(int numJoueurReseau, String nomJoueur1, String nomJoueur2, String terrain) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;

        if (numJoueurReseau == 1) {
            this.typeJoueur1 = 0;
            this.typeJoueur2 = JoueurReseau.TYPE_RESEAU;
        } else {
            this.typeJoueur1 = JoueurReseau.TYPE_RESEAU;
            this.typeJoueur2 = 0;
        }

        this.dureeTimer = 45;

        this.estUneSauvegarde = false;
        this.terrain = terrain;
        this.cheminFichier = Diaballik.DOSSIER_TERRAINS + "/" + terrain;
        this.multijoueur = true;

        readProperties();
    }
    public ConfigurationPartie(String nomJoueur1, String nomJoueur2, int joueur1ia, int joueur2ia, int dureeTimer, String terrain) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;

        this.typeJoueur1 = joueur1ia;
        this.typeJoueur2 = joueur2ia;

        this.dureeTimer = dureeTimer;

        this.estUneSauvegarde = false;
        this.terrain = terrain;
        this.cheminFichier = Diaballik.DOSSIER_TERRAINS + "/" + terrain;
        this.multijoueur = false;

        readProperties();
    }
    public ConfigurationPartie(String cheminFichier, boolean estUneSauvegarde) {
        this.cheminFichier = cheminFichier;
        this.estUneSauvegarde = estUneSauvegarde;
        this.multijoueur = false;

        readProperties();
    }

    private void readProperties() {
        try (FileInputStream i = new FileInputStream(CONFIG_FILE)) {
            properties.load(i);

            this.aideDeplacement = Boolean.parseBoolean(properties.getProperty("aideDeplacement"));
            this.aidePasse = Boolean.parseBoolean(properties.getProperty("aidePasse"));
            this.autoSelectionPion = Boolean.parseBoolean(properties.getProperty("autoSelectionPion"));
        } catch (IOException ex) {
            this.aideDeplacement = true;
            this.aidePasse = true;
            this.autoSelectionPion = false;

            this.writeProperties();
        }
    }
    public void writeProperties() {
        try (FileOutputStream o = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("aideDeplacement", Boolean.toString(aideDeplacement));
            properties.setProperty("autoSelectionPion", Boolean.toString(autoSelectionPion));
            properties.setProperty("aidePasse", Boolean.toString(aidePasse));

            properties.store(o, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void setAideDeplacement(boolean aideDeplacement) {
        this.aideDeplacement = aideDeplacement;
    }
    public void setAidePasse(boolean aidePasse) {
        this.aidePasse = aidePasse;
    }
    public void setAutoSelectionPion(boolean autoSelectionPion) {
        this.autoSelectionPion = autoSelectionPion;
    }

    public void setDureeTimer(int dureeTimer) {
        this.dureeTimer = dureeTimer;
    }

    public boolean isAideDeplacement() {
        return aideDeplacement;
    }
    public boolean isAidePasse() {
        return aidePasse;
    }
    public boolean isAutoSelectionPion() {
        return autoSelectionPion;
    }

    public boolean estUneSauvegarde() {
        return estUneSauvegarde;
    }
    public boolean estMultijoueur() {
        return multijoueur;
    }

    public String getNomJoueur1() {
        return nomJoueur1;
    }
    public String getNomJoueur2() {
        return nomJoueur2;
    }
    public int getTypeJoueur1() {
        return typeJoueur1;
    }
    public int getTypeJoueur2() {
        return typeJoueur2;
    }
    public int getDureeTimer() {
        return dureeTimer;
    }
    public String getTerrain() {
        return terrain;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }
}
