package diaballik.vue;

import diaballik.Diaballik;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Point;
import diaballik.model.Terrain;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class TerrainApercu extends StackPane {
    StackPane[][] cases = new StackPane[Terrain.HAUTEUR][Terrain.LARGEUR];

    TerrainApercu() {
        super();

        getStyleClass().add("apercuTerrain");
        //Circle[][] pions = new PionVue[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];
        //this.setStyle("-fx-background-color: #88bfcd");

        getStylesheets().add(getClass().getResource(Diaballik.CSS_JEU).toExternalForm());
    }

    public void setTerrain(Terrain t) {
        GridPane g = new GridPane();

        ColumnConstraints cc[] = new ColumnConstraints[Terrain.LARGEUR];
        RowConstraints rc[] = new RowConstraints[Terrain.HAUTEUR];

        ColumnConstraints c;
        for (int i = 0; i < Terrain.LARGEUR; i++) {
            c = new ColumnConstraints();
            c.setPercentWidth(100*((double)1/Terrain.LARGEUR));
            cc[i] = c;
        }

        RowConstraints r;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            r = new RowConstraints();
            r.setPercentHeight(100*((double)1/Terrain.HAUTEUR));
            rc[i] = r;
        }

        g.getColumnConstraints().addAll(cc);
        g.getRowConstraints().addAll(rc);

        Point p;
        StackPane cv;
        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                cv = new StackPane();
                cv.getStyleClass().add((i + j) % 2 == 0 ? "couleurCasePair" : "couleurCaseImpair");
                cases[i][j] = cv;
                g.add(cv, j, i);
            }
        }

        Circle pv;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                p = t.getPionDe(i, j).getPosition().getPoint();

                if (t.getPionDe(i, j).aLaBalle())
                    pv = new Circle(10);
                else
                    pv = new Circle(8);

                pv.getStyleClass().add(i == 0 ? "couleurJoueurVert" : "couleurJoueurRouge");
                //pions[i][j] = pv;
                cases[p.getY()][p.getX()].getChildren().add(pv);
            }
        }

        getChildren().add(g);
    }
}
