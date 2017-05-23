package diaballik.model;

import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;

    private static final boolean NOTATION_POINT = true;

    public Point(Point p) {
        this(p.getX(), p.getY());
    }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public boolean estDansTerrain() {
        return x >= 0 && y >= 0 && x < Terrain.LARGEUR && y < Terrain.HAUTEUR;
    }
}
