package diaballik.model;

public class Action {
    private final Case caseAvant;
    private final int action;
    private final Case caseApres;
    private final int tour;

    Action(Case caseAvant, int action, Case caseApres, int tour) {
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
        return "Tour " + tour + ": " + Joueur.parseAction(action) + " de " + caseAvant + " vers " + caseApres;
    }
}
