package diaballik.model;

public class Metadonnees {
    public int tour;
    public Joueur joueurVert;
    public Joueur joueurRouge;
    public Terrain terrain;

    @Override
    public String toString() {
        return "Metadonnees{" +
                "tour=" + tour +
                ", joueurVert=" + joueurVert +
                ", joueurRouge=" + joueurRouge +
                '}';
    }
}
