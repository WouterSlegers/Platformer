package com.wjslegers.platformer.Levels;

import android.content.Context;
import android.util.SparseArray;


import java.io.IOException;
import java.io.InputStream;

public class Level extends LevelData {
    public static final String TAG = "Utils";
    private final SparseArray<String> _TileIDtoSpriteName = new SparseArray<>();

    public Level(final Context context, final String level){
        _TileIDtoSpriteName.put(0, "nullsprite");
        _TileIDtoSpriteName.put(1, PLAYER);
        _TileIDtoSpriteName.put(2, "tile_top");
        _TileIDtoSpriteName.put(3, "tile_square");
        _TileIDtoSpriteName.put(4, "tile_topleft");
        _TileIDtoSpriteName.put(5, "tile_top_left");
        _TileIDtoSpriteName.put(6, "tile_topright");
        _TileIDtoSpriteName.put(7, "tile_top_right");
        _TileIDtoSpriteName.put(8, "tile_top_round");
        _TileIDtoSpriteName.put(9, "spears_up");
        _TileIDtoSpriteName.put(10, "coin");
        _TileIDtoSpriteName.put(11, "exclamation_block");
        _TileIDtoSpriteName.put(12, "sword1");
        _TileIDtoSpriteName.put(13, "sword2_vert");


        loadTiles(context, level); //Fills _Tiles
        updateLevelDimensions();
    }

    private void loadTiles(final Context context, String inFile) {
        String tableContents = "";

        try {
            InputStream stream = context.getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tableContents = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] rows = tableContents.split("\\r?\\n");
        int rowLength = (rows[0].split(",")).length;
        int[][] result = new int [rows.length][rowLength];

        for (int i = 0; i < rows.length; i++){
            String[] numbers = rows[i].split(",");

            for (int j = 0; j < numbers.length; j++) {
                try {
                    result[i][j] = Integer.parseInt(numbers[j].trim());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }
        _Tiles = result;
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