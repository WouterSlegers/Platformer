package com.wjslegers.platformer.Entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wjslegers.platformer.Utils.BitmapUtils;
import com.wjslegers.platformer.config;

public class StaticEntity extends Entity {
    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xpos, final int ypos) {
        _x = xpos;
        _y = ypos;
        loadBitmap(spriteName, xpos, ypos);
    }

    protected void loadBitmap(final String spriteName, final int xpos, final int ypos) {
        _bitmap = _game._bitmapPool.createBitmap(spriteName, _width, _height);
    }

    protected void resizeRecenterReload(final String spriteName, final int xpos, final int ypos, final float scaleFactorX, final float scaleFactorY) {
        _width = scaleFactorX * config.DEFAULT_DIMENSION;
        _height = scaleFactorY * config.DEFAULT_DIMENSION;
        _x += 0.5f * (1-scaleFactorX) * config.DEFAULT_DIMENSION;
        _y += 0.5f * (1-scaleFactorY) * config.DEFAULT_DIMENSION;
        loadBitmap(spriteName, xpos, ypos);
    }


    @Override
    public void render(Canvas canvas, final Matrix transform, Paint paint) {
        canvas.drawBitmap(_bitmap, transform, paint);
    }

    @Override
    public void destroy() {

    }
}
