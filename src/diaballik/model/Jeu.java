package diaballik.model;

import diaballik.Diaballik;

import java.io.*;
import java.util.ArrayList;
import java.util.Observable;

public class Jeu extends Observable {
    private class Action {
        Pion pion;
        int action;
        Case caseAvant;

        public Action(Pion pion, int action, Case caseAvant) {
            this.pion = pion;
            this.action = action;
            this.caseAvant = caseAvant;
        }

        public Action(Jeu jeu, String s) {
            String[] parts = s.split(":");
            Point pointPion = new Point(parts[0]);
            this.pion = jeu.getTerrain().getCaseAt(pointPion).getPion();
            this.action = Integer.parseInt(parts[1]);
            Point pointCaseAvant = new Point(parts[2]);
            this.caseAvant = jeu.getTerrain().getCaseAt(pointCaseAvant);
        }

        public String getSaveString() {
            StringBuilder sb = new StringBuilder();

            sb.append(this.pion.getPosition().getPoint().getSaveString())
                    .append(":")
                    .append(action)
                    .append(":")
                    .append(this.caseAvant.getPoint().getSaveString());

            return sb.toString();
        }

        public Pion getPion() {
            return pion;
        }

        public int getAction() {
            return action;
        }

        public Case getCaseAvant() {
            return caseAvant;
        }

        @Override
        public String toString() {
            return action + ": " + pion + " était sur " + caseAvant;
        }
    }

    private Terrain terrain;
    private final Joueur[] joueurs;

    private final ArrayList<Action> historique = new ArrayList<>();

    private final ArrayList<Point> arriveeJoueurVert = new ArrayList<>();
    private final ArrayList<Point> arriveeJoueurRouge = new ArrayList<>();

    private int tour;
    private int joueurActuel;

    private final static String nomsDisponiblesPath = "nomsDisponibles.txt";
    private final ArrayList<String> nomsDisponibles = new ArrayList<>();

    private final Diaballik diaballik;

    public final static int NOMBRE_JOUEURS = 2;

    public final static int CHANGED_INFOS = 1;
    public final static int CHANGED_TOUR = 2;
    public final static int CHANGED_ALL = 3;

    public final static int VICTOIRE_NORMALE = 1;
    public final static int VICTOIRE_ANTIJEU = 2;

    public Jeu(ConfigurationPartie cp, Diaballik diaballik) {
        this.diaballik = diaballik;

        initArrivee();
        initNomsDisponibles();

        this.joueurs = new Joueur[NOMBRE_JOUEURS];
        this.joueurs[0] = new Joueur(this, Joueur.JOUEUR_VERT);
        this.joueurs[1] = new Joueur(this, Joueur.JOUEUR_ROUGE);

        load(cp);

        this.joueurActuel = this.getTour() - 1;

        updateListeners(CHANGED_ALL);
    }

