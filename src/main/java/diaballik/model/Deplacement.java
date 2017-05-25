package diaballik.model;

public class Deplacement {
    private int distance;
    private Case aCase;

    public Deplacement(Case c, int distance) {
        this.distance = distance;
        this.aCase = c;
    }

    public int getDistance() {
        return distance;
    }
    public Case getCase() {
        return aCase;
    }
}
