package com.wjslegers.platformer.Entities;

import com.wjslegers.platformer.config;

public class GravityEntity extends DynamicEntity {
    public float _gravity = config.GRAVITY;
    public boolean _isOnGround = false;
    public boolean _hitHead = false;

    public GravityEntity(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
    }

    @Override
    public void update(double dt) {
        if(!_isOnGround){
            final float gravityThisTick = (float) (_gravity*dt);
            _velY += gravityThisTick;
        }
        super.update(dt);//last, clamps speeds
        _isOnGround = false;
    }

    @Override
    public void onCollision(Entity that) {
        _hitHead = false;
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;

        if(Entity.overlap.y != 0){
            _velY = 0;
            if(Entity.overlap.y < 0f){//hit feet(?)
                _isOnGround = true;
            }else {//hit head(?)
                _hitHead = true;
            }
        }
    }
}
