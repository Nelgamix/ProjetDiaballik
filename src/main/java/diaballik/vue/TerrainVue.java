package diaballik.vue;

import diaballik.Diaballik;
import diaballik.controleur.TerrainControleur;
import diaballik.model.Jeu;
import diaballik.model.Joueur;
import diaballik.model.Point;
import diaballik.model.Terrain;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Observable;
import java.util.Observer;

public class TerrainVue extends GridPane implements Observer {
    private final TerrainControleur terrainControleur;
    private final Terrain terrain;

    private final CaseVue[][] cases; // Représentation visuelle du terrain (l'UI)
    private final PionVue[][] pions;

    public TerrainVue(TerrainControleur terrainControleur) {
        super();

        this.terrainControleur = terrainControleur;
        this.terrain = terrainControleur.getJeu().getTerrain();
        terrainControleur.getJeu().addObserver(this);
        this.setId("terrainView");

        this.cases = new CaseVue[Terrain.HAUTEUR][Terrain.LARGEUR];
        this.pions = new PionVue[Jeu.NOMBRE_JOUEURS][Joueur.NOMBRE_PIONS];

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

        this.getColumnConstraints().addAll(cc);
        this.getRowConstraints().addAll(rc);

        for (int i = 0; i < Terrain.HAUTEUR; i++) {
            for (int j = 0; j < Terrain.LARGEUR; j++) {
                CaseVue cv = new CaseVue(this, terrain.getCaseSur(new Point(j, i)));
                this.cases[i][j] = cv;
                this.add(cv, j, i);
            }
        }

        PionVue pv;
        for (int i = 0; i < Jeu.NOMBRE_JOUEURS; i++) {
            for (int j = 0; j < Joueur.NOMBRE_PIONS; j++) {
                pv = new PionVue(this, this.terrain.getPionDe(i, j));
                this.pions[i][j] = pv;
            }
        }

        update(null, null);
    }

    public CaseVue getCaseSur(Point p) {
        return cases[p.getY()][p.getX()];
    }

    public TerrainControleur getTerrainControleur() {
        return terrainControleur;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (terrainControleur.getJeu().getJoueurActuel().getCouleur() == Joueur.VERT) {
            for (PionVue p : pions[Joueur.ROUGE]) p.desactiver();
            for (PionVue p : pions[Joueur.VERT]) p.activer();
        } else {
            for (PionVue p : pions[Joueur.ROUGE]) p.activer();
            for (PionVue p : pions[Joueur.VERT]) p.desactiver();
        }

        int changed_type = (arg != null ? (int)arg : 0);
        Diaballik d = getTerrainControleur().diaballik;
        if (d.getSceneJeu() != null) {
            switch (changed_type) {
                case Jeu.CHANGEMENT_TOUR:
                    d.setCurseurNormal(d.getSceneJeu());
                    terrainControleur.finSelection();
                    break;
                case Jeu.CHANGEMENT_GLOBAL:
                    terrainControleur.finSelection();
                    break;
                default:
                    break;
            }
        }
    }
}
