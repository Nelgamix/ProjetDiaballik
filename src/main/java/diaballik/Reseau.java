package diaballik;

import diaballik.model.Action;
import javafx.application.Platform;
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

    void fermerReseau() {
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

    boolean isRunning() {
        return running;
    }

    public void host() {
        reseauThread = Executors.newSingleThreadExecutor();
        this.running = true;
        reseauThread.execute(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                clientSocket = serverSocket.accept();
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                Platform.runLater(d::close);
                Platform.runLater(() -> diaballik.nouveauJeuReseau(1));
            } catch (IOException io) {}
        });
    }

    public void client(String remote) {
        reseauThread = Executors.newSingleThreadExecutor();
        this.running = true;
        reseauThread.execute(() -> {
            try {
                clientSocket = new Socket(remote, PORT);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                Platform.runLater(d::close);
                Platform.runLater(() -> diaballik.nouveauJeuReseau(0));
            } catch (IOException io) {}
        });
    }

    public void envoyerAction(Action a) {
        reseauThread.execute(() -> {
            try {
                oos.reset();
                oos.writeObject(a);
                if (a.isInverse()) a.setInverse(false);
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
    }

    public void recevoirAction() {
        reseauThread.execute(() -> {
            try {
                Action a = (Action)ois.readObject();
                Platform.runLater(() -> actionRecue(a));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void actionRecue(Action a) {
        if (a.getAction() == Action.FINTOUR) {
            diaballik.getJeu().avancerTour();
        } else if (a.isInverse()) {
            diaballik.getJeu().mapperDepuisReseau(a);
            diaballik.getJeu().executerAction(a, true);
        } else {
            diaballik.getJeu().mapperDepuisReseau(a);
            diaballik.getJeu().getJoueurActuel().setActionAJouer(a);
            diaballik.getJeu().getJoueurActuel().jouer();
        }
    }
}
