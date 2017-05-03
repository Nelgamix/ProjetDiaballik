package sample.model;

public class Terrain {
    private final Case cases[][];
    private final Pion pions[][];

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    public Terrain() {
        this.cases = new Case[HAUTEUR][LARGEUR];
        this.pions = new Pion[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                //Point p = new Point(i, j);
                //this.cases.put(p, new Case(p));
                this.cases[i][j] = new Case(new Point(i, j));
            }
        }

        Pion pion;
        for (int i = 0; i < HAUTEUR; i++) {
            pion = new Pion(Joueur.COULEUR_VERT, i, this.cases[i][0]);
            this.cases[i][0].setPion(pion);
            this.pions[Joueur.COULEUR_VERT][i] = pion;
        }

        for (int i = 0; i < HAUTEUR; i++) {
            pion = new Pion(Joueur.COULEUR_ROUGE, i, this.cases[i][6]);
            this.cases[i][6].setPion(pion);
            this.pions[Joueur.COULEUR_ROUGE][i] = pion;
        }

        getCaseAt(new Point(3, 0)).getPion().setaLaBalle(true);
        getCaseAt(new Point(3, 6)).getPion().setaLaBalle(true);
    }

    public Pion getPionAt(Point point) {
        return this.pions[point.getX()][point.getY()];
    }

    public Case getCaseAt(Point point) {
        return this.cases[point.getX()][point.getY()];
    }

    public void setPionOnCase(Point point, Pion pion) {
        this.cases[point.getX()][point.getY()].setPion(pion);
    }

}
