package com.wjslegers.platformer.Levels;

public abstract class LevelData {
    public static final String NULLSPRITE = "nullsprite";
    public static final String BACKGROUND = "wave_tile_square";
    public static final String PLAYER = "player_left1";
    public static final String ACTIVATION_BLOCK = "exclamation_block";
    public static final String SPIKE = "spears_up";
    public static final String COIN = "coin";
    public static final String SWORD_HOR = "sword1";
    public static final String SWORD_VERT = "sword2_vert";
    public static final int NO_TILE = 0;


    public int[][] _Tiles = new int[][]{};
    int _height = 0;
    int _width = 0;

    public int getTile(final int x, final int y){
        return _Tiles[y][x];
    }
    int[] getRow(final int y){
        return _Tiles[y];
    }

    public void updateLevelDimensions(){
        _height = _Tiles.length;
        _width = _Tiles[0].length;
    }

    abstract public String getSpriteName(final int tileType);
}
