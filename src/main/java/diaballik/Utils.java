package diaballik;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 08.
 * Created by Nico (15:49).
 */
public class Utils {
    private final static ArrayList<String> nomsDisponibles = new ArrayList<>();
    private final static Random r = new Random();

    private final static String CHEMIN_NOM_DISPONIBLES = "/nomsDisponibles.txt";

    private static void initNomsDisponibles() {
        try (BufferedReader br = new BufferedReader(new FileReader(Utils.class.getResource(CHEMIN_NOM_DISPONIBLES).getFile()))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                nomsDisponibles.add(sCurrentLine);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String getNomAleatoire() {
        if (nomsDisponibles.isEmpty()) initNomsDisponibles();

        String nomChoisi = "";
        int idx = 0;
        while (!nomValide(nomChoisi)) {
            idx = r.nextInt(nomsDisponibles.size());
            nomChoisi = nomsDisponibles.get(idx);
        }

        nomsDisponibles.remove(idx);
        return nomChoisi;
    }

    public static boolean nomValide(String nom) {
        return nom.length() >= 3 && nom.length() <= 30;
    }
}
