package com.wjslegers.platformer.Input;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wjslegers.platformer.R;
import com.wjslegers.platformer.config;

import static com.wjslegers.platformer.Entities.Entity._game;

public class SplitTouchController extends InputManager
        implements View.OnTouchListener {

    boolean _releasedSinceOver = false;

    public SplitTouchController(View view){
        view.findViewById(R.id.screen_left)
                .setOnTouchListener(this);
        view.findViewById(R.id.screen_right)
                .setOnTouchListener(this);
        view.findViewById(R.id.screen_top)
                .setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        int action = event.getActionMasked();
        int id = v.getId();
        if(action == MotionEvent.ACTION_DOWN){
            // User started pressing a key
            if (!_game._gameOver) {
                _releasedSinceOver = false;
            }

            if (id == R.id.screen_left) {
                _horizontalFactor -= 1;
            } else if(id == R.id.screen_right) {
                _horizontalFactor += 1;
            }
            if (id == R.id.screen_top) {
                _isJumping = true;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            // User released a key
            if (_game._gameOver) {
                if (_releasedSinceOver || _game._justOpened) {
                    _game.restart();
                    _game._justOpened = false;
                }
            }
            _releasedSinceOver = true;

            if (id == R.id.screen_left) {
                _horizontalFactor += 1;
            } else if (id == R.id.screen_right) {
                _horizontalFactor -= 1;
            }
            if (id == R.id.screen_top) {
                _isJumping = false;
            }
        }
        return false;
    }
}
