package diaballik.model;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 05.
 * Created by Nico (22:07).
 */
public class ConfigurationPartie {
    String path;
    boolean isSave;

    String nomJoueur1;
    String nomJoueur2;
    int joueur1ia;
    int joueur2ia;

    public final static String DEFAULT_TERRAIN_PATH = "defaultTerrains/defaultTerrain.txt";

    public ConfigurationPartie(String nomJoueur1, String nomJoueur2, int joueur1ia, int joueur2ia) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
        this.joueur1ia = joueur1ia;
        this.joueur2ia = joueur2ia;

        this.isSave = false;
        this.path = DEFAULT_TERRAIN_PATH;
    }

    public ConfigurationPartie(String path, boolean isSave) {
        this.path = path;
        this.isSave = isSave;
    }
}
