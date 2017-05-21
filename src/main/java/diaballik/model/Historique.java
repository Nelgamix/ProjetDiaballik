package diaballik.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Historique {
    private final ArrayList<ArrayList<Action>> tours = new ArrayList<>();
    private final Jeu jeu;

    Historique(Jeu jeu) {
        this.jeu = jeu;
        this.tours.add(new ArrayList<>());
    }
    Historique(Jeu jeu, BufferedReader br) throws IOException {
        this(jeu);

        try {
            int n, i = 0;

            n = Integer.parseInt(br.readLine());

            while (i++ < n)
                addAction(new Action(jeu, br));

        } catch (IOException ioe) {
            System.err.println("(Historique.<init>) Erreur de lecture sur une action");
            throw ioe;
        }
    }

    ArrayList<Action> ajouterTour() {
        if (this.tours.size() > jeu.getTour()) return getActions(jeu.getTour());

        ArrayList<Action> a = new ArrayList<>();
        this.tours.add(a);
        return a;
    }

    // écrase toutes les actions inutiles à la fin du tour lorsqu'on a rollback
    public void ecraserInutile() {
        ArrayList<Action> a = getActions(jeu.getTour());
        if (a == null) return;
        int i = jeu.getNumAction();
        while (i++ <= a.size())
            a.remove(jeu.getNumAction() - 1);
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
            if (b.getAction() == Action.PASSE)
                n++;
        return n;
    }
    int getNombreDeplacementRetirer(int tour) {
        if (tour < 1) return 0;
        ArrayList<Action> a = getActions(tour);
        if (a == null) return 0;
        int n = 0;
        for (Action b : a)
            if (b.getAction() == Action.DEPLACEMENT)
                n++;
        return n;
    }

    Action getActionTourNum(int tour, int num) {
        if (tour < 1 || num < 1) return null;
        if (this.tours.size() < tour) return null;
        if (this.getActions(tour).size() < num) return null;

        return getActions(tour).get(num - 1);
    }

    public ArrayList<Action> getActions(int tour) {
        if (tour < 1 || this.tours.size() < tour) return null;
        return tours.get(tour - 1);
    }

    // ajoute une nouvelle action à l'historique (au tour actuel)
    void addAction(Action action) {
        ecraserFinHistorique();
        ArrayList<Action> a = getActions(action.getTour());
        if (a == null) {
            a = ajouterTour();
            a.add(action);
        } else {
            a.add(action);
        }
    }

    // écrase la fin de l'historique (par rapport au tour de jeu et numAction actuels)
    public void ecraserFinHistorique() {
        int tour = jeu.getTour(), num = jeu.getNumAction();

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

    // retourne le nombres d'action enregistrée pour le tour tour
    int nombreActions(int tour) {
        if (tour < 1) return 0;
        ArrayList<Action> a = getActions(tour);
        return a == null ? 0 : a.size();
    }
    int nombreActionsTotal() {
        int n = 0;
        for (ArrayList<Action> aa : this.tours)
            n += aa.size();

        return n;
    }

    public boolean peutDefaire() {
        boolean peutDefaire = getActionTourNum(jeu.getTour(), jeu.getNumAction() - 1) != null;
        if (!peutDefaire && !jeu.getConfigurationPartie().estMultijoueur()) peutDefaire = getActions(jeu.getTour() - 1) != null;

        return peutDefaire;
    }
    public boolean peutRefaire() {
        boolean peutRefaire = getActionTourNum(jeu.getTour(), jeu.getNumAction()) != null;
        if (!peutRefaire && !jeu.getConfigurationPartie().estMultijoueur()) peutRefaire = getActions(jeu.getTour() + 1) != null;

        return peutRefaire;
    }

    String getSaveString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreActionsTotal()).append("\n");

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
