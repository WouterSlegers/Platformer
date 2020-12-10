package com.wjslegers.platformer;

import android.graphics.Color;

public abstract class config {
    //general settings
    public static final float DEFAULT_DIMENSION = 1.0f; //in meters
    public static final int BG_COLOR = Color.rgb(135, 206, 235);
    public static final int DEFAULT_STAGE_WIDTH = 1280;
    public static final int DEFAULT_STAGE_HEIGHT = 720;
    public static final float SOUND_INTERVAL = 1f;

    //camera settings
    public static float METERS_TO_SHOW_X = 11f;
    public static float METERS_TO_SHOW_Y = 0f;
    public final static float FOLLOW_PERCENTAGE = 0.26f;//camera
    public static final float TEXT_SIZE = 35f;

    //player movement
    public static final float GRAVITY = 20f;
    public static final float PLAYER_ACC_SPEED = 2.0f;
    public static final float MAX_SPEED_X = 10f;
    public static final float DRAG = 0.7f;
    public static final float JUMP_FORCE = -13f;
    public static final float HURT_FORCE = -(GRAVITY / 4);
    public static final float MIN_INPUT_TO_TURN = 0.05f; //5 percent joystick input won't turn it.
    public static final float STANDING_STILL_MARGIN = 0.012f;
    public static final float PLAYER_MOVE_TILT = 3.8f;

    //coin settings
    public static final float COIN_BOUNCE_SPEED = 10f;
    public static final float COIN_TRAVEL_SPEED = 4.5f;
    public static final float COIN_SCALE_FACTOR = 0.3f;

    //sword settings
    public static final float SWORD_TRAVEL_SPEED = 3.0f;
    public static final float SWORD_OUT_OF_BOUND = 5.0f;

    //other player settings
    public static final float PLAYER_SCALE_FACTOR = 0.5f;
    public static final int STARTING_HEALTH = 5;
    public static final double INVINCIBLE_TIME = 1;
    public static final double ACTIVATION_TIME = 4;
    public static final int PLAYER_INVINCIBLE_ALPHA = 140;
    public static final float[] ACTIVATED_PLAYER_COLOR_TRANSFORM = {
            1.1f, 0, 0, 0, 0,
            0, 0.8f, 0, 0, 0,
            0, 0, 1.1f, 0, 0,
            0, 0, 0, 1f, 0};
}
