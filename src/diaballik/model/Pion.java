package diaballik.model;

import java.util.Observable;

public class Pion extends Observable {
    private boolean aLaBalle;
    private int couleur;
    private int numero;
    private Case position;
    private boolean selectionne;
    private boolean marque;

    public Pion(int couleur, int numero, Case position) {
        this.aLaBalle = false;
        this.couleur = couleur;
        this.numero = numero;
        this.position = position;
        this.selectionne = false;
        this.marque = false;
    }

    public void setSelectionne(boolean b) {
        this.selectionne = b;
        updateListeneners();
    }

    public void setMarque(boolean b) {
        this.marque = b;
        updateListeneners();
    }

    public boolean isSelectionne() {
        return selectionne;
    }

    public void setaLaBalle(boolean aLaBalle) {
        this.aLaBalle = aLaBalle;
    }

    public boolean aLaBalle() {
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

        updateListeneners();
    }

    public void passe(Case caseAlliee) {
        if (caseAlliee.getPion() == null) { // prérequis: la case où on pass doit être occupée
            System.err.println("Pion (passe): Case destination libre");
            return;
        }

        this.setaLaBalle(false); // on n'a plus la balle
        caseAlliee.getPion().setaLaBalle(true); // la balle est au pion de la case alliée
        caseAlliee.getPion().updateListeneners();

        updateListeneners();
    }

    public void updateListeneners() {
        this.setChanged();
        this.notifyObservers();
    }

    public boolean isMarque() {
        return marque;
    }

    @Override
    public String toString() {
        return this.getPosition().getPoint().toString();
    }
}
