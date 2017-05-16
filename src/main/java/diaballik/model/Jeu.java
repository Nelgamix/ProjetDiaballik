package diaballik.model;

import diaballik.Diaballik;
import diaballik.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

public class Jeu extends Observable {
    private Terrain terrain;
    private final Joueur[] joueurs;

    public final ConfigurationPartie cp;

    public final Historique historique = new Historique(this);

    private final ArrayList<Point> arriveeJoueurVert = new ArrayList<>();
    private final ArrayList<Point> arriveeJoueurRouge = new ArrayList<>();

    private int tour;
    private int numAction;
    int joueurActuel;

    private final ArrayList<String> nomsDisponibles = new ArrayList<>();

    final Diaballik diaballik;

    public final static int NOMBRE_JOUEURS = 2;

    public final static int CHANGEMENT_INIT = 0;
    public final static int CHANGEMENT_POSITION = 1;
    public final static int CHANGEMENT_INFOS = 2;
    public final static int CHANGEMENT_TOUR = 3;
    public final static int CHANGEMENT_GLOBAL = 4;

    public final static int VICTOIRE_NORMALE = 1;
    public final static int VICTOIRE_ANTIJEU = 2;

    public Jeu(ConfigurationPartie cp, Diaballik diaballik) {
        this.diaballik = diaballik;
        this.cp = cp;

        initArrivee();

        this.joueurs = new Joueur[NOMBRE_JOUEURS];

        charger(cp);

        this.joueurActuel = this.getTour() - 1;

        getJoueurActuel().preparerJouer();

        updateListeners(CHANGEMENT_GLOBAL);
    }

