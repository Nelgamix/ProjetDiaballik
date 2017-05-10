package diaballik.model;

import java.util.ArrayList;

public class Historique {
    private final ArrayList<ArrayList<Action>> tours = new ArrayList<>();
    private final Jeu jeu;

    Historique(Jeu jeu) {
        this.jeu = jeu;
    }

    Action getDerniereAction() {
        if (this.tours.isEmpty()) return null;

        ArrayList<Action> a = this.tours.get(this.tours.size() - 1);

        if (a.isEmpty()) return null;
        else return a.get(a.size() - 1);
    }

    Action getDerniereActionTour(int tour) {
        if (this.tours.size() < tour) return null;

        ArrayList<Action> a = getActions(tour);

        if (a.isEmpty()) return null;
        else return a.get(a.size() - 1);
    }

    void ecraserInutile(int tour, int num) {
        ArrayList<Action> a = getActions(tour);
        if (a == null) return;
        int i = num;
        while (i++ <= a.size())
            a.remove(num - 1);
    }

    boolean tourExiste(int tour) {
        return tour > 0 && tour <= this.tours.size();
    }

    int getNombrePassesRetirer(int tour) {
        if (tour < 1) return 0;
        ArrayList<Action> a = getActions(tour);
        if (a == null) return 0;
        int n = 0;
        for (Action b : a)
            if (b.getAction() == Joueur.ACTION_PASSE)
                n++;
        return n;
    }

    int getNombreDeplacementRetirer(int tour) {
        if (tour < 1) return 0;
        ArrayList<Action> a = getActions(tour);
        if (a == null) return 0;
        int n = 0;
        for (Action b : a)
            if (b.getAction() == Joueur.ACTION_DEPLACEMENT)
                n++;
        return n;
    }

    Action getActionTourNum(int tour, int num) {
        if (tour < 1 || num < 1) return null;
        if (this.tours.size() < tour) return null;
        if (this.getActions(tour).size() < num) return null;

        return getActions(tour).get(num - 1);
    }

    ArrayList<Action> getActions(int tour) {
        if (tour < 1 || this.tours.size() < tour) return null;
        return tours.get(tour - 1);
    }

    void addAction(Case caseAvant, int action, Case caseApres, int tour) {
        verifierAvantAjout(tour, -1);
        ArrayList<Action> a = getActions(tour);
        if (a == null) {
            a = new ArrayList<>();
            a.add(new Action(caseAvant, action, caseApres, tour));
            this.tours.add(a);
        } else {
            a.add(new Action(caseAvant, action, caseApres, tour));
        }
    }

    void addAction(Jeu j, String s) {
        ArrayList<Action> a = getActions(Integer.parseInt(s.split(":")[0]));
        if (a == null) {
            a = new ArrayList<>();
            a.add(new Action(j, s));
            this.tours.add(a);
        } else {
            a.add(new Action(j, s));
        }
    }

    void verifierAvantAjout(int tour, int num) {
        if (num == -1 && this.tours.size() < tour) {
            for (int i = 0; i < tour - this.tours.size(); i++) {
                this.tours.add(new ArrayList<>());
            }
        } else {
            if (tour < 1 || num < 1) return;

            if (this.tours.size() > tour) {
                int s = this.tours.size();
                for (int i = tour; i < s; i++) {
                    this.tours.remove(tour);
                }
            }

            ArrayList<Action> a = getActions(tour);
            if (a != null && a.size() >= num) {
                int s = a.size();
                for (int i = num - 1; i < s; i++) {
                    a.remove(num - 1);
                }
            }
        }

    }

    int nombreActions(int tour) {
        if (tour < 1) return 0;
        ArrayList<Action> a = getActions(tour);
        return a == null ? 0 : a.size();
    }

    public boolean peutDefaire() {
        boolean peutDefaire = getActionTourNum(jeu.getTour(), jeu.getNumAction() - 1) != null;
        if (!peutDefaire) peutDefaire = getActions(jeu.getTour() - 1) != null;

        return peutDefaire;
    }

    public boolean peutRefaire() {
        boolean peutRefaire = getActionTourNum(jeu.getTour(), jeu.getNumAction()) != null;
        if (!peutRefaire) peutRefaire = getActions(jeu.getTour() + 1) != null;

        return peutRefaire;
    }

    String getSaveString() {
        StringBuilder sb = new StringBuilder();

        for (ArrayList<Action> a : this.tours)
            for (Action b : a)
                sb.append(b.getSaveString());

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (ArrayList<Action> a : this.tours) {
            sb.append("Tour ").append(i++).append(":\n");
            for (Action b : a)
                sb.append("\t").append(b).append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }
}
