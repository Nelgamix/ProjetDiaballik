package sample.model;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:58).
 */
public class Case {
    private Pion pion;
    private Point point;

    public Case(Pion pion, Point point) {
        this.pion = pion;
        this.point = point;
    }

    public void setPion(Pion pion) {
        this.pion = pion;
    }

    public Pion getPion() {
        return this.pion;
    }
}