    private void initArrivee() {
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            arriveeJoueurVert.add(new Point(i, 0));
            arriveeJoueurRouge.add(new Point(i, 6));
        }
    }

    public void charger(ConfigurationPartie cp) {
        try (BufferedReader br = Utils.readerConditionnel(cp.cheminFichier, cp.estUneSauvegarde)) {
            String sCurrentLine;
            String parts[];

            if (cp.estUneSauvegarde) {
                // tour actuel
                if ((sCurrentLine = br.readLine()) != null) {
                    parts = sCurrentLine.split(":");
                    this.tour = Integer.parseInt(parts[0]);
                    this.numAction = Integer.parseInt(parts[1]);
                }

                // joueur 1
                if ((sCurrentLine = br.readLine()) != null) {
                    this.joueurs[0] = new JoueurLocal(this, Joueur.VERT, sCurrentLine);
                }

                // joueur 2
                if ((sCurrentLine = br.readLine()) != null) {
                    this.joueurs[1] = new JoueurLocal(this, Joueur.ROUGE, sCurrentLine);
                }
            } else {
                this.tour = 1;
                this.numAction = 1;

                if (cp.typeJoueur1 == 0)
                    this.joueurs[0] = new JoueurLocal(this, Joueur.VERT);
                else if (cp.typeJoueur1 == 1 || cp.typeJoueur1 == 2 || cp.typeJoueur1 == 3)
                    this.joueurs[0] = new JoueurIA(this, Joueur.VERT, cp.typeJoueur1);
                else
                    this.joueurs[0] = new JoueurReseau(this, Joueur.VERT);

                if (cp.typeJoueur2 == 0)
                    this.joueurs[1] = new JoueurLocal(this, Joueur.ROUGE);
                else if (cp.typeJoueur2 == 1 || cp.typeJoueur2 == 2 || cp.typeJoueur2 == 3)
                    this.joueurs[1] = new JoueurIA(this, Joueur.ROUGE, cp.typeJoueur2);
                else
                    this.joueurs[1] = new JoueurReseau(this, Joueur.ROUGE);

                if (!this.joueurs[0].setNom(cp.nomJoueur1)) throw new IllegalStateException();
                if (!this.joueurs[1].setNom(cp.nomJoueur2)) throw new IllegalStateException();
            }

            StringBuilder terrainString = new StringBuilder();
            int y = 0;
            while (y < Terrain.HAUTEUR && (sCurrentLine = br.readLine()) != null) {
                terrainString.append(sCurrentLine).append("\n");
                y++;
            }

            terrain = new Terrain(terrainString.toString());

            if (cp.estUneSauvegarde) {
                while ((sCurrentLine = br.readLine()) != null) // on lit les configurations
                    historique.addAction(this, sCurrentLine);

                this.setNumAction();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

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
        // On écrit les infos suivantes (qui caractérisent l'état du jeu)
        // tour
        // nomJ1:depl:pass
        // nomJ2:depl:pass
        // terrain...
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(chemin))) {
            bw.write(this.getTour() + ":" + this.numAction + "\n");
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
            diaballik.finJeu(getJoueurActuel(), VICTOIRE_ANTIJEU);
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
            System.out.println("Déplacement!");

            p.deplacer(c);

            //historique.ecraserFinHistorique(this.tour, this.numAction);
            historique.addAction(action);

            this.getJoueurActuel().moinsAction(action);
            this.numAction++;
            //if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_DEPLACEMENT)) {
            //    avancerTour();
            //}

            preparerJoueur();

            updateListeners(CHANGEMENT_INFOS);

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
        ArrayList<Case> c = new ArrayList<>();

        if (!getJoueurActuel().peutDeplacer()) return c;

        Point pbase = pion.getPosition().getPoint();
        Case ca;

        ca = getTerrain().getCaseSur(new Point(pbase.getX() + 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseSur(new Point(pbase.getX(), pbase.getY() + 1));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseSur(new Point(pbase.getX() - 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseSur(new Point(pbase.getX(), pbase.getY() - 1));
        if (ca != null && ca.getPion() == null) c.add(ca);

        return c;
    }

    public ArrayList<Pion> getPassesPossibles(Pion pion) {
        ArrayList<Pion> pions = new ArrayList<>();

        if (!getJoueurActuel().peutPasser()) return pions;

        Case c;
        Point p = pion.getPosition().getPoint();

        int i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() - ++i, p.getY()))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() + ++i, p.getY()))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX(), p.getY() + --i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX(), p.getY() + ++i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() + ++i, p.getY() + i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() + ++i, p.getY() - i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() + --i, p.getY() + i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseSur(new Point(p.getX() + --i, p.getY() - i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        return pions;
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
            int xMax = Math.max(pEnvoyeur.getX(), pReceptionneur.getX());
            int xMin = Math.min(pEnvoyeur.getX(), pReceptionneur.getX());
            int yMax = Math.max(pEnvoyeur.getY(), pReceptionneur.getY());

            int y = yMax;
            for (int x = xMax - 1; x > xMin; x--) {
                y--;
                Pion pionPresent = getTerrain().getCaseSur(new Point(x, y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
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
            System.out.println("Passe!");

            envoyeur.passe(receptionneur);

            historique.addAction(action);

            if (partieTerminee(receptionneur)) {
                // la partie est terminée (le vainqueur est joueurActuel())
                diaballik.finJeu(getJoueurActuel(), VICTOIRE_NORMALE);
            }

            this.getJoueurActuel().moinsAction(action);
            this.numAction++;
            //if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_PASSE)) {
            //    avancerTour();
            //}

            preparerJoueur();

            updateListeners(CHANGEMENT_INFOS);

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

    public void preparerJoueur() {
        getJoueurActuel().preparerJouer();
    }

    // Change le tour actuel (change aussi le joueur actuel)
    public void avancerTour() {
        historique.ecraserInutile(tour, numAction);
        historique.ajouterTour();
        getJoueurActuel().reset_actions();
        this.tour++;
        this.numAction = 1;
        joueurActuel = ++joueurActuel % Jeu.NOMBRE_JOUEURS;
        preparerJoueur();

        updateListeners(CHANGEMENT_TOUR);

        /*if (getJoueurActuel() instanceof JoueurIA)
            ((JoueurIA) getJoueurActuel()).jouerIA();*/
    }
    public void reculerTour() {
        this.tour--;

        joueurActuel = --joueurActuel < 0 ? Jeu.NOMBRE_JOUEURS - 1 : joueurActuel;

        setNumAction();

        updateListeners(CHANGEMENT_TOUR);
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
        Action a = historique.getActionTourNum(this.tour, this.numAction - 1);

        if (a == null) {
            if (cp.multijoueur)
                return;

            if (this.tour > 1)
                reculerTour();

            return;
        }

        this.numAction--;

        this.executerAction(a, true);

        a.setInverse(true);
        if (cp.multijoueur)
            diaballik.reseau.envoyerAction(a);
    }
    public void refaire() {
        Action a = historique.getActionTourNum(this.tour, this.numAction);

        if (a == null) {
            if (cp.multijoueur)
                return;

            if (this.historique.tourExiste(tour + 1))
                avancerTour();

            return;
        }

        this.numAction++;

        this.executerAction(a, false);

        System.out.println("Refaire");
        if (cp.multijoueur)
            diaballik.reseau.envoyerAction(a);
    }

    public int getNumAction() {
        return numAction;
    }

    public boolean joueurActuelReseau() {
        return cp.multijoueur && getJoueurActuel() instanceof JoueurReseau;
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

        preparerJoueur();

        updateListeners(CHANGEMENT_GLOBAL);
    }

    public boolean pionAllie(Pion pion) {
        return pion.getCouleur() == getJoueurActuel().getCouleur();
    }

    public void mapperDepuisReseau(Action a) {
        a.setCaseAvant(terrain.getCaseSur(a.getCaseAvant().getPoint()));
        a.setCaseApres(terrain.getCaseSur(a.getCaseApres().getPoint()));
    }
}
