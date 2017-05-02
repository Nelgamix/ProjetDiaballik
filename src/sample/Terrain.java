package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 02.
 * Created by Nico (23:38).
 */
public class Terrain extends Pane {
    private final Rectangle[][] rectangle; // Représentation visuelle du terrain (l'UI)
    private final Case[][] cases; // Représentation modèle du terrain (Cases)

    private final Rectangle[][] pions; // Les pions

    private final static int HAUTEUR = 7;
    private final static int LARGEUR = 7;

    public Terrain() {
        this.rectangle = new Rectangle[HAUTEUR][LARGEUR];
        this.cases = new Case[HAUTEUR][LARGEUR];

        this.pions = new Rectangle[2][Joueur.NOMBRE_PIONS];

        int p = 0;
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.rectangle[i][j] = new Rectangle(50, 50);
                this.cases[i][j] = new Case(null);

                this.rectangle[i][j].setFill((p++ % 2 == 0 ? Color.BLUE : Color.ORANGE));
                this.rectangle[i][j].setX(50 * i);
                this.rectangle[i][j].setY(50 * j);
                this.getChildren().add(this.rectangle[i][j]);
            }
            this.pions[0][i] = new Rectangle(25, 25);
            this.pions[0][i].setFill(Color.GREEN);
            this.getChildren().add(this.pions[0][i]);
            this.pions[1][i] = new Rectangle(25, 25);
            this.pions[1][i].setFill(Color.RED);
            this.getChildren().add(this.pions[1][i]);
        }

        Joueur j = new Joueur(this, Joueur.COULEUR_VERT);
        Joueur j2 = new Joueur(this, Joueur.COULEUR_ROUGE);

        update();
    }

    public void setPion(Pion pion, int x, int y) {
        this.cases[x][y].setPion(pion);
    }

    public void update() {
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                Pion p = this.cases[i][j].getPion();
                if (p != null) placerPion(p, i, j);
            }
        }
    }

    public void placerPion(Pion p, int x, int y) {
        System.out.println("truc " + p.getCouleur() + " t " + p.getNumero());
        this.pions[p.getCouleur()][p.getNumero()].setX(12 + x * 50);
        this.pions[p.getCouleur()][p.getNumero()].setY(12 + y * 50);
    }
}