    private void initArrivee() {
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            arriveeJoueurVert.add(new Point(i, 0));
            arriveeJoueurRouge.add(new Point(i, 6));
        }
    }

    private void initNomsDisponibles() {
        try (BufferedReader br = new BufferedReader(new FileReader(nomsDisponiblesPath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                nomsDisponibles.add(sCurrentLine);
            }
        } catch (IOException ignored) {}
    }

    public String getNomAleatoire() {
        int index = (int)Math.floor(Math.random() * nomsDisponibles.size());
        String nomChoisi = nomsDisponibles.get(index);
        nomsDisponibles.remove(index);
        return nomChoisi;
    }

    public void load(ConfigurationPartie cp) {
        try (BufferedReader br = new BufferedReader(new FileReader(cp.path))) {
            String sCurrentLine;
            String parts[];
            Joueur joueur;

            if (cp.isSave) {
                // tour actuel
                if ((sCurrentLine = br.readLine()) != null) {
                    this.tour = Integer.parseInt(sCurrentLine);
                }

                // joueur 1
                if ((sCurrentLine = br.readLine()) != null) {
                    parts = sCurrentLine.split(":");
                    joueur = this.joueurs[0];

                    joueur.setNom(parts[0]);
                    joueur.setDeplacementsRestants(Integer.parseInt(parts[1]));
                    joueur.setPassesRestantes(Integer.parseInt(parts[2]));
                }

                // joueur 2
                if ((sCurrentLine = br.readLine()) != null) {
                    parts = sCurrentLine.split(":");
                    joueur = this.joueurs[1];

                    joueur.setNom(parts[0]);
                    joueur.setDeplacementsRestants(Integer.parseInt(parts[1]));
                    joueur.setPassesRestantes(Integer.parseInt(parts[2]));
                }
            } else {
                this.tour = 1;

                while (!this.joueurs[0].setNom(cp.nomJoueur1) && !this.joueurs[0].setNom(getNomAleatoire()));
                while (!this.joueurs[1].setNom(cp.nomJoueur2) && !this.joueurs[1].setNom(getNomAleatoire()));
            }

            StringBuilder terrainString = new StringBuilder();
            int y = 0;
            while (y < Terrain.HAUTEUR && (sCurrentLine = br.readLine()) != null) {
                terrainString.append(sCurrentLine).append("\n");
                y++;
            }

            terrain = new Terrain(terrainString.toString());

            Action a;
            while ((sCurrentLine = br.readLine()) != null) { // on lit les configurations
                a = new Action(this, sCurrentLine);
                historique.add(a);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    // Save the game state into path
    public void save(String path) {
        // On écrit les infos suivantes (qui caractérisent l'état du jeu)
        // tour
        // nomJ1:depl:pass
        // nomJ2:depl:pass
        // terrain...
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(this.getTour() + "\n");
            bw.write(this.joueurs[0].getSaveString());
            bw.write(this.joueurs[1].getSaveString());
            bw.write(this.getTerrain().getSaveString());
            for (Action a : historique) bw.write(a.getSaveString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean antijeu() {
        // 1: On vérifie si ils sont tous sur une colonne différente
        Pion[] tab = new Pion[7]; // on se souvient de la ligne des pions
        for (Pion p : getTerrain().getPions()[getJoueurAdverse().getCouleur()]) { // pour chaque pion du joueur adversaire
            if (tab[p.getPosition().getPoint().getX()] != null) {
                System.out.println("Antijeu: etape 1 non valide");
                return false;
            } else {
                tab[p.getPosition().getPoint().getX()] = p;
            }
        }

        // 2: On vérifie qu'ils bloquent le passage (qu'ils sont côte à côte, direct ou en diagonal)
        int i = 1;
        Pion p1 = tab[0], p2 = tab[i];
        while (++i < 7) {
            if (Math.abs(p1.getPosition().getPoint().getY() - p2.getPosition().getPoint().getY()) > 1) {
                System.out.println("Antijeu: etape 2 non valide");
                return false;
            }

            p1 = p2;
            p2 = tab[i];
        }

        // 3: on vérifie que le joueur qui a appelé antijeu a bien 3 pions collés au mur
        int n = 0;
        Point po;
        Case c;
        for (Pion p : getTerrain().getPions()[getJoueurActuel().getCouleur()]) { // pour chaque pion du joueur actuel
            po = p.getPosition().getPoint();

            if (n >= 3) break;

            // vérif case de gauche
            c = getTerrain().getCaseAt(new Point(po.getX() - 1, po.getY()));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case du haut
            c = getTerrain().getCaseAt(new Point(po.getX(), po.getY() + 1));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case de droite
            c = getTerrain().getCaseAt(new Point(po.getX() + 1, po.getY()));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
                continue;
            }

            // vérif case du bas
            c = getTerrain().getCaseAt(new Point(po.getX(), po.getY() - 1));
            if (c != null && c.getPion() != null && c.getPion().getCouleur() == getJoueurAdverse().getCouleur()) {
                n++;
            }
        }

        if (n >= 3) {
            diaballik.endGame(getJoueurActuel(), VICTOIRE_ANTIJEU);
            return true;
        } else {
            System.out.println("Antijeu: etape 3 non valide (n = " + n + ")");
            return false;
        }
    }

    // Effectue un déplacement (le pion p se déplace sur la case c)
    public void deplacement(Pion p, Case c) {
        if (!p.aLaBalle() && deplacementPossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_DEPLACEMENT)) {
            System.out.println("Déplacement!");

            Case cv = p.getPosition();

            p.deplacer(c);

            historique.add(new Action(p, Joueur.ACTION_DEPLACEMENT, cv));

            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_DEPLACEMENT)) {
                changerTour();
            }

            updateListeners(CHANGED_INFOS);
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

    // TODO: améliorer ce truc!!
    public ArrayList<Case> getDeplacementsPossibles(Pion pion) {
        ArrayList<Case> c = new ArrayList<>();

        if (getJoueurActuel().getDeplacementsRestants() < 1) return c;

        Point pbase = pion.getPosition().getPoint();
        Case ca;

        ca = getTerrain().getCaseAt(new Point(pbase.getX() + 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseAt(new Point(pbase.getX(), pbase.getY() + 1));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseAt(new Point(pbase.getX() - 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) c.add(ca);
        ca = getTerrain().getCaseAt(new Point(pbase.getX(), pbase.getY() - 1));
        if (ca != null && ca.getPion() == null) c.add(ca);

        return c;
    }

    public ArrayList<Pion> getPassesPossibles(Pion pion) {
        ArrayList<Pion> pions = new ArrayList<>();

        if (getJoueurActuel().getPassesRestantes() < 1) return pions;

        Case c;
        int couleur = getJoueurActuel().getCouleur();
        Point p = pion.getPosition().getPoint();

        int i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() - ++i, p.getY()))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() + ++i, p.getY()))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX(), p.getY() + --i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX(), p.getY() + ++i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() + ++i, p.getY() + i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() + ++i, p.getY() - i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() + --i, p.getY() + i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        i = 0;
        while ((c = getTerrain().getCaseAt(new Point(p.getX() + --i, p.getY() - i))) != null && (c.getPion() == null || pionAllie(c.getPion()))) {
            if (c.getPion() != null && pionAllie(c.getPion()))
            pions.add(c.getPion());
        }

        return pions;
    }

    // Vérifie la possibilité d'une passe (uniquement dans les axes, la couleur n'est pas vérifiée, ...)
    private boolean passePossible(Case c1, Case c2) {
        Point p = c1.getPoint();
        Point p2 = c2.getPoint();

        // check alignement
        if (p.getX() == p2.getX()) { // en ligne
            int yMax = Math.max(p.getY(), p2.getY());
            int yMin = Math.min(p.getY(), p2.getY());

            for (int y = yMax - 1; y > yMin; y--) {
                Pion pionPresent = getTerrain().getCaseAt(new Point(p.getX(), y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (p.getY() == p2.getY()) { // colonne
            int xMax = Math.max(p.getX(), p2.getX());
            int xMin = Math.min(p.getX(), p2.getX());

            for (int x = xMax - 1; x > xMin; x--) {
                Pion pionPresent = getTerrain().getCaseAt(new Point(x, p.getY())).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != getJoueurActuel().getCouleur()) {
                    return false;
                }
            }
        } else if (Math.abs(p.getX() - p2.getX()) == Math.abs(p.getY() - p2.getY())) { // diagonale
            int xMax = Math.max(p.getX(), p2.getX());
            int xMin = Math.min(p.getX(), p2.getX());
            int yMax = Math.max(p.getY(), p2.getY());

            int y = yMax;
            for (int x = xMax - 1; x > xMin; x--) {
                y--;
                Pion pionPresent = getTerrain().getCaseAt(new Point(x, y)).getPion();
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
    public void passe(Pion p, Case c) {
        if (p.aLaBalle() && passePossible(p.getPosition(), c) && this.getJoueurActuel().actionPossible(Joueur.ACTION_PASSE)) {
            System.out.println("Passe!");

            Case cv = p.getPosition();

            p.passe(c);

            historique.add(new Action(c.getPion(), Joueur.ACTION_PASSE, cv));

            if (partieTerminee(c.getPion())) {
                // la partie est terminée (le vainqueur est joueurActuel())
                diaballik.endGame(getJoueurActuel(), VICTOIRE_NORMALE);
            }

            if (!this.getJoueurActuel().moinsAction(Joueur.ACTION_PASSE)) {
                changerTour();
            }

            updateListeners(CHANGED_INFOS);
        }
    }

    // retourne vrai si la partie est terminée (le vainqueur est le joueur actuel)
    private boolean partieTerminee(Pion pion) {
        Point p = pion.getPosition().getPoint();

        if (this.getJoueurActuel().getCouleur() == Joueur.JOUEUR_VERT) {
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

    // Change le tour actuel (change aussi le joueur actuel)
    public void changerTour() {
        historique.clear();
        getJoueurActuel().reset_actions();
        this.tour++;
        joueurActuel = ++joueurActuel % Jeu.NOMBRE_JOUEURS;
        //joueurActuel = (joueurActuel + 1 >= Jeu.NOMBRE_JOUEURS ? 0 : joueurActuel + 1);

        updateListeners(CHANGED_TOUR);
    }

    public Joueur getJoueurActuel() {
        return this.joueurs[joueurActuel % NOMBRE_JOUEURS];
    }

    public Joueur getJoueurAdverse() {
        return this.joueurs[(joueurActuel + 1) % NOMBRE_JOUEURS];
    }

    public ArrayList<Action> getHistorique() {
        return historique;
    }

    private void updateListeners(Object o) {
        this.setChanged();
        this.notifyObservers(o);
    }

    public int getTour() {
        return tour;
    }

    public void rollback() {
        if (historique.size() == 0) return;

        Action a = historique.get(historique.size() - 1);
        historique.remove(historique.size() - 1);

        switch (a.getAction()) {
            case Joueur.ACTION_DEPLACEMENT:
                a.getPion().deplacer(a.getCaseAvant());
                getJoueurActuel().plusAction(Joueur.ACTION_DEPLACEMENT);
                break;
            case Joueur.ACTION_PASSE:
                a.getPion().passe(a.getCaseAvant());
                getJoueurActuel().plusAction(Joueur.ACTION_PASSE);

                break;
            default:
                System.err.println("Action non reconnue");
        }

        updateListeners(CHANGED_ALL);
    }

    public boolean pionAllie(Pion pion) {
        return pion.getCouleur() == getJoueurActuel().getCouleur();
    }
}
