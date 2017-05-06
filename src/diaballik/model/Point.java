package diaballik.model;

public class Point {
    private int x;
    private int y;

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
        return "[" + this.x + ";" + this.y + "]";
    }
}
