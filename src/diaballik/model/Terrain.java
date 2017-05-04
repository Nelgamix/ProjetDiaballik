package diaballik.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Terrain {
    private final Case cases[][];
    private final Pion pions[][];

    public final static int HAUTEUR = 7;
    public final static int LARGEUR = 7;

    public Terrain(String file) {
        this.cases = new Case[HAUTEUR][LARGEUR];
        this.pions = new Pion[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                this.cases[i][j] = new Case(new Point(i, j));
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            String parts[];

            int y, x; // pour se repérer et arrêter la boucle
            Pion pion; // le pion qu'on va créer à chaque lecture
            boolean estPion = true; // pour marquer une lecture erronée ou un vide
            int couleur = -1;
            boolean pionBalle = false;

            y = 0;
            while ((sCurrentLine = br.readLine()) != null && y < HAUTEUR) {
                parts = sCurrentLine.split(";");

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
                        pion = new Pion(couleur, x, this.cases[x][y]);
                        this.cases[x][y].setPion(pion);
                        this.pions[couleur][x] = pion;

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
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
