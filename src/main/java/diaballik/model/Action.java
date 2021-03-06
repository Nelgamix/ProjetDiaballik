package diaballik.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

public class Action implements Serializable {
    public final static int PASSE = 0;
    public final static int DEPLACEMENT = 1;
    public final static int FINTOUR = 2;
    public final static int ANTIJEU = 3;

    private Case caseAvant;
    private int action;
    private Case caseApres;
    private boolean inverse; // si inverse, l'action provient de défaire
    private int tour;
    private int cout;

    public Action(Action action) {
        this(action.caseAvant, action.getAction(), action.getCaseApres(), action.getTour());
    }
    public Action(int action) {
        this.action = action;
        this.tour = -1;
        this.inverse = false;
    }
    public Action(Case caseAvant, int action, Case caseApres) {
        this(caseAvant, action, caseApres, -1);
    }
    public Action(Case caseAvant, int action, Case caseApres, int tour) {
        this(caseAvant, action, caseApres, tour, 1);
    }
    public Action(Case caseAvant, int action, Case caseApres, int tour, int cout) {
        this.caseAvant = caseAvant;
        this.action = action;
        this.caseApres = caseApres;
        this.tour = tour;
        this.cout = cout;
        this.inverse = false;
    }
    public Action(Jeu jeu, BufferedReader br) throws IOException {
        String sCurrentLine, parts[];
        try {
            if ((sCurrentLine = br.readLine()) != null) {
                parts = sCurrentLine.split(":");
                this.tour = Integer.parseInt(parts[0]);
                Point pointCaseAvant = new Point(parts[1]);
                this.caseAvant = jeu.getTerrain().getCaseSur(pointCaseAvant);
                this.action = Integer.parseInt(parts[2]);
                Point pointCaseApres = new Point(parts[3]);
                this.caseApres = jeu.getTerrain().getCaseSur(pointCaseApres);
                this.cout = Integer.parseInt(parts[4]);
                this.inverse = false;
            }
        } catch (IOException ioe) {
            System.err.println("(Action.<init>) Erreur de lecture");
            throw ioe;
        }
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }
    public boolean isInverse() {
        return inverse;
    }
    public void reverse() {
        Case tmp = getCaseApres();
        setCaseApres(getCaseAvant());
        setCaseAvant(tmp);
    }

    int getCout() {
        return cout;
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
        return String.valueOf(this.tour) +
                ":" +
                this.caseAvant.getPoint().getSaveString() +
                ":" +
                action +
                ":" +
                this.caseApres.getPoint().getSaveString() +
                ":" +
                this.cout +
                "\n";
    }

    public int getAction() {
        return action;
    }
    public int getTour() {
        return tour;
    }

    public Case getCaseApres() {
        return caseApres;
    }
    public Case getCaseAvant() {
        return caseAvant;
    }

    void setCaseAvant(Case caseAvant) {
        this.caseAvant = caseAvant;
    }
    void setCaseApres(Case caseApres) {
        this.caseApres = caseApres;
    }

    @Override
    public String toString() {
        if (getTour() < 0) return parseAction(action) + " de " + caseAvant.getPoint() + " vers " + caseApres.getPoint();
        else
            return "Tour " + tour + ": " + parseAction(action) + " de " + caseAvant.getPoint() + " vers " + caseApres.getPoint();
    }


}
