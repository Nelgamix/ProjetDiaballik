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
                        couleur = Joueur.JOUEUR_VERT;
                        break;
                    case "R":
                        couleur = Joueur.JOUEUR_ROUGE;
                        break;
                    case "BV":
                        couleur = Joueur.JOUEUR_VERT;
                        pionBalle = true;
                        break;
                    case "BR":
                        couleur = Joueur.JOUEUR_ROUGE;
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

                    if (couleur == Joueur.JOUEUR_VERT)
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

    public Pion getPionOf(int joueur, int num) {
        return this.pions[joueur][num];
    }

    public Case getCaseAt(Point point) {
        return this.cases[point.getY()][point.getX()];
    }

    public String getPionRepr(Pion pion) {
        String res = "";

        if (pion == null)
            return "0";

        if (pion.aLaBalle())
            res += "B";

        if (pion.getCouleur() == Joueur.JOUEUR_VERT)
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
                sb.append(getPionRepr(c.getPion()));
            }
            skip = true;
            sb.append("\n");
        }

        return sb.toString();
    }
}
