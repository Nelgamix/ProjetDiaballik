package sample;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:58).
 */
public class Case {
    public Pion pion;

    public Case(Pion pion) {
        this.pion = pion;
    }

    public void setPion(Pion pion) {
        this.pion = pion;
    }

    public Pion getPion() {
        return this.pion;
    }
}
