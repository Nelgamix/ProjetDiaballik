package diaballik.model;

import java.io.Serializable;

public class Action implements Serializable {
    public final static int PASSE = 0;
    public final static int DEPLACEMENT = 1;
    public final static int FINTOUR = 2;

    private Case caseAvant;
    private final int action;
    private Case caseApres;
    private boolean inverse; // si inverse, l'action provient de défaire
    private final int tour;

    public Action(int action) {
        this.action = action;
        this.tour = -1;
        this.inverse = false;
    }

    public Action(Case caseAvant, int action, Case caseApres, int tour) {
        this.caseAvant = caseAvant;
        this.action = action;
        this.caseApres = caseApres;
        this.tour = tour;
        this.inverse = false;
    }

    public Action(Jeu jeu, String s) {
        String[] parts = s.split(":");
        this.tour = Integer.parseInt(parts[0]);
        Point pointCaseAvant = new Point(parts[1]);
        this.caseAvant = jeu.getTerrain().getCaseSur(pointCaseAvant);
        this.action = Integer.parseInt(parts[2]);
        Point pointCaseApres = new Point(parts[3]);
        this.caseApres = jeu.getTerrain().getCaseSur(pointCaseApres);
        this.inverse = false;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public boolean isInverse() {
        return inverse;
    }

    private static String parseAction(int action) {
        switch (action) {
            case PASSE:
                return "passe";
            case DEPLACEMENT:
                return "déplacement";
            case FINTOUR:
                return "fin du tour";
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

    public Case getCaseApres() {
        return caseApres;
    }

    public int getAction() {
        return action;
    }

    public Case getCaseAvant() {
        return caseAvant;
    }

    public void setCaseAvant(Case caseAvant) {
        this.caseAvant = caseAvant;
    }

    public void setCaseApres(Case caseApres) {
        this.caseApres = caseApres;
    }

    @Override
    public String toString() {
        return "Tour " + tour + ": " + parseAction(action) + " de " + caseAvant + " vers " + caseApres;
    }


}
