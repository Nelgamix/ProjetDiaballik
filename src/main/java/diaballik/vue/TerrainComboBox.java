package diaballik.vue;

import diaballik.Utils;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Map;

public class TerrainComboBox extends ComboBox<Map.Entry<String, String>> {
    private Map<String, String> mapTerrainsDispo;

    TerrainComboBox() {
        super();

        mapTerrainsDispo = Utils.getTerrainsDisponibles();

        this.getItems().addAll(mapTerrainsDispo.entrySet());
        this.getSelectionModel().selectFirst();

        this.setCellFactory(new Callback<ListView<Map.Entry<String, String>>, ListCell<Map.Entry<String, String>>>() {

            @Override
            public ListCell<Map.Entry<String, String>> call(ListView<Map.Entry<String, String>> arg0) {
                return new ListCell<Map.Entry<String, String>>() {

                    private final Label l;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        l = new Label();
                    }

                    @Override
                    protected void updateItem(Map.Entry<String, String> item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            l.setText(item.getValue());
                            setGraphic(l);
                        }
                    }
                };
            }
        });
        this.setConverter(new StringConverter<Map.Entry<String, String>>() {
            @Override
            public String toString(Map.Entry<String, String> object) {
                return object == null ? null : object.getValue();
            }

            @Override
            public Map.Entry<String, String> fromString(String string) {
                return null;
            }
        });
    }

    public String getTerrainSelectionnePath() {
        return getSelectionModel().getSelectedItem().getKey();
    }

    public void selectionTerrain(String terrain) {
        if (terrain.equals("")) {
            getSelectionModel().selectFirst();
            return;
        }

        for (Map.Entry<String, String> e : mapTerrainsDispo.entrySet()) {
            if (e.getKey().equals(terrain)) {
                getSelectionModel().select(e);
            }
        }
    }
}
