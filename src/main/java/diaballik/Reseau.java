package diaballik;

import diaballik.model.Action;
import diaballik.model.Jeu;
import diaballik.scene.SceneJeu;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Reseau {
    private final SceneJeu sceneJeu;
    public Dialog<Void> d;

    public enum Tache {
        IDLE,
        ATTENTE_CLIENT,
        ATTENTE_SERVEUR,
        ATTENTE_ACTION,
        ENVOI_ACTION
    }
    private Tache tacheActuelle;

    private ExecutorService reseauThread;
    private boolean running;

    private final static int PORT = 42698;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Reseau(SceneJeu sceneJeu) {
        this.sceneJeu = sceneJeu;
        this.running = false;
    }

    private Jeu getJeu() {
        return sceneJeu.getJeu();
    }

    private boolean estOccupe() {
        return reseauThread != null && !reseauThread.isTerminated();
    }

    public void fermerReseau() {
        System.out.println("Fermeture réseau");
        try {
            if (ois != null) ois.close();
            if (ois != null) oos.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

        reseauThread.shutdown();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public Tache getTacheActuelle() {
        return tacheActuelle;
    }
    private void setTacheActuelle(Tache tacheActuelle) {
        this.tacheActuelle = tacheActuelle;
    }

    public void host(String nom, String terrain) {
        reseauThread = Executors.newSingleThreadExecutor();
        this.running = true;
        reseauThread.execute(() -> {
            setTacheActuelle(Tache.ATTENTE_CLIENT);
            try {
                serverSocket = new ServerSocket(PORT);
                clientSocket = serverSocket.accept();
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                setTacheActuelle(Tache.IDLE);
                Platform.runLater(d::close);
                Platform.runLater(() -> sceneJeu.getReseau().preconfigServeur(nom, terrain));
            } catch (IOException ignored) {}
        });
    }
    public void client(String nom, String remote) {
        reseauThread = Executors.newSingleThreadExecutor();
        this.running = true;
        reseauThread.execute(() -> {
            setTacheActuelle(Tache.ATTENTE_SERVEUR);
            try {
                clientSocket = new Socket(remote, PORT);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                setTacheActuelle(Tache.IDLE);
                Platform.runLater(d::close);
                Platform.runLater(() -> sceneJeu.getReseau().preconfigClient(nom));
            } catch (IOException ignored) {}
        });
    }

    public void envoyerAction(Action a) {
        reseauThread.execute(() -> {
            setTacheActuelle(Tache.ENVOI_ACTION);
            try {
                oos.reset();
                oos.writeObject(a);
                if (a.isInverse()) a.setInverse(false);
            } catch (IOException io) {
                Platform.runLater(sceneJeu.getReseau()::deconnecte);
            }
            setTacheActuelle(Tache.IDLE);
        });
    }
    public void recevoirAction() {
        reseauThread.execute(() -> {
            setTacheActuelle(Tache.ATTENTE_ACTION);
            try {
                Action a = (Action)ois.readObject();
                Platform.runLater(() -> actionRecue(a));
            } catch (Exception e) {
                Platform.runLater(sceneJeu.getReseau()::deconnecte);
            }
            setTacheActuelle(Tache.IDLE);
        });
    }

    private void preconfigServeur(String nom, String terrain) {
        reseauThread.execute(() -> {
            try {
                String nomClient = (String)ois.readObject();

                oos.reset();
                oos.writeObject(nom);
                oos.writeObject(terrain);

                Platform.runLater(() -> sceneJeu.nouveauJeuReseau(1, nom, nomClient, terrain));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void preconfigClient(String nom) {
        reseauThread.execute(() -> {
            try {
                oos.reset();
                oos.writeObject(nom);

                String nomServ = (String)ois.readObject();
                String terrain = (String)ois.readObject();

                Platform.runLater(() -> sceneJeu.nouveauJeuReseau(0, nomServ, nom, terrain));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void actionRecue(Action a) {
        switch (a.getAction()) {
            case Action.FINTOUR:
                getJeu().avancerTour();
                break;
            case Action.ANTIJEU:
                Platform.runLater(getJeu()::antijeu);
                break;
            default:
                if (a.isInverse()) {
                    getJeu().mapperDepuisReseau(a);
                    getJeu().executerAction(a, true);
                } else {
                    getJeu().mapperDepuisReseau(a);
                    getJeu().getJoueurActuel().setActionAJouer(a);
                    getJeu().getJoueurActuel().jouer();
                }
        }
    }

    private void deconnecte() {
        fermerReseau();

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Déconnecté");
        alert.setHeaderText("Un joueur a été déconnecté");
        alert.setContentText("Aïe aïe aïe! Vous (ou votre adversaire) a été déconnecté.");

        alert.showAndWait();

        sceneJeu.retourMenu();
    }
}
