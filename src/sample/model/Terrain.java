package sample.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Terrain {
    public final HashMap<Point, Case> cases = new HashMap<>(); // Représentation modèle du terrain (Cases)

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    public Terrain() {
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                Point p = new Point(i, j);
                this.cases.put(p, new Case(null, p));
            }
        }
    }

    public Case getCase(Point point) {
        return this.cases.get(point);
    }

    public void setPionOnCase(Point point, Pion pion) {
        this.cases.get(point).setPion(pion);
    }

    /*public void setPion(Pion pion, int x, int y) {
        this.cases[x][y].setPion(pion);
    }*/
}
