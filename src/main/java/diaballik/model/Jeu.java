package diaballik.model;

import diaballik.Utils;
import diaballik.scene.SceneJeu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

public class Jeu extends Observable {
    private Terrain terrain;
    private final Joueur[] joueurs;

    private final ConfigurationPartie configurationPartie;

    public Historique historique;

    private final ArrayList<Point> arriveeJoueurVert = new ArrayList<>();
    private final ArrayList<Point> arriveeJoueurRouge = new ArrayList<>();

    private int tour;
    private int numAction;
    private int joueurActuel;

    private final SceneJeu sceneJeu;

    public final static int NOMBRE_JOUEURS = 2;

    public final static int VICTOIRE_NORMALE = 1;
    public final static int VICTOIRE_ANTIJEU = 2;

    private final static String VERSION_SAUVEGARDE = "0.1.1";

    public Jeu(SceneJeu sceneJeu, ConfigurationPartie configurationPartie) throws OutdatedSave, IOException {
        this.sceneJeu = sceneJeu;
        this.configurationPartie = configurationPartie;

        initArrivee();

        this.joueurs = new Joueur[NOMBRE_JOUEURS];

        charger();

        getJoueurActuel().preparerJouer();

        updateListeners(SignalUpdate.GLOBAL);
    }

    // Initialise les lignes d'arrivées
    private void initArrivee() {
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            arriveeJoueurVert.add(new Point(i, 0));
            arriveeJoueurRouge.add(new Point(i, 6));
        }
    }

    // charge le jeu (avec la config qui contient le chemin du terrain & save)
    private void charger() throws OutdatedSave, IOException {
        try (BufferedReader br = Utils.readerConditionnel(getConfigurationPartie().getCheminFichier(), getConfigurationPartie().estUneSauvegarde())) {
            String sCurrentLine;
            String parts[];

            if (getConfigurationPartie().estUneSauvegarde()) {
                if ((sCurrentLine = br.readLine()) != null) {
                    String version = Utils.getSaveVersion(sCurrentLine);
                    if (!version.equals(VERSION_SAUVEGARDE)) {
                        throw new OutdatedSave(version);
                    }
                }

                // tour actuel
                if ((sCurrentLine = br.readLine()) != null) {
                    parts = sCurrentLine.split(":");
                    this.tour = Integer.parseInt(parts[0]);
                    this.joueurActuel = this.tour - 1;
                    this.numAction = Integer.parseInt(parts[1]);
                    getConfigurationPartie().setDureeTimer(Integer.parseInt(parts[2]));
                }

                // joueur 1
                if ((sCurrentLine = br.readLine()) != null) {
                    this.joueurs[0] = constructJoueur(Integer.parseInt(sCurrentLine.split(":")[1]), Joueur.VERT, sCurrentLine.split(":")[0]);
                }

                // joueur 2
                if ((sCurrentLine = br.readLine()) != null) {
                    this.joueurs[1] = constructJoueur(Integer.parseInt(sCurrentLine.split(":")[1]), Joueur.ROUGE, sCurrentLine.split(":")[0]);
                }
            } else {
                this.tour = 1;
                this.joueurActuel = 0;
                this.numAction = 1;

                this.joueurs[0] = constructJoueur(getConfigurationPartie().getTypeJoueur1(), Joueur.VERT, getConfigurationPartie().getNomJoueur1());
                this.joueurs[1] = constructJoueur(getConfigurationPartie().getTypeJoueur2(), Joueur.ROUGE, getConfigurationPartie().getNomJoueur2());
            }

            terrain = new Terrain(br);

            if (getConfigurationPartie().estUneSauvegarde()) {
                historique = new Historique(this, br);

                this.setNumAction();
            } else {
                historique = new Historique(this);
            }
        } catch (IOException ioe) {
            System.err.println("(Jeu.<init>) Erreur de lecture dans le flux d'entrée");
            throw ioe;
        } catch (OutdatedSave os) {
            System.err.println("(Jeu.<init>) Sauvegarde non compatible (" + os.versionFound + " trouvée, " + VERSION_SAUVEGARDE + " requise)");
            throw os;
        }
    }

    // Construit un joueur
    private Joueur constructJoueur(int type, int couleur, String nom) {
        Joueur j;

        if (type == 0)
            j = new JoueurLocal(this, couleur);
        else if (type == 1 || type == 2 || type == 3)
            j = new JoueurIA(this, couleur, type);
        else
            j = new JoueurReseau(this, couleur);

        j.setNom(nom);

        return j;
    }

    // set le numAction en fonction de l'historique
    private void setNumAction() {
        this.numAction = historique.nombreActions(tour) + 1;

        for (int i = 0; i < historique.getNombrePassesRetirer(tour); i++) {
            getJoueurActuel().moinsAction(Action.PASSE);
        }

        for (int i = 0; i < historique.getNombreDeplacementRetirer(tour); i++) {
            getJoueurActuel().moinsAction(Action.DEPLACEMENT);
        }
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    // Save the game state into cheminFichier
    public void sauvegarde(String chemin) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(chemin))) {
            bw.write("[v" + VERSION_SAUVEGARDE + "] Sauvegarde Diaballik\n");
            bw.write(this.getTour() + ":" + this.numAction + ":" + getConfigurationPartie().getDureeTimer() + "\n");
            bw.write(this.joueurs[0].getSaveString());
            bw.write(this.joueurs[1].getSaveString());
            bw.write(this.getTerrain().getSaveString());
            bw.write(historique.getSaveString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // renvoie une chaine vide si antijeu valide; sinon la raison de l'échec de la fonction
    public String antijeu() {
        // 1: On vérifie si ils sont tous sur une colonne différente
        Pion[] tab = new Pion[7]; // on se souvient de la ligne des pions
        for (Pion p : getTerrain().getPions()[getJoueurAdverse().getCouleur()]) { // pour chaque pion du joueur adversaire
            if (tab[p.getPosition().getPoint().getX()] != null) {
                return "étape 1 non valide (pions adverses ne prennent pas la largeur du terrain)";
            } else {
                tab[p.getPosition().getPoint().getX()] = p;
            }
        }

        // 2: On vérifie qu'ils bloquent le passage (qu'ils sont côte à côte, direct ou en diagonal)
        int i = 1;
        Pion p1 = tab[0], p2 = tab[i];
        while (++i < 7) {
            if (Math.abs(p1.getPosition().getPoint().getY() - p2.getPosition().getPoint().getY()) > 1) {
                return "étape 2 non valide (pions adverses non collés)";
            }

            p1 = p2;
            p2 = tab[i];
        }

        // 3: on vérifie que le joueur qui a appelé actionAntijeu a bien 3 pions collés au mur
        int n = 0;
        Point po;
        Case c;
        for (Pion p : getTerrain().getPions()[getJoueurActuel().getCouleur()]) { // pour chaque pion du joueur actuel
            po = p.getPosition().getPoint();

            if (n >= 3) break;

            // vérif case de gauche
            c = getTerrain().getCaseSur(new Point(po.getX() - 1, po.getY()));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case du haut
            c = getTerrain().getCaseSur(new Point(po.getX(), po.getY() + 1));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case de droite
            c = getTerrain().getCaseSur(new Point(po.getX() + 1, po.getY()));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case du bas
            c = getTerrain().getCaseSur(new Point(po.getX(), po.getY() - 1));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
            }
        }

        if (n >= 3) {
            if (getConfigurationPartie().estMultijoueur()) {
                Action aj = new Action(Action.ANTIJEU);
                sceneJeu.getReseau().envoyerAction(aj);
            }

            sceneJeu.finJeu(getJoueurActuel(), VICTOIRE_ANTIJEU);

            return "";
        } else {
            return "étape 3 non valide (pions alliés collés à la ligne adverse = " + n + "/3)";
        }
    }

    // Effectue un déplacement (le pion p se déplace sur la case c)
    public boolean deplacement(Action action) {
        Pion p = action.getCaseAvant().getPion();
        Case c = action.getCaseApres();

        if (!p.aLaBalle() && deplacementPossible(p.getPosition(), c)) {
            p.deplacer(c);

            historique.addAction(action);

            this.getJoueurActuel().moinsAction(action);
            this.numAction++;

            preparerJoueur();

            updateListeners(SignalUpdate.INFOS);

            return true;
        } else {
            return false;
        }
    }

    // Vérifie la possibilité d'un déplacement (uniquement par rapport aux coordonnées)
    private boolean deplacementPossible(Case c1, Case c2) {
        Point p = c1.getPoint();
        Point p2 = c2.getPoint();

        // On cherche un point commun (x ou y)
        if (p.getY() == p2.getY()) {
            if (Math.abs(p.getX() - p2.getX()) == 1) { // il faut un différentiel de 1 pour qu'il soit à côté
                return true;
            }
        } else if (p.getX() == p2.getX()) {
            if (Math.abs(p.getY() - p2.getY()) == 1) { // il faut un différentiel de 1 pour qu'il soit à côté
                return true;
            }
        }

        return false;
    }

    public ArrayList<Case> getDeplacementsPossibles(Pion pion) {
        if (!getJoueurActuel().peutDeplacer()) return new ArrayList<>();

        return terrain.getDeplacementsPossibles(pion);
    }
    public ArrayList<Pion> getPassesPossibles(Pion pion) {
        if (!getJoueurActuel().peutPasser()) return new ArrayList<>();

        return terrain.getPassesPossibles(pion);
    }

    // Vérifie la possibilité d'une passe (uniquement dans les axes, la couleur n'est pas vérifiée, ...)
    private boolean passePossible(Pion envoyeur, Pion receptionneur) {
        Point pEnvoyeur = envoyeur.getPosition().getPoint();
        Point pReceptionneur = receptionneur.getPosition().getPoint();

        // check alignement
        if (pEnvoyeur.getX() == pReceptionneur.getX()) { // en ligne
            int yMax = Math.max(pEnvoyeur.getY(), pReceptionneur.getY());
            int yMin = Math.min(pEnvoyeur.getY(), pReceptionneur.getY());

            for (int y = yMax - 1; y > yMin; y--) {
                Pion pionPresent = getTerrain().getCaseSur(new Point(pEnvoyeur.getX(), y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (pEnvoyeur.getY() == pReceptionneur.getY()) { // colonne
            int xMax = Math.max(pEnvoyeur.getX(), pReceptionneur.getX());
            int xMin = Math.min(pEnvoyeur.getX(), pReceptionneur.getX());

            for (int x = xMax - 1; x > xMin; x--) {
                Pion pionPresent = getTerrain().getCaseSur(new Point(x, pEnvoyeur.getY())).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (Math.abs(pEnvoyeur.getX() - pReceptionneur.getX()) == Math.abs(pEnvoyeur.getY() - pReceptionneur.getY())) { // diagonale
            int xMax = pReceptionneur.getX();
            int yMax = pReceptionneur.getY();
            int x = pEnvoyeur.getX();
            int y = pEnvoyeur.getY();
            Pion pionPresent;
            do {
                if (pReceptionneur.getX() > x) x++;
                else x--;

                if (pReceptionneur.getY() > y) y++;
                else y--;

                pionPresent = getTerrain().getCaseSur(new Point(x, y)).getPion();

                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur())
                    return false;
            } while (x != xMax && y != yMax);
        } else {
            return false;
        }

        return true;
    }

    // Effectue une passe d'un pion p vers un pion positionné sur une case c
    public boolean passe(Action action) {
        Pion envoyeur = action.getCaseAvant().getPion();
        Pion receptionneur = action.getCaseApres().getPion();

        if (envoyeur.aLaBalle() && passePossible(envoyeur, receptionneur)) {
            envoyeur.passe(receptionneur);

            historique.addAction(action);

            this.getJoueurActuel().moinsAction(action);
            this.numAction++;

            preparerJoueur();

            updateListeners(SignalUpdate.INFOS);

            return true;
        } else {
            return false;
        }
    }

    // retourne vrai si la partie est terminée (le vainqueur est le joueur actuel)
    private boolean partieTerminee(Pion pion) {
        Point p = pion.getPosition().getPoint();

        if (this.getJoueurActuel().getCouleur() == Joueur.VERT) {
            for (Point e : arriveeJoueurRouge) {
                if (e.getX() == p.getX() && e.getY() == p.getY()) {
                    return true;
                }
            }
        } else {
            for (Point e : arriveeJoueurVert) {
                if (e.getX() == p.getX() && e.getY() == p.getY()) {
                    return true;
                }
            }
        }

        return false;
    }
    public boolean partieTerminee() {
        for (Pion p : this.terrain.getPions()[getJoueurActuel().getCouleur()]) {
            if (p.aLaBalle() && partieTerminee(p)) {
                return true;
            }
        }

        return false;
    }

    public void preparerJoueur() {
        getJoueurActuel().preparerJouer();
    }

    // Change le tour actuel (change aussi le joueur actuel)
    public void avancerTour() {
        historique.ecraserInutile();
        historique.ajouterTour();
        getJoueurActuel().reset_actions();
        this.tour++;
        this.numAction = 1;
        joueurActuel = ++joueurActuel % Jeu.NOMBRE_JOUEURS;
        preparerJoueur();

        updateListeners(SignalUpdate.TOUR);

        /*if (getJoueurActuel() instanceof JoueurIA)
            ((JoueurIA) getJoueurActuel()).jouerIA();*/
    }
    public void reculerTour() {
        this.tour--;

        joueurActuel = --joueurActuel < 0 ? Jeu.NOMBRE_JOUEURS - 1 : joueurActuel;

        setNumAction();

        while (getJoueurActuel().estUneIA()) {
            defaire();
            if (tour == 1 && numAction == 1)
                break;
        }

        preparerJoueur();

        updateListeners(SignalUpdate.TOUR);
    }

    public Joueur getJoueurActuel() {
        return this.joueurs[joueurActuel % NOMBRE_JOUEURS];
    }
    public Joueur getJoueurAdverse() {
        return this.joueurs[(joueurActuel + 1) % NOMBRE_JOUEURS];
    }

    public void updateListeners(Object o) {
        this.setChanged();
        this.notifyObservers(o);
    }

    public int getTour() {
        return tour;
    }

    public void defaire() {
        if (!historique.peutDefaire()) return;

        Action a = historique.getActionTourNum(this.tour, this.numAction - 1);

        if (a == null) {
            if (getConfigurationPartie().estMultijoueur())
                return;

            if (this.tour > 1)
                reculerTour();

            return;
        }

        this.numAction--;

        this.executerAction(a, true);

        if (getConfigurationPartie().estMultijoueur()) {
            a.setInverse(true);
            sceneJeu.getReseau().envoyerAction(a);
        }
    }
    public void refaire() {
        if (!historique.peutRefaire()) return;

        Action a = historique.getActionTourNum(this.tour, this.numAction);

        if (a == null) {
            if (getConfigurationPartie().estMultijoueur())
                return;

            if (this.historique.tourExiste(tour + 1))
                avancerTour();

            return;
        }

        this.numAction++;

        this.executerAction(a, false);

        if (getConfigurationPartie().estMultijoueur())
            sceneJeu.getReseau().envoyerAction(a);
    }

    public int getNumAction() {
        return numAction;
    }

    // Execute l'action passée en paramètre, et ajoute une action au joueur actuel si defaire = true (enlève si defaire = false)
    // retourne true si action effectuée
    // false sinon
    public void executerAction(Action a, boolean defaire) {
        boolean succes = false;

        switch (a.getAction()) {
            case Action.DEPLACEMENT:
                if (defaire) {
                    a.getCaseApres().getPion().deplacer(a.getCaseAvant());
                } else {
                    a.getCaseAvant().getPion().deplacer(a.getCaseApres());
                }

                succes = true;
                break;
            case Action.PASSE:
                if (defaire) {
                    a.getCaseApres().getPion().passe(a.getCaseAvant().getPion());
                } else {
                    a.getCaseAvant().getPion().passe(a.getCaseApres().getPion());
                }

                succes = true;
                break;
            default:
                System.err.println("Action non reconnue");
        }

        if (succes)
            if (defaire)
                getJoueurActuel().plusAction(a);
            else
                getJoueurActuel().moinsAction(a);

        if (getConfigurationPartie().estMultijoueur())
            preparerJoueur();

        updateListeners(SignalUpdate.GLOBAL);
    }

    public boolean pionAllie(Pion pion) {
        return pion.getCouleur() == getJoueurActuel().getCouleur();
    }

    public void mapperDepuisReseau(Action a) {
        a.setCaseAvant(terrain.getCaseSur(a.getCaseAvant().getPoint()));
        a.setCaseApres(terrain.getCaseSur(a.getCaseApres().getPoint()));
    }

    public SceneJeu getSceneJeu() {
        return sceneJeu;
    }

    public ConfigurationPartie getConfigurationPartie() {
        return configurationPartie;
    }
}
