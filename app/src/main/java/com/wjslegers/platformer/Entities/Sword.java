package com.wjslegers.platformer.Entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wjslegers.platformer.Viewport;
import com.wjslegers.platformer.config;


public class Sword extends DynamicEntity {
    private int _turned = 1;
    public boolean _horizontal;

    public Sword(final String spriteName, final int xpos, final int ypos, final boolean horizontal) {
        super(spriteName, xpos, ypos);
        _horizontal = horizontal;
        _hurts = true;
        if (horizontal) {
            resizeRecenterReload(spriteName, xpos, ypos, 1f, 32f / 103f); //retrieve scale or use targetWidth in bitmaputils
        } else {
            resizeRecenterReload(spriteName, xpos, ypos, 32f / 103f, 1f);
        }

        if (horizontal) {
            _velX = config.SWORD_TRAVEL_SPEED;
        } else {
            _velY = config.SWORD_TRAVEL_SPEED;
        }
    }

    private void turnAround() {
        _velX = -_velX;
        _velY = -_velY;
        _turned = -_turned;
    }


    @Override
    public void update(double dt) {
        if (_horizontal && (_x <= - config.SWORD_OUT_OF_BOUND - _width || _x >= _game.getWorldWidth() + config.SWORD_OUT_OF_BOUND)) {
            turnAround();
        } else if (_y > _game.getWorldHeight() || _y <= - config.SWORD_OUT_OF_BOUND - _height) {
            turnAround();
        }
        super.update(dt);
    }

    @Override
    public void render(final Canvas canvas, final Matrix transform, final Paint paint) {
        if (_horizontal) {
            transform.preScale(_turned, 1);
            if (_turned < 0) {
                final float offset = _game.worldToScreenX(_width);
                transform.postTranslate(offset, 0);
            }
        } else {
            transform.preScale(1, _turned);
            if (_turned < 0) {
                final float offset = _game.worldToScreenX(_height);
                transform.postTranslate(0, offset);
            }
        }
        super.render(canvas, transform, paint);
    }

    @Override
    public void onCollision(Entity that) {
        if (that != _game._level._player) {
            if (_horizontal) {
                Entity.getOverlap(this, that, Entity.overlap);
                _x += Entity.overlap.x;
            } else {
                Entity.getOverlap(this, that, Entity.overlap);
                _y += Entity.overlap.y;
            }
            turnAround();
        } else if (_game._level._player._activated) {
            _game._level.removeEntity(this);
        }
    }
}
