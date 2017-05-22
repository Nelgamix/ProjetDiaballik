package diaballik.model;

import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;

    private static final boolean NOTATION_POINT = true;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Point(String s) {
        String parts[];
        parts = s.split(";");
        this.x = Integer.parseInt(parts[0]);
        this.y = Integer.parseInt(parts[1]);
    }

    public String getSaveString() {
        return this.getX() + ";" + this.getY();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public String toString() {
        if (NOTATION_POINT) {
            char a = (char)this.y;
            a += 65;
            return "(" + a + "-" + (this.x + 1) + ")";
        } else {
            return "[" + this.y + ";" + this.x + "]";
        }
    }

    public boolean estDansTerrain() {
        return x >= 0 && y >= 0 && x < Terrain.LARGEUR && y < Terrain.HAUTEUR;
    }
}
