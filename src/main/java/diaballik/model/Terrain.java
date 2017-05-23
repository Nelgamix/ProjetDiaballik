package diaballik.model;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Terrain {
    private String nom;

    private final Case cases[][];
    private final Pion pions[][];

    private final ArrayList<Point> arriveeJoueurVert = new ArrayList<>();
    private final ArrayList<Point> arriveeJoueurRouge = new ArrayList<>();

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    // Constructeur par copie
    public Terrain(Terrain t) {
        initArrivee();

        this.cases = new Case[HAUTEUR][LARGEUR];
        this.pions = new Pion[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        this.nom = t.nom;

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.cases[i][j] = new Case(new Point(j, i));
            }
        }

        Pion pt, p;
        Case ctmp;
        for (int c = 0; c < 2; c++)
            for (int i = 0; i < Joueur.NOMBRE_PIONS; i++) {
                pt = t.getPionDe(c, i);
                ctmp = getCaseSur(pt.getPosition().getPoint());
                p = new Pion(c, ctmp);
                if (pt.aLaBalle()) p.setaLaBalle(true);
                ctmp.setPion(p);
                this.pions[c][i] = p;
            }

        /*System.out.println("Entrée");
        System.out.println(t);
        System.out.println("Sortie");
        System.out.println(this);*/
    }

    // Constructeur depuis un flux d'entrée (eg fichier)
    public Terrain(BufferedReader br) {
        initArrivee();

        this.cases = new Case[HAUTEUR][LARGEUR];
        this.pions = new Pion[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.cases[i][j] = new Case(new Point(j, i));
            }
        }

        int x, y = 0;
        Case cc;
        Pion pion; // le pion qu'on va créer à chaque lecture

        String sCurrentLine, parts[];
        try {
            if ((sCurrentLine = br.readLine()) != null) this.nom = sCurrentLine;

            while (y < HAUTEUR && (sCurrentLine = br.readLine()) != null) {
                parts = sCurrentLine.split(";");

                x = 0;
                for (String c : parts) {
                    if (x == LARGEUR) break;

                    cc = getCaseSur(new Point(x, y));
                    if (!c.equals("0")) {
                        switch (c) {
                            case "V":
                                pion = new Pion(Joueur.VERT, cc);
                                ajouterPionA(Joueur.VERT, pion);
                                break;
                            case "R":
                                pion = new Pion(Joueur.ROUGE, cc);
                                ajouterPionA(Joueur.ROUGE, pion);
                                break;
                            case "BV":
                                pion = new Pion(Joueur.VERT, cc);
                                ajouterPionA(Joueur.VERT, pion);
                                pion.setaLaBalle(true);
                                break;
                            case "BR":
                                pion = new Pion(Joueur.ROUGE, cc);
                                ajouterPionA(Joueur.ROUGE, pion);
                                pion.setaLaBalle(true);
                                break;
                            default:
                                System.err.println("(Terrain.<init>) Terrain invalide.");
                                return;
                        }

                        cc.setPion(pion);
                    }

                    x++;
                }

                y++;
            }
        } catch (IOException ioe) {
            System.err.println("(Terrain.<init>) Erreur de lecture.");
            Platform.exit();
        }
    }

    private void ajouterPionA(int joueur, Pion pion) {
        int i = 0;
        while (i < Joueur.NOMBRE_PIONS && getPionDe(joueur, i) != null) {i++;}

        if (i < Joueur.NOMBRE_PIONS) this.pions[joueur][i] = pion;
    }

    public Pion getPionDe(int joueur, int num) {
        return this.pions[joueur][num];
    }
    Pion[] getPionsDe(int joueur) {
        return this.pions[joueur];
    }

    public Case getCaseSur(Point point) {
        if (estValide(point)) return this.cases[point.getY()][point.getX()];
        else return null;
    }

    private boolean estValide(Point point) {
        return point.getX() >= 0 && point.getX() < LARGEUR && point.getY() >= 0 && point.getY() < HAUTEUR;
    }

    String getPionRepresentation(Pion pion) {
        String res = "";

        if (pion == null)
            return "0";

        if (pion.aLaBalle())
            res += "B";

        if (pion.getCouleur() == Joueur.VERT)
            res += "V";
        else
            res += "R";

        return res;
    }

    String getSaveString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nom).append("\n");
        boolean skip = true;

        for (Case[] cs : cases) {
            for (Case c : cs) {
                if (!skip)
                    sb.append(";");
                else
                    skip = false;
                sb.append(getPionRepresentation(c.getPion()));
            }
            skip = true;
            sb.append("\n");
        }

        return sb.toString();
    }

    public Pion[][] getPions() {
        return pions;
    }
    public Case[][] getCases() {
        return cases;
    }

    ArrayList<Pion> getPassesPossibles(Pion pion) {
        ArrayList<Pion> ret = new ArrayList<>();

        Case c;
        Point p = pion.getPosition().getPoint();

        int i = 0;
        while ((c = getCaseSur(new Point(p.getX() - ++i, p.getY()))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX() + ++i, p.getY()))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX(), p.getY() + --i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX(), p.getY() + ++i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX() + ++i, p.getY() + i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX() + ++i, p.getY() - i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX() + --i, p.getY() + i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        i = 0;
        while ((c = getCaseSur(new Point(p.getX() + --i, p.getY() - i))) != null && (c.getPion() == null || pion.pionAllie(c.getPion()))) {
            if (c.getPion() != null && pion.pionAllie(c.getPion()))
                ret.add(c.getPion());
        }

        return ret;
    }
    ArrayList<Case> getDeplacementsPossibles(Pion pion) {
        ArrayList<Case> ret = new ArrayList<>();

        Point pbase = pion.getPosition().getPoint();
        Case ca;

        ca = getCaseSur(new Point(pbase.getX() + 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) ret.add(ca);
        ca = getCaseSur(new Point(pbase.getX(), pbase.getY() + 1));
        if (ca != null && ca.getPion() == null) ret.add(ca);
        ca = getCaseSur(new Point(pbase.getX() - 1, pbase.getY()));
        if (ca != null && ca.getPion() == null) ret.add(ca);
        ca = getCaseSur(new Point(pbase.getX(), pbase.getY() - 1));
        if (ca != null && ca.getPion() == null) ret.add(ca);

        return ret;
    }

    public boolean passePossible(Point pEnvoyeur, Point pReceptionneur, int couleur) {
        if (!pEnvoyeur.estDansTerrain() || !pReceptionneur.estDansTerrain()) return false;

        // check alignement
        if (pEnvoyeur.getX() == pReceptionneur.getX()) { // en ligne
            int yMax = Math.max(pEnvoyeur.getY(), pReceptionneur.getY());
            int yMin = Math.min(pEnvoyeur.getY(), pReceptionneur.getY());

            for (int y = yMax - 1; y > yMin; y--) {
                Pion pionPresent = getCaseSur(new Point(pEnvoyeur.getX(), y)).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != couleur) {
                    return false;
                }
            }
        } else if (pEnvoyeur.getY() == pReceptionneur.getY()) { // colonne
            int xMax = Math.max(pEnvoyeur.getX(), pReceptionneur.getX());
            int xMin = Math.min(pEnvoyeur.getX(), pReceptionneur.getX());

            for (int x = xMax - 1; x > xMin; x--) {
                Pion pionPresent = getCaseSur(new Point(x, pEnvoyeur.getY())).getPion();
                if (pionPresent != null && pionPresent.getCouleur() != couleur) {
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

                pionPresent = getCaseSur(new Point(x, y)).getPion();

                if (pionPresent != null && pionPresent.getCouleur() != couleur)
                    return false;
            } while (x != xMax && y != yMax);
        } else {
            return false;
        }

        return true;
    }
    public boolean deplacementPossible(Point p, Point p2) {
        if (!p.estDansTerrain() || !p2.estDansTerrain()) return false;

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

    // Initialise les lignes d'arrivées
    private void initArrivee() {
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            arriveeJoueurVert.add(new Point(i, 0));
            arriveeJoueurRouge.add(new Point(i, 6));
        }
    }
    // retourne vrai si la partie est terminée (le vainqueur est le joueur actuel)
    private boolean partieTerminee(Pion pion, int couleur) {
        Point p = pion.getPosition().getPoint();

        if (couleur == Joueur.VERT) {
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
    public boolean partieTerminee(int couleur) {
        for (Pion p : getPionsDe(couleur)) {
            if (p.aLaBalle() && partieTerminee(p, couleur)) {
                return true;
            }
        }

        return false;
    }

    Pion getPionALaBalle(int joueur) {
        for (Pion p : getPionsDe(joueur))
            if (p.aLaBalle()) return p;
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        Terrain toCmp = (Terrain) obj;
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                Point p = new Point(i, j);
                Case c = getCaseSur(p);
                if (c.getPion() != null && !c.getPion().equals(toCmp.getCaseSur(p).getPion())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String s = "";

        for (Case[] c : cases) {
            for (Case ci : c)
                s += ci.toString() + "\t";
            s += "\n";
        }

        return s;
    }

    // TODO: changer
    @Override
    public int hashCode() {
        int hash = 0;

        for (Case[] c : cases)
            hash += Arrays.hashCode(c);

        return hash;
    }
}
