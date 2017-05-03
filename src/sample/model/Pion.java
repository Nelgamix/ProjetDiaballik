package sample.model;

import java.util.Observable;

public class Pion extends Observable {
    private boolean aLaBalle;
    private int couleur;
    private int numero;
    private Case position;
    private boolean selectionne;

    public Pion(int couleur, int numero, Case position) {
        this.aLaBalle = false;
        this.couleur = couleur;
        this.numero = numero;
        this.position = position;
        this.selectionne = false;
    }

    public void setSelectionne(boolean b) {
        this.selectionne = b;
        this.setChanged();
        this.notifyObservers();
    }

    public boolean isSelectionne() {
        return selectionne;
    }

    public boolean isaLaBalle() {
        return aLaBalle;
    }

    public int getCouleur() {
        return couleur;
    }

    public int getNumero() {
        return numero;
    }

    public Case getPosition() {
        return position;
    }

    public void deplacer(Case nouvellePosition) {
        if (nouvellePosition.getPion() != null) { // prérequis: la case où on va doit être libre
            System.err.println("Pion (deplacer): Case destination non libre");
            return;
        }

        this.position.setPion(null); // plus de pion: on part
        nouvellePosition.setPion(this); // la nouvelle case doit savoir que le pion est celui ci
        this.position = nouvellePosition; // on change donc la position

        this.setChanged();
        this.notifyObservers();
    }
}
