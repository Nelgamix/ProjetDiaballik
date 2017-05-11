package diaballik.model;

public class Joueur {
    private final Jeu jeu;
    private String nom;
    private int couleur;
    private int deplacementsRestants;
    private int passesRestantes;

    private final static int NOMBRE_DEPLACEMENTS_MAX = 2;
    private final static int NOMBRE_PASSES_MAX = 1;

    final static int ACTION_PASSE = 0;
    final static int ACTION_DEPLACEMENT = 1;

    public final static int NOMBRE_PIONS = 7;

    private int type; // 0 = humain

    // Joueurs et couleurs
    public final static int JOUEUR_VERT = 0; // J vert
    public final static int JOUEUR_ROUGE = 1; // J rouge

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
        this.setDeplacementsRestants(Integer.parseInt(parts[2]));
        this.setPassesRestantes(Integer.parseInt(parts[3]));
    }

    static String parseAction(int action) {
        switch (action) {
            case ACTION_PASSE:
                return "passe";
            case ACTION_DEPLACEMENT:
                return "déplacement";
            default:
                return "inconnu";
        }
    }

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

    boolean actionPossible(int action) {
        switch (action) {
            case ACTION_PASSE:
                return this.peutPasser();
            case ACTION_DEPLACEMENT:
                return this.peutDeplacer();
            default:
                System.err.println("Action non reconnue");
                return false;
        }
    }

    // décrémente le compteur correspondant à action.
    // si aucun compteur > 0 à la fin, renvoie false sinon renvoie true
    boolean moinsAction(int action) {
        switch (action) {
            case ACTION_PASSE:
                this.passesRestantes--;
                break;
            case ACTION_DEPLACEMENT:
                this.deplacementsRestants--;
                break;
            default:
                System.err.println("Action non reconnue");
        }

        if (this.deplacementsRestants + this.passesRestantes > 0)
            return true;
        else {
            return false;
        }
    }

    void plusAction(int action) {
        switch (action) {
            case ACTION_PASSE:
                this.passesRestantes++;
                break;
            case ACTION_DEPLACEMENT:
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
        this.deplacementsRestants = NOMBRE_DEPLACEMENTS_MAX;
        this.passesRestantes = NOMBRE_PASSES_MAX;
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
