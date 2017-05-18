package diaballik;

import diaballik.model.Joueur;
import diaballik.model.JoueurLocal;
import diaballik.model.Metadonnees;
import diaballik.model.Terrain;
import sun.misc.Launcher;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean nomValide(String nom) {
        return nom.length() >= 3 && nom.length() <= 30;
    }

    public static BufferedReader readerConditionnel(String fichier, boolean estSauvegarde) {
        BufferedReader br = null;

        if (estSauvegarde) {
            try {
                br = new BufferedReader(new FileReader(fichier));
            } catch (IOException e) {}
        } else {
            final File jarFile = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            if (jarFile.isFile()) { // depuis le jar
                try {
                    br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fichier), "UTF8"));
                } catch (UnsupportedEncodingException ignored) {}
            } else {
                br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fichier)));
            }
        }

        return br;
    }

    public static String getSaveVersion(String line) {
        String version = "";
        Pattern p = Pattern.compile("((\\d+)\\.)+\\d+");
        Matcher m = p.matcher(line);
        if (m.find()) {
            version = m.group();
        }

        return version;
    }

    public static Metadonnees getMetadonneesSauvegarde(String fichier) {
        Metadonnees md = new Metadonnees();

        try (BufferedReader br = new BufferedReader(new FileReader(Diaballik.DOSSIER_SAUVEGARDES + "/" + fichier + Diaballik.EXTENSION_SAUVEGARDE))) {
            String sCurrentLine;
            if ((sCurrentLine = br.readLine()) != null) { // version
                md.version = Utils.getSaveVersion(sCurrentLine);
            }

            if ((sCurrentLine = br.readLine()) != null) { // tour
                md.tour = Integer.parseInt(sCurrentLine.split(":")[0]);
            }

            md.joueurVert = new JoueurLocal(Joueur.VERT, br);
            md.joueurRouge = new JoueurLocal(Joueur.ROUGE, br);

            md.terrain = new Terrain(br);

            return md;
        } catch (IOException ignored) {}

        return null;
    }

    public static boolean saveExists(String filename) {
        File f = new File(Diaballik.DOSSIER_SAUVEGARDES + "/" + filename + Diaballik.EXTENSION_SAUVEGARDE);
        return f.exists();
    }

    public static List<String> getFichiersDansDossier(String directory, String extension, boolean resource) {
        List<String> results = new ArrayList<>();

        if (resource) {
            final File jarFile = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            if (jarFile.isFile()) { // Si on lance depuis un jar
                final JarFile jar;
                try {
                    jar = new JarFile(jarFile);
                    final Enumeration<JarEntry> entries = jar.entries(); // envoie toutes les entrées du jar
                    while(entries.hasMoreElements()) {
                        final String name = entries.nextElement().getName();
                        if (name.startsWith(directory.substring(1) + "/")) { // filtrer selon le directory
                            if (!name.endsWith("/")) {
                                results.add(name.substring(directory.length()));
                            }
                        }
                    }

                    jar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // Run with IDE
                final URL url = Launcher.class.getResource(directory);
                if (url != null) {
                    try {
                        final File apps = new File(url.toURI());
                        File[] files = apps.listFiles((dir, name) -> name.endsWith(extension));
                        if (files != null) {
                            for (File app : files) {
                                results.add(app.getName());
                            }
                        }
                    } catch (URISyntaxException ignored) {}
                }
            }
        } else {
            File file = new File(directory);

            File[] files = file.listFiles((dir, name) -> name.endsWith(extension));

            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        results.add(f.getName().substring(0, f.getName().length() - extension.length()));
                    }
                }
            }
        }

        return results;
    }

    public static Map<String, String> getTerrainsDisponibles() {
        Map<String, String> ret = new HashMap<>();

        List<String> terrains = getFichiersDansDossier(Diaballik.DOSSIER_TERRAINS, ".txt", true);

        String nomTerrain;
        for (String terrain : terrains) {
            BufferedReader br = readerConditionnel(Diaballik.DOSSIER_TERRAINS + "/" + terrain, false);
            try {
                if ((nomTerrain = br.readLine()) != null) {
                    ret.put(terrain, nomTerrain);
                }
            } catch (IOException ignored) {}
        }

        return ret;
    }

    public static String getExternalIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            return in.readLine();
        } catch (Exception e) {}

        return "inconnue";
    }
    public static String getInternalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe) {}

        return "inconnue";
    }
}
