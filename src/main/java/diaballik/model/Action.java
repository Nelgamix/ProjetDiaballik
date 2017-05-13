package diaballik.model;

public class Action {
    public final static int PASSE = 0;
    public final static int DEPLACEMENT = 1;

    private final Case caseAvant;
    private final int action;
    private final Case caseApres;
    private final int tour;

    public Action(Case caseAvant, int action, Case caseApres, int tour) {
        this.caseAvant = caseAvant;
        this.action = action;
        this.caseApres = caseApres;
        this.tour = tour;
    }

    Action(Jeu jeu, String s) {
        String[] parts = s.split(":");
        this.tour = Integer.parseInt(parts[0]);
        Point pointCaseAvant = new Point(parts[1]);
        this.caseAvant = jeu.getTerrain().getCaseSur(pointCaseAvant);
        this.action = Integer.parseInt(parts[2]);
        Point pointCaseApres = new Point(parts[3]);
        this.caseApres = jeu.getTerrain().getCaseSur(pointCaseApres);
    }

    static String parseAction(int action) {
        switch (action) {
            case PASSE:
                return "passe";
            case DEPLACEMENT:
                return "déplacement";
            default:
                return "inconnu";
        }
    }

    String getSaveString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.tour)
                .append(":")
                .append(this.caseAvant.getPoint().getSaveString())
                .append(":")
                .append(action)
                .append(":")
                .append(this.caseApres.getPoint().getSaveString())
                .append("\n");

        return sb.toString();
    }

    Case getCaseApres() {
        return caseApres;
    }

    int getAction() {
        return action;
    }

    Case getCaseAvant() {
        return caseAvant;
    }

    @Override
    public String toString() {
        return "Tour " + tour + ": " + parseAction(action) + " de " + caseAvant + " vers " + caseApres;
    }
}
