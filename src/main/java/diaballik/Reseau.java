package diaballik;

import diaballik.model.Action;
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
    private final Diaballik diaballik;
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

    Reseau(Diaballik diaballik) {
        this.diaballik = diaballik;
        this.running = false;
    }

    boolean estOccupe() {
        return reseauThread != null && !reseauThread.isTerminated();
    }

    public void fermerReseau() {
        System.out.println("Shutdown");
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
    public void setTacheActuelle(Tache tacheActuelle) {
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
                Platform.runLater(() -> diaballik.reseau.preconfigServeur(nom, terrain));
            } catch (IOException io) {}
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
                Platform.runLater(() -> diaballik.reseau.preconfigClient(nom));
            } catch (IOException io) {}
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
                Platform.runLater(diaballik.reseau::deconnecte);
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
                Platform.runLater(diaballik.reseau::deconnecte);
            }
            setTacheActuelle(Tache.IDLE);
        });
    }

    public void preconfigServeur(String nom, String terrain) {
        reseauThread.execute(() -> {
            try {
                String nomClient = (String)ois.readObject();

                oos.reset();
                oos.writeObject(nom);
                oos.writeObject(terrain);

                Platform.runLater(() -> diaballik.nouveauJeuReseau(1, nom, nomClient, terrain));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void preconfigClient(String nom) {
        reseauThread.execute(() -> {
            try {
                oos.reset();
                oos.writeObject(nom);

                String nomServ = (String)ois.readObject();
                String terrain = (String)ois.readObject();

                Platform.runLater(() -> diaballik.nouveauJeuReseau(0, nomServ, nom, terrain));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void actionRecue(Action a) {
        switch (a.getAction()) {
            case Action.FINTOUR:
                diaballik.getJeu().avancerTour();
                break;
            case Action.ANTIJEU:
                Platform.runLater(diaballik.getJeu()::antijeu);
                break;
            default:
                if (a.isInverse()) {
                    diaballik.getJeu().mapperDepuisReseau(a);
                    diaballik.getJeu().executerAction(a, true);
                } else {
                    diaballik.getJeu().mapperDepuisReseau(a);
                    diaballik.getJeu().getJoueurActuel().setActionAJouer(a);
                    diaballik.getJeu().getJoueurActuel().jouer();
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

        diaballik.showSceneMenu();
    }
}
