package diaballik.model;

import java.util.Observable;

public class Case extends Observable {
    private final Point point;
    private Pion pion;

    private boolean marque;

    public Case(Point point) {
        this(point, null);
    }

    public Case(Point point, Pion pion) {
        this.point = point;
        this.pion = pion;

        this.marque = false;
    }

    public void setMarque(boolean marque) {
        this.marque = marque;

        setChanged();
        notifyObservers();
    }

    public boolean isMarque() {
        return marque;
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

    @Override
    public String toString() {
        return this.point.toString();
    }
}
