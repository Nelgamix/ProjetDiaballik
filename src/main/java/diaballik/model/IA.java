package diaballik.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class IA {
    private final static int MAX_DEPTH = 2;

    static ConfigurationTerrain minimax(ConfigurationTerrain c, int couleur) {
        return max(c, Integer.MAX_VALUE, couleur, 0, MAX_DEPTH);
    }
    public static ConfigurationTerrain meilleurTour(ConfigurationTerrain c, int couleur, int deplacementsRestants, int passesRestantes) {
        Random r = new Random();

        HashSet<ConfigurationTerrain> cs = enumAll2(c, couleur, deplacementsRestants, passesRestantes);
        ArrayList<ConfigurationTerrain> max = new ArrayList<>();
        int evalMax = Integer.MIN_VALUE;
        int evalAct;
        for (ConfigurationTerrain ct : cs) {
            if (ct.gagne(couleur)) {
                max.clear();
                max.add(ct);

                break;
            }

            evalAct = ct.fullEval(couleur);
            if (evalAct > evalMax) {
                max.clear();
                max.add(ct);
                evalMax = evalAct;
            } else if (evalAct == evalMax)
                max.add(ct);
        }

        return max.get(r.nextInt(max.size()));
    }
    static ConfigurationTerrain meilleurTour(ConfigurationTerrain c, int couleur) {
        return meilleurTour(c, couleur, Joueur.DEPLACEMENTS_MAX, Joueur.PASSES_MAX);
    }

    private static ConfigurationTerrain min(ConfigurationTerrain config, int valMin, int couleur, int depth, int maxDepth) {
        ConfigurationTerrain min = null, tmp;
        int evalMin = Integer.MAX_VALUE, evalAct;

        int couleurAdv = (couleur+1)%2;

        if (depth == maxDepth)
            return config;

        for (ConfigurationTerrain c : enumAll2(config, couleurAdv)) {
            if (c.gagne(couleurAdv)) return c;

            tmp = max(c, evalMin, couleur, depth + 1, maxDepth);
            evalAct = tmp.fullEval(couleur);

            if (evalAct < evalMin && !tmp.gagne(couleur)) { // si on trouve une config avec une valeur inférieure
                evalMin = evalAct;
                min = tmp;
            }

            if (evalMin < valMin) { // cutoff si la val trouvée est inférieure au min
                //System.out.println("Cutoff min");
                return min;
            }
        }

        return min;
    }
    private static ConfigurationTerrain max(ConfigurationTerrain config, int valMax, int couleur, int depth, int maxDepth) {
        ConfigurationTerrain max = null, tmp;
        int evalMax = Integer.MIN_VALUE, evalAct;

        int couleurAdv = (couleur+1)%2;

        if (depth == maxDepth)
            return config;

        for (ConfigurationTerrain c : enumAll2(config, couleur)) {
            if (c.gagne(couleur)) return c;

            tmp = min(c, evalMax, couleur, depth + 1, maxDepth);

            evalAct = tmp.fullEval(couleur);

            if (evalAct > evalMax && !tmp.gagne(couleurAdv)) {
                evalMax = evalAct;
                max = tmp;
            }

            if (valMax < evalMax) {
                //System.out.println("Cutoff max");
                return max;
            }
        }

        return max;
    }

    @Deprecated
    private static HashSet<ConfigurationTerrain> enumAll(ConfigurationTerrain config, int couleur) {
        HashSet<ConfigurationTerrain> H = new HashSet<>();
        H.add(config);

        // Déplacement en 1
        for (ConfigurationTerrain c : enumDeplacements(config, couleur)) {
            H.add(c);

            // Passe en 2
            for (ConfigurationTerrain c2 : enumPasses(c, couleur)) {
                H.add(c2);

                // Déplacement en 3
                H.addAll(enumDeplacements(c2, couleur));
            }

            // Déplacement en 2
            for (ConfigurationTerrain c2 : enumDeplacements(c, couleur)) {
                H.add(c2);

                // Passe en 3
                H.addAll(enumPasses(c2, couleur));
            }
        }

        // Passe en 1
        for (ConfigurationTerrain c : enumPasses(config, couleur)) {
            H.add(c);

            // Déplacement en 2
            for (ConfigurationTerrain c2 : enumDeplacements(c, couleur)) {
                H.add(c2);

                // Déplacement en 3
                H.addAll(enumDeplacements(c2, couleur));
            }
        }

        //System.out.println(H.size() + " configurations trouvées");
        /*for (Configuration c : H) {
            System.out.println(c);
        }*/

        return H;
    }
    private static HashSet<ConfigurationTerrain> enumAll2(ConfigurationTerrain config, int couleur) {
        return enumAll2(config, couleur, Joueur.DEPLACEMENTS_MAX, Joueur.PASSES_MAX);
    }
    private static HashSet<ConfigurationTerrain> enumAll2(ConfigurationTerrain config, int couleur, int deplacementsRestants, int passesRestantes) {
        HashSet<ConfigurationTerrain> H = new HashSet<>();
        H.add(config);

        if (deplacementsRestants > 0)
            for (ConfigurationTerrain c : enumDeplacements(config, couleur))
                H.addAll(enumAll2(c, couleur, deplacementsRestants - 1, passesRestantes));

        if (passesRestantes > 0)
            for (ConfigurationTerrain c : enumPasses(config, couleur))
                H.addAll(enumAll2(c, couleur, deplacementsRestants, passesRestantes - 1));

        return H;
    }
    private static ArrayList<ConfigurationTerrain> enumDeplacements(ConfigurationTerrain config, int couleur) {
        Terrain terrain = config.getTerrain();
        ArrayList<ConfigurationTerrain> tmp = new ArrayList<>();

        ConfigurationTerrain c;
        for (Pion p : terrain.getPionsDe(couleur)) {
            if (!p.aLaBalle()) {
                for (Case m : terrain.getDeplacementsPossibles(p)) {
                    c = new ConfigurationTerrain(config);
                    c.addAction(new Action(p.getPosition(), Action.DEPLACEMENT, m));
                    c.deplacement();
                    tmp.add(c);
                }
            }
        }

        return tmp;
    }
    private static ArrayList<ConfigurationTerrain> enumPasses(ConfigurationTerrain config, int couleur) {
        Terrain terrain = config.getTerrain();
        ArrayList<ConfigurationTerrain> tmp = new ArrayList<>();

        if (config.gagne(couleur)) {
            tmp.add(config);
            return tmp;
        }

        ConfigurationTerrain c;
        for (Pion p : terrain.getPionsDe(couleur)) {
            if (p.aLaBalle()) {
                for (Pion p2 : terrain.getPassesPossibles(p)) {
                    c = new ConfigurationTerrain(config);
                    c.addAction(new Action(p.getPosition(), Action.PASSE, p2.getPosition()));
                    c.passe();
                    tmp.add(c);
                }
            }
        }

        return tmp;
    }
}
