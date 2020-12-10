package com.wjslegers.platformer;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.wjslegers.platformer.Entities.Entity;

import java.util.HashMap;
import java.util.Map;

public class Viewport {
    private final PointF _lookAt = new PointF(0f, 0f);
    public static int _pixelsPerMeterX; //viewport "density"
    public static int _pixelsPerMeterY;
    private int _screenWidth; //resolution
    private int _screenHeight;
    private int _screenCenterY; //center screen
    private int _screenCenterX;
    private float _metersToShowX; //Field of View
    private float _metersToShowY;
    private float _halfDistX; //cached value (0.5*FOV)
    private float _halfDistY;
    private final static float BUFFER = 2f; //overdraw, to avoid visual gaps
    private Map <String, Float> _viewCache = new HashMap<String, Float>();

    public Viewport(final int screenWidth, final int screenHeight, final float metersToShowX, final float metersToShowY) {
        _screenWidth = screenWidth;
        _screenHeight = screenHeight;
        _screenCenterX = _screenWidth / 2;
        _screenCenterY = _screenHeight / 2;
        _lookAt.x = 0.0f;
        _lookAt.y = 0.0f;
        setMetersToShow(metersToShowX, metersToShowY);
    }

    private void setMetersToShow(float metersToShowX, float metersToShowY) {
        if (metersToShowX <= 0f && metersToShowY <= 0f)
            throw new IllegalArgumentException("One of the dimensions must be provided!");
        //formula: new height = (original height / original width) x new width
        _metersToShowX = metersToShowX;
        _metersToShowY = metersToShowY;
        if (metersToShowX == 0f || metersToShowY == 0f) {
            if (metersToShowY > 0f) { //if Y is configured, calculate X
                _metersToShowX = ((float) _screenWidth / _screenHeight) * metersToShowY;
            } else { //if X is configured, calculate Y
                _metersToShowY = ((float) _screenHeight / _screenWidth) * metersToShowX;
            }
        }
        _halfDistX = (_metersToShowX + BUFFER) * 0.5f;
        _halfDistY = (_metersToShowY + BUFFER) * 0.5f;
        _pixelsPerMeterX = (int) (_screenWidth / _metersToShowX);
        _pixelsPerMeterY = (int) (_screenHeight / _metersToShowY);
    }


    public void worldToScreen(final float worldPosX, final float worldPosY, Point screenPos){
        screenPos.x = (int) (_screenCenterX - ((_lookAt.x - worldPosX) * _pixelsPerMeterX));
        screenPos.y = (int) (_screenCenterY - ((_lookAt.y - worldPosY) * _pixelsPerMeterY));
    }
    public void worldToScreen(final PointF worldPos, Point screenPos){
        worldToScreen(worldPos.x, worldPos.y, screenPos);
    }
    public void worldToScreen(final Entity e, final Point screenPos){
        worldToScreen(e._x, e._y, screenPos);
    }


    public void cacheView() {
        _viewCache.clear();
        final float maxX = (_lookAt.x + _halfDistX);
        final float preMinX = (_lookAt.x - _halfDistX);
        final float maxY = (_lookAt.y + _halfDistY);
        final float preMinY  = (_lookAt.y - _halfDistY);
        
        _viewCache.put("maxX", maxX);
        _viewCache.put("preMinX", preMinX);
        _viewCache.put("maxY", maxY);
        _viewCache.put("preMinY", preMinY);
    }

    /*public boolean inView(final Entity e) {
        final float right = (_lookAt.x + _halfDistX);
        final float left = (_lookAt.x - _halfDistX) - e._width;
        final float bottom = (_lookAt.y + _halfDistY);
        final float top  = (_lookAt.y - _halfDistY) - e._height;
        return (e._x > left && e._x < right)
                && (e._y > bottom && e._y < top);
    }//more or less how it was before*/

    public boolean inView(final Entity e) {
        return (e._x > (_viewCache.get("preMinX") - e._width) && e._x < _viewCache.get("maxX"))
                && (e._y > (_viewCache.get("preMinY") - e._height) && e._y < _viewCache.get("maxY"));
    }

    public boolean inView(final RectF bounds) {
        final float right = (_lookAt.x + _halfDistX);
        final float left = (_lookAt.x - _halfDistX);
        final float bottom = (_lookAt.y + _halfDistY);
        final float top  = (_lookAt.y - _halfDistY);
        return (bounds.left < right && bounds.right > left)
                && (bounds.top < bottom && bounds.bottom > top);
    }

    public void lookAt(final float x, final float y){
        _lookAt.x = (_lookAt.x * (1-config.FOLLOW_PERCENTAGE)) + x * config.FOLLOW_PERCENTAGE;
        _lookAt.y = (_lookAt.y * (1-config.FOLLOW_PERCENTAGE)) + y * config.FOLLOW_PERCENTAGE;
    }
    //public void lookAt(final GameObject obj){lookAt(obj.centerX(), obj.centerY());}
    public void lookAt(final PointF pos){
        lookAt(pos.x, pos.y);
    }
    public void lookAt(final Entity e){
        if (e != null){
            lookAt(e._x, e._y);
        }
    }

    public float getHorizontalView(){
        return _metersToShowX;
    }
    public float getVerticalView(){
        return _metersToShowY;
    }
    public int getScreenWidth() {
        return _screenWidth;
    }
    public int getScreenHeight(){
        return _screenHeight;
    }
    public int getPixelsPerMeterX(){
        return _pixelsPerMeterX;
    }
    public int getPixelsPerMeterY(){
        return _pixelsPerMeterY;
    }
}