package sample.model;

public class Pion {
    private boolean aLaBalle;
    private int couleur;
    private int numero;

    public Pion(int couleur, int numero) {
        this.aLaBalle = false;
        this.couleur = couleur;
        this.numero = numero;
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
}
