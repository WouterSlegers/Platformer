package com.wjslegers.platformer.Entities;


import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wjslegers.platformer.Input.InputManager;
import com.wjslegers.platformer.Utils.Jukebox;
import com.wjslegers.platformer.Utils.Utils;
import com.wjslegers.platformer.Viewport;
import com.wjslegers.platformer.config;

public class Player extends GravityEntity {
    static final String TAG = "Player";

    private boolean _standingStill = false;
    private int _facing = LEFT;
    private static final int LEFT = 1;
    private static final int RIGHT = -1;
    private float[] _resetTransform = {
            1f, 0, 0, 0, 0,
            0, 1f, 0, 0, 0,
            0, 0, 1f, 0, 0,
            0, 0, 0, 1f, 0};

    public int _score = 0;
    public int _health = config.STARTING_HEALTH;
    public boolean _invincible = false;
    private double _invincibleCounter = 0;
    public boolean _activated = false;
    private double _activatedTimer = 0;
    public boolean _playedPowerUp = false;
    public boolean _playedPowerDown = false;

    private final Paint _playerPaint = new Paint();
    private ColorMatrix _colorMatrix = new ColorMatrix();
    private ColorMatrixColorFilter _colorFilter = new ColorMatrixColorFilter(_colorMatrix);

    public Player(final String spriteName, final int xpos, final int ypos) {
        super(spriteName, xpos, ypos);
        resizeRecenterReload(spriteName, xpos, ypos, config.PLAYER_SCALE_FACTOR, config.PLAYER_SCALE_FACTOR);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX += direction * config.PLAYER_ACC_SPEED;
        _velX *= config.DRAG;
        _velX = Utils.clamp(_velX, -config.MAX_SPEED_X, config.MAX_SPEED_X);
        updateFacingDirection(direction);
        if (controls._isJumping && _isOnGround) {
            _velY = config.JUMP_FORCE;
            _isOnGround = false;
        }

        if (_invincible) {
            _invincibleCounter += dt;
            if (_invincibleCounter >= config.INVINCIBLE_TIME) {
                _invincible = false;
                _playerPaint.setAlpha(255);
            }
        }

        if (_activated){
            if (_activatedTimer >= 0){
                if (!_playedPowerDown && _activatedTimer <= Jukebox.POWER_DOWN_LENGTH){
                    _game._jukebox.play(Jukebox.POWER_DOWN, 0, 1);
                    _playedPowerDown = true;
                }
                _activatedTimer -= dt;
        } else {
                _playedPowerUp = false;
                _activated = false;
                setActivatedColor(false);
            }
        }

        super.update(dt);
        faceScreen();
        if (_health <= 0){
            _game._gameOver = true;
        }
    }

    @Override
    public void onHittingBottom() {
        _game.restart();
    }

    private void faceScreen() {
        if (_standingStill){
            if (Math.abs(_velX) >= config.STANDING_STILL_MARGIN){
                loadBitmap("player_left1", (int) _x* Viewport._pixelsPerMeterX, (int) _y*Viewport._pixelsPerMeterY);
                _standingStill = false;
            }
        }
        if(!_standingStill) {
            if (Math.abs(_velX) <= config.STANDING_STILL_MARGIN) {
                loadBitmap("player_front1", (int) _x * Viewport._pixelsPerMeterX, (int) _y * Viewport._pixelsPerMeterY);
                _standingStill = true;
            }
        }
    }

    private void setActivatedColor(final boolean on) {
        if (on){
        _colorMatrix.set(config.ACTIVATED_PLAYER_COLOR_TRANSFORM);
        } else {
        _colorMatrix.set(_resetTransform);
        }
        _colorFilter = new ColorMatrixColorFilter(_colorMatrix);
        _playerPaint.setColorFilter(_colorFilter);
    }

    private void updateFacingDirection(final float controlDirection) {
        if (Math.abs(controlDirection) < config.MIN_INPUT_TO_TURN) {
            return;
        }
        if (controlDirection < 0) {
            _facing = LEFT;
        } else if (controlDirection > 0) {
            _facing = RIGHT;
        }
    }

    @Override
    public void render(final Canvas canvas, final Matrix transform, final Paint paint) {
        transform.preScale(_facing, 1);
        if (_facing == RIGHT) {
            final float offset = _game.worldToScreenX(_width);
            transform.postTranslate(offset, 0);
        }
        transform.preRotate(_facing*_velX*config.PLAYER_MOVE_TILT, (_width/2)* Viewport._pixelsPerMeterX, (_height/2)*Viewport._pixelsPerMeterY);
        super.render(canvas, transform, _playerPaint);
    }

    @Override
    public void onCollision(Entity that) {
        super.onCollision(that);//takes player out of blocks + set _isOnGround and overlap
        if (that._activatesPlayer) {
            _activatedTimer = config.ACTIVATION_TIME;
            _playedPowerDown = false;
            if (!_activated){
                _activated = true;
                setActivatedColor(true);
                _game._jukebox.play(Jukebox.POWER_UP, 0, 1);
            }
        }
        if (!_invincible && that._hurts){
            _health--;
            _invincible = true;
            _invincibleCounter = 0;
            _playerPaint.setAlpha(config.PLAYER_INVINCIBLE_ALPHA);

            _velY = config.HURT_FORCE;
            if (Entity.overlap.x > 0){
                _velX = 1f;
            } else {
                _velX = -1f;
            }
        }
    }
    public void resetValues(){
        _invincible = false;
        _activated = false;
        _health = config.STARTING_HEALTH;
    }
}
