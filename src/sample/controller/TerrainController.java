package sample.controller;

import sample.model.Terrain;
import sample.view.TerrainView;

public class TerrainController {
    private final Terrain terrain;
    private final TerrainView terrainView;

    public TerrainController(Terrain terrain) {
        this.terrain = terrain;
        this.terrainView = new TerrainView(this);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public TerrainView getTerrainView() {
        return terrainView;
    }
}
