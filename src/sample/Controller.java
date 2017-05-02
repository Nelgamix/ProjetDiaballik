package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Controller {
    @FXML private Circle circle;

    @FXML
    private void handleClick(Event e) {
        Circle c = (Circle)e.getSource();
        c.setFill(Color.RED);

        Timeline t = new Timeline();

        KeyValue kvx = new KeyValue(c.centerXProperty(), 150);
        KeyValue kvy = new KeyValue(c.centerYProperty(), 150);

        KeyFrame kf = new KeyFrame(Duration.millis(500), kvx, kvy);

        t.getKeyFrames().add(kf);

        t.play();
    }
}
