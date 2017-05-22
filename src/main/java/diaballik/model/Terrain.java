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

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    // Constructeur par copie
    public Terrain(Terrain t) {
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

    @Override
    public int hashCode() {
        int hash = 0;

        for (Case[] c : cases)
            hash += Arrays.hashCode(c);

        return hash;
    }
}
