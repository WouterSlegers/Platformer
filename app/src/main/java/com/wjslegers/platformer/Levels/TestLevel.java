package com.wjslegers.platformer.Levels;

import android.util.SparseArray;

public class TestLevel extends LevelData {
    private final SparseArray<String> _TileIDtoSpriteName = new SparseArray<>();

    public TestLevel(){
        _TileIDtoSpriteName.put(0, "player_left1");
        _TileIDtoSpriteName.put(1, PLAYER);
        _TileIDtoSpriteName.put(2, "tile_square");
        _TileIDtoSpriteName.put(3, "tile_top");
        _TileIDtoSpriteName.put(4, "tile_top_left");
        _TileIDtoSpriteName.put(5, "tile_top_right");
        _TileIDtoSpriteName.put(6, "spears_up");

        _Tiles = new int[][]{
                {5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
        };
        updateLevelDimensions();
    }

    @Override
    public String getSpriteName(int tileType) {
        final String fileName = _TileIDtoSpriteName.get(tileType);
        if (fileName != null){
            return fileName;
        }
        return NULLSPRITE;
    }
}
