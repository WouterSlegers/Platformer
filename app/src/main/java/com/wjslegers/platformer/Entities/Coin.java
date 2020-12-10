package com.wjslegers.platformer.Entities;


import com.wjslegers.platformer.Utils.Jukebox;
import com.wjslegers.platformer.config;

public class Coin extends GravityEntity {
    private static double _soundIntervalTimer = 0;
    public Coin(final String spriteName, final int xpos, final int ypos){
        super(spriteName, xpos, ypos);
        resizeRecenterReload(spriteName, xpos, ypos, config.COIN_SCALE_FACTOR, config.COIN_SCALE_FACTOR);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (_soundIntervalTimer >= 0){
            _soundIntervalTimer -= dt;
        }
    }

    @Override
    public void onHittingBottom() {
        _game._level.removeEntity(this);
        _game._jukebox.play(Jukebox.COIN_DESTROY, 0, 1);
    }


    @Override
    public void onCollision(Entity that) {
        super.onCollision(that);
        if(that._hurts){
            _game._level.removeEntity(this);
            _game._jukebox.play(Jukebox.COIN_DESTROY, 0, 1);
        }
        if (_isOnGround){
            _velY = -config.COIN_BOUNCE_SPEED -_velY;
        } else if (_hitHead) {
            _velY = config.COIN_BOUNCE_SPEED - _velY;
        }

        if (that == _game._level._player) {
            if (_game._level._player._activated) {
                _game._level._player._score += 1;
                _game._level.removeEntity(this);
                _game._jukebox.play(Jukebox.PICK_UP, 0, 1);
            } else {
                _velX = config.COIN_TRAVEL_SPEED * (centerX() - _game._level._player.centerX());
                if (_soundIntervalTimer <= 0) {
                    _game._jukebox.play(Jukebox.DENIED, 0, 1);
                    _soundIntervalTimer = config.SOUND_INTERVAL;
                }
            }
        }
    }
}
