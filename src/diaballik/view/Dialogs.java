package diaballik.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Package ${PACKAGE} / Project JavaFXML.
 * Date 2017 05 05.
 * Created by Nico (19:29).
 */
public class Dialogs {
    public static boolean confirmByDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'action");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static void showCredits() {
        Dialog<Boolean> credits = new Dialog<>();
        credits.setTitle("Credits");

        credits.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        Optional<Boolean> result = credits.showAndWait();
    }
}
