package sample.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.controller.TerrainController;
import sample.model.Case;
import sample.model.Joueur;
import sample.model.Pion;

import java.util.Observable;
import java.util.Observer;

public class PionView extends Rectangle {
    private Pion pion;
    private TerrainController terrainController;

    private final static int HAUTEUR = 25;
    private final static int LARGEUR = 25;

    public PionView(TerrainController terrainController, Pion pion, CaseView caseView) {
        super(HAUTEUR, LARGEUR);
        this.pion = pion;
        this.terrainController = terrainController;

        this.setFill((pion.getCouleur() == Joueur.COULEUR_VERT ? Color.GREEN : Color.RED));
        this.setOnMouseClicked(e -> mouseClicked());
    }

    private void mouseClicked() {
        System.out.println("Bitch " + pion.getNumero() + " " + pion.getCouleur());
        this.terrainController.setPionSelected(this.pion);
    }
}
