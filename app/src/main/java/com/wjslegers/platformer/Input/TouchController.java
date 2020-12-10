package com.wjslegers.platformer.Input;

import android.view.MotionEvent;
import android.view.View;

import com.wjslegers.platformer.R;

import static com.wjslegers.platformer.Entities.Entity._game;

public class TouchController extends InputManager
        implements View.OnTouchListener {

    boolean _releasedSinceOver = false;

    public TouchController(View view){
        view.findViewById(R.id.keypad_left)
                .setOnTouchListener(this);
        view.findViewById(R.id.keypad_right)
                .setOnTouchListener(this);
        view.findViewById(R.id.keypad_jump)
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

            if (id == R.id.keypad_left) {
                _horizontalFactor -= 1;
            } else if(id == R.id.keypad_right) {
                _horizontalFactor += 1;
            }
            if (id == R.id.keypad_jump) {
                _isJumping = true;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            // User released a key
            if (_game._gameOver) {
                if (_releasedSinceOver) {
                    _game.restart();
                }
            }
            _releasedSinceOver = true;

            if (id == R.id.keypad_left) {
                _horizontalFactor += 1;
            } else if (id == R.id.keypad_right) {
                _horizontalFactor -= 1;
            }
            if (id == R.id.keypad_jump) {
                _isJumping = false;
            }
        }
        return false;
    }
}
