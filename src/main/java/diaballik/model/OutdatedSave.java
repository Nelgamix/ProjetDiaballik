package diaballik.model;

public class OutdatedSave extends Exception {
    String versionFound;

    OutdatedSave(String versionFound) {
        this.versionFound = versionFound;
    }
}
