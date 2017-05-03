package sample.model;

public class Case {
    private final Point point;
    private Pion pion;

    public Case(Point point) {
        this(point, null);
    }

    public Case(Point point, Pion pion) {
        this.point = point;
        this.pion = pion;
    }

    public void setPion(Pion pion) {
        this.pion = pion;
    }

    public Pion getPion() {
        return pion;
    }

    public Point getPoint() {
        return point;
    }
}
