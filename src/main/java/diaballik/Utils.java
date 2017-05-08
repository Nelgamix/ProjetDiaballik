package diaballik;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
    private final static ArrayList<String> nomsDisponibles = new ArrayList<>();
    private final static Random r = new Random();

    private final static String CHEMIN_NOM_DISPONIBLES = "/nomsDisponibles.txt";

    private static void initNomsDisponibles() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream((CHEMIN_NOM_DISPONIBLES))))) {
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

    public static BufferedReader readerConditionnel(String fichier, boolean estSauvegarde) {
        BufferedReader br = null;

        if (estSauvegarde) {
            try {
                br = new BufferedReader(new FileReader("." + fichier));
            } catch (IOException e) {}
        } else {
            final File jarFile = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            if (jarFile.isFile()) { // depuis le jar
                br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fichier)));
            } else {
                br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fichier)));
            }
        }

        return br;
    }
}
