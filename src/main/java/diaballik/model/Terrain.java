package diaballik.model;

// TODO: x et y *inversé*, ajouter méthodes propres pour l'accès
public class Terrain {
    private final Case cases[][];
    private final Pion pions[][];

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    public Terrain(String terrainString) {
        this.cases = new Case[HAUTEUR][LARGEUR];
        this.pions = new Pion[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.cases[i][j] = new Case(new Point(j, i));
            }
        }

        int x, y = 0;
        Pion pion; // le pion qu'on va créer à chaque lecture
        boolean estPion = true; // pour marquer une lecture erronée ou un vide
        int couleur = -1;
        boolean pionBalle = false;

        int nbv = 0, nbr = 0;

        String[] lines = terrainString.split("\n"), parts;

        while (y < HAUTEUR) {
            parts = lines[y].split(";");

            x = 0;
            for (String c : parts) {
                if (x == LARGEUR) break;

                switch (c) {
                    case "V":
                        couleur = Joueur.VERT;
                        break;
                    case "R":
                        couleur = Joueur.ROUGE;
                        break;
                    case "BV":
                        couleur = Joueur.VERT;
                        pionBalle = true;
                        break;
                    case "BR":
                        couleur = Joueur.ROUGE;
                        pionBalle = true;
                        break;
                    default:
                        estPion = false;
                        break;
                }

                if (estPion) {
                    Case ct = this.cases[y][x];

                    pion = new Pion(couleur, x, ct);
                    this.cases[y][x].setPion(pion);

                    if (couleur == Joueur.VERT)
                        this.pions[couleur][nbv++] = pion;
                    else
                        this.pions[couleur][nbr++] = pion;

                    if (pionBalle) {
                        pion.setaLaBalle(true);
                        pionBalle = false;
                    }
                } else {
                    estPion = true;
                }

                x++;
            }

            y++;
        }
    }

    public Pion getPionDe(int joueur, int num) {
        return this.pions[joueur][num];
    }

    public Case getCaseSur(Point point) {
        if (estValide(point)) return this.cases[point.getY()][point.getX()];
        else return null;
    }

    private boolean estValide(Point point) {
        return point.getX() >= 0 && point.getX() < LARGEUR && point.getY() >= 0 && point.getY() < HAUTEUR;
    }

    public String getPionRepresentation(Pion pion) {
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

    public String getSaveString() {
        StringBuilder sb = new StringBuilder();
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
}
