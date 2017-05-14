package diaballik.model;

import java.io.Serializable;
import java.util.Observable;

public class Case extends Observable implements Serializable {
    private final Point point;
    transient private Pion pion;

    public Case(Point point) {
        this(point, null);
    }

    public Case(Point point, Pion pion) {
        this.point = point;
        this.pion = pion;
    }

    public void setMarque(boolean marque) {
        setChanged();
        notifyObservers();
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
        return this.getPion() != null ? pion.toString() : "0";
        //return this.point.toString();
    }
}
