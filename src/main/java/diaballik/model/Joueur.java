package diaballik.model;

import static diaballik.model.Jeu.VICTOIRE_NORMALE;

public abstract class Joueur {
    protected final Jeu jeu;
    protected String nom;
    protected int couleur;
    protected int deplacementsRestants;
    protected int passesRestantes;

    protected final static int DEPLACEMENTS_MAX = 2;
    protected final static int PASSES_MAX = 1;

    public final static int NOMBRE_PIONS = 7;

    protected int type; // 0 = humain

    protected Action actionAJouer;

    // Joueurs et couleurs
    public final static int VERT = 0; // J vert
    public final static int ROUGE = 1; // J rouge

    // Constructeurs
    public Joueur(Jeu jeu, int couleur) {
        this.jeu = jeu;
        this.couleur = couleur;
        this.nom = "";
        this.type = 0;

        reset_actions();
    }
    public Joueur(int couleur, String line) {
        this(null, couleur, line);
    }
    public Joueur(Jeu jeu, int couleur, String line) {
        this(jeu, couleur);

        String[] parts = line.split(":");

        this.setNom(parts[0]);
        this.setType(Integer.parseInt(parts[1]));
        /*this.setDeplacementsRestants(Integer.parseInt(parts[2]));
        this.setPassesRestantes(Integer.parseInt(parts[3]));*/
    }

    // Nom
    boolean setNom(String nom) {
        if (nom.length() > 30) {
            System.err.println("(Joueur.setNom) Nom \"" + nom.substring(0, 10) + "...\" trop long (> 30 char.)");
            return false;
        } else if (nom.length() < 3) {
            System.err.println("(Joueur.setNom) Nom \"" + nom + "\" trop court (< 3 char.)");
            return false;
        }

        this.nom = nom;
        return true;
    }
    public String getNom() {
        return nom;
    }

    public int getCouleur() {
        return this.couleur;
    }

    // Le joueur peut il déplacer un pion?
    public boolean peutDeplacer() {
        return this.deplacementsRestants > 0;
    }
    // Le joueur peut il FAIRE UNE PASSE? (pas passer le tour)
    public boolean peutPasser() {
        return this.passesRestantes > 0;
    }

    boolean actionPossible(Action action) {
        switch (action.getAction()) {
            case Action.PASSE:
                return this.peutPasser();
            case Action.DEPLACEMENT:
                return this.peutDeplacer();
            default:
                System.err.println("Action non reconnue");
                return false;
        }
    }

    public boolean estUneIA() {
        return false;
    }

    public abstract boolean preparerJouer();
    public boolean jouer() {
        boolean succes = false;

        if (actionPossible(actionAJouer)) {
            if (actionAJouer.getAction() == Action.PASSE) {
                if (jeu.passe(actionAJouer)) {
                    succes = true;
                }
            } else {
                if (jeu.deplacement(actionAJouer)) {
                    succes = true;
                }
            }
        }

        return succes;
    }
    protected void finAction() {
        if (jeu.partieTerminee()) {
            // la partie est terminée (le vainqueur est joueurActuel())
            jeu.diaballik.finJeu(jeu.getJoueurActuel(), VICTOIRE_NORMALE);
        }
    }

    public void finTour() {
        jeu.avancerTour();
    }

    public void setActionAJouer(Action actionAJouer) {
        this.actionAJouer = actionAJouer;
    }

    // décrémente le compteur correspondant à action.
    // si aucun compteur > 0 à la fin, renvoie false sinon renvoie true
    void moinsAction(Action action) {
        moinsAction(action.getAction());
    }
    void plusAction(Action action) {
        plusAction(action.getAction());
    }

    void moinsAction(int action) {
        switch (action) {
            case Action.PASSE:
                this.passesRestantes--;
                break;
            case Action.DEPLACEMENT:
                this.deplacementsRestants--;
                break;
            default:
                System.err.println("Action non reconnue");
        }
    }
    void plusAction(int action) {
        switch (action) {
            case Action.PASSE:
                this.passesRestantes++;
                break;
            case Action.DEPLACEMENT:
                this.deplacementsRestants++;
                break;
            default:
                System.err.println("Action non reconnue");
        }
    }

    void setDeplacementsRestants(int deplacementsRestants) {
        this.deplacementsRestants = deplacementsRestants;
    }
    void setPassesRestantes(int passesRestantes) {
        this.passesRestantes = passesRestantes;
    }

    void reset_actions() {
        this.deplacementsRestants = DEPLACEMENTS_MAX;
        this.passesRestantes = PASSES_MAX;
    }

    public int getDeplacementsRestants() {
        return deplacementsRestants;
    }
    public int getPassesRestantes() {
        return passesRestantes;
    }

    void setType(int type) {
        this.type = type;
    }
    int getType() {
        return type;
    }

    String getSaveString() {
        return this.getNom() + ":" + this.getType() + ":" + this.getDeplacementsRestants() + ":" + this.getPassesRestantes() + "\n";
    }
}
