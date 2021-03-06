package diaballik.model;

import java.io.Serializable;
import java.util.Observable;

public class Case extends Observable implements Serializable {
    private final Point point;
    transient private Pion pion;

    Case(Point point) {
        this(point, null);
    }
    private Case(Point point, Pion pion) {
        this.point = point;
        this.pion = pion;
    }

    boolean pionPresent() {
        return pion != null;
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
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Case))
            return false;

        Case toCmp = (Case) obj;

        return getPoint().equals(toCmp.getPoint()) && getPion() == null && toCmp.getPion() == null || getPion() != null && toCmp.getPion() != null && getPion().equals(toCmp.getPion());
    }

    @Override
    public int hashCode() {
        return getPion() != null ? getPion().hashCode() : 0;
    }
}
