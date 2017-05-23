package diaballik.model;

import java.util.Observable;

public class Pion extends Observable {
    private boolean aLaBalle;
    private int couleur;
    private Case position;

    Pion(int couleur, Case position) {
        this.aLaBalle = false;
        this.couleur = couleur;
        this.position = position;
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

        updateListeneners(SignalUpdate.POSITION);
    }
    public void passe(Pion receptionneur) {
        this.setaLaBalle(false); // on n'a plus la balle
        receptionneur.setaLaBalle(true); // la balle est au pion de la case alliée
        receptionneur.updateListeneners(SignalUpdate.PASSE);

        //updateListeneners(Jeu.CHANGEMENT_POSITION);
    }

    public boolean pionAllie(Pion p) {
        return p.couleur == this.couleur;
    }

    private void updateListeneners(SignalUpdate changement) {
        this.setChanged();
        this.notifyObservers(changement);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        Pion p = (Pion) obj;

        return p.getCouleur() == getCouleur() && p.aLaBalle() == aLaBalle();
    }

    @Override
    public String toString() {
        String s = "";
        if (aLaBalle()) s += "B";

        if (couleur == Joueur.VERT) s += "V";
        else s += "R";

        return s;
    }

    @Override
    public int hashCode() {
        return (getCouleur() + 1) + (aLaBalle() ? 2 : 1);
    }
}
