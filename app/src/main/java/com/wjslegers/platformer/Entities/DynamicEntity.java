package com.wjslegers.platformer.Entities;


import com.wjslegers.platformer.Utils.Utils;
import com.wjslegers.platformer.config;


public class DynamicEntity extends StaticEntity{
    private static final float MAX_DELTA = 0.40f;
    public float _velX = 0;
    public float _velY = 0;

    public DynamicEntity(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);

    }

    @Override
    public void update(double dt) {
        _x += Utils.clamp((float)(_velX * dt), -MAX_DELTA, MAX_DELTA);
        _y += Utils.clamp((float)(_velY * dt), -MAX_DELTA, MAX_DELTA);
        if(_y > _game.getWorldHeight()){
            onHittingBottom();
        }
    }

    public void onHittingBottom() {
    }

    @Override
    public void onCollision(Entity that) {
    }
}