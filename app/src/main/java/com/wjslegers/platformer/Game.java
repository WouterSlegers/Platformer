package com.wjslegers.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.wjslegers.platformer.Input.InputManager;
import com.wjslegers.platformer.Levels.Level;
import com.wjslegers.platformer.Levels.LevelManager;

import com.wjslegers.platformer.Entities.Entity;
import com.wjslegers.platformer.Utils.BitmapPool;
import com.wjslegers.platformer.Utils.Jukebox;
import com.wjslegers.platformer.Utils.Utils;

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public static final String TAG = "Game";
    private static final double NANOS_TO_SECONDS = 1.0 / 1_000_000_000; //constant not config
    static int STAGE_WIDTH = config.DEFAULT_STAGE_WIDTH;
    static int STAGE_HEIGHT = config.DEFAULT_STAGE_HEIGHT;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private volatile boolean _musicIsPlaying = false;

    private SurfaceHolder _holder = null;
    private final Paint _paint = new Paint();

    private Canvas _canvas = null;
    private final Matrix _transform = new Matrix();

    private Level _map = null;
    public LevelManager _level = null;
    private InputManager _controls = new InputManager(); //safe to implemenent, has no behaviour
    private Viewport _camera = null;
    public Jukebox _jukebox = null;

    private ArrayList<Entity> _visibleEntities = new ArrayList<>();
    private ArrayList<Entity> _visibleBackgroundEntities = new ArrayList<>();
    public BitmapPool _bitmapPool = null;

    public boolean _gameOver = true;
    public boolean _justOpened = true;

    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        final int TARGET_HEIGHT = STAGE_HEIGHT;
        final int actualHeight = getScreenHeight();
        final float ratio = (TARGET_HEIGHT >= actualHeight) ? 1 : (float) TARGET_HEIGHT / actualHeight;
        STAGE_WIDTH = (int) (ratio * getScreenWidth());
        STAGE_HEIGHT = TARGET_HEIGHT;

        _camera = new Viewport(STAGE_WIDTH, STAGE_HEIGHT, config.METERS_TO_SHOW_X, config.METERS_TO_SHOW_Y);
        Log.d(TAG, _camera.toString());

        Entity._game = this;
        _jukebox = new Jukebox(context);

        _bitmapPool = new BitmapPool(this);

        _map = new Level(context, "level1.txt");
        _level = new LevelManager(_map, _bitmapPool);
        _level._jukebox = _jukebox;

        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);

        Log.d(TAG, String.format("Resolution: %1$d : %2$d", STAGE_WIDTH, STAGE_HEIGHT));
    }

    private void mapReset(){
        _bitmapPool = new BitmapPool(this);
        _level = new LevelManager(_map, _bitmapPool);
        _level._jukebox = _jukebox;
    }

    public InputManager getControls(){
        return _controls;
    }
    public void setControls(final InputManager controls){
        _controls.onPause();
        _controls.onStop();
        _controls = controls;
    }
    public float getWorldHeight(){
        return _level._levelHeight;
    }
    public float getWorldWidth(){
        return _level._levelWidth;
    }
    public int worldToScreenX(final float worldDistance) {
        return (int) (worldDistance * _camera.getPixelsPerMeterX());
    }
    public int worldToScreenY(final float worldDistance) {
        return (int) (worldDistance * _camera.getPixelsPerMeterY());
    }
    public float ScreenToWorldX(final float pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterX());
    }
    public float ScreenToWorldY(final float pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterY());
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        while (_isRunning) {
            final double _deltaTime = (System.nanoTime() - lastFrame) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            update(_deltaTime);
            buildVisibleSet();
            render(_camera);
        }
    }

    private void update(final double dt) {
        if(_gameOver || _justOpened){
            return;
        }
        _camera.lookAt(_level._player);
        _camera.cacheView(); //must be called to refresh viewpoint
        _level.update(dt);
    }

    public void restart(){
        _gameOver = false;
        _level._player.resetValues();
        mapReset();
        _camera.lookAt(_level._player);
        if (!_musicIsPlaying){
            _jukebox.playBackgroundMusic();
            _musicIsPlaying = true;
        }
    }

    private void buildVisibleSet(){
        _visibleEntities.clear();
        _visibleBackgroundEntities.clear();
        _visibleEntities.add(_level._player);
        for (final Entity e : _level._entities){
            if(_camera.inView(e)){
                _visibleEntities.add(e);
            }
        }
        for (final Entity e : _level._backgroundEntities){
            if(_camera.inView(e)){
                _visibleBackgroundEntities.add(e);
            }
        }
    }

    private static final Point _position = new Point();
    private void render(final Viewport camera) {
        if (!acquireAndLockCanvas()) {
            return;
        }
        try {
            _canvas.drawColor(config.BG_COLOR);
            for (final Entity e : _visibleBackgroundEntities) {
                _transform.reset();
                camera.worldToScreen(e, _position);
                _transform.postTranslate(_position.x, _position.y);
                e.render(_canvas, _transform, _paint);
            }
            for (final Entity e : _visibleEntities) {
                _transform.reset();
                camera.worldToScreen(e, _position);
                _transform.postTranslate(_position.x, _position.y);
                e.render(_canvas, _transform, _paint);
            }
            renderHUD(_canvas, _paint);
        } finally {
            _holder.unlockCanvasAndPost(_canvas);
        }
    }

    private void renderHUD(final Canvas canvas, final Paint paint) {
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(config.TEXT_SIZE);
        if(!_gameOver) {
            canvas.drawText(String.format("%1$s %2$d", getContext().getString(R.string.health), _level._player._health), 10, config.TEXT_SIZE, paint);
            canvas.drawText(String.format("%1$s %2$d/%3$d", getContext().getString(R.string.score), _level._player._score, _level._coinsTotal), 10, 2*config.TEXT_SIZE, paint);
        } else {
            if (_justOpened) {
                canvas.drawText(getContext().getString(R.string.instructions1), 10, config.TEXT_SIZE, paint);
                canvas.drawText(getContext().getString(R.string.instructions2), 10, 3*config.TEXT_SIZE, paint);
                canvas.drawText(getContext().getString(R.string.instructions3), 10, 5*config.TEXT_SIZE, paint);
                canvas.drawText(getContext().getString(R.string.instructions4), 10, 7*config.TEXT_SIZE, paint);
                canvas.drawText(getContext().getString(R.string.instructions5), 10, 9*config.TEXT_SIZE, paint);
            } else {
                canvas.drawText(getContext().getString(R.string.gameOver), getScreenWidth()/4, getScreenHeight()/2, paint);
            }
        }
    }

    private boolean acquireAndLockCanvas() {
        if (!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }


    protected void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _controls.onResume();
        _gameThread = new Thread(this);
        _jukebox.onResume();
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        _controls.onPause();
        _jukebox.onPause();
        while (true) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;

        if (_level != null) {
            _level.destroy();
            _level = null;
        }
        if(_jukebox != null){
            _jukebox.destroy();
            _jukebox = null;
        }
        _controls = null;
        Entity._game = null;
        if(_bitmapPool != null){
            _bitmapPool.empty(); //safe but redundant, levelManager empties too
        }
        Entity._game = null;
        _holder.removeCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull final SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(@NonNull final SurfaceHolder surfaceHolder, final int format, final int width, int height) {
        Log.d(TAG, "surfaceChanged");
        Log.d(TAG, String.format("\t Width: %1$s Height: %2$s", width, height));
        if (_gameThread != null && _isRunning) {
            Log.d(TAG, "GameThread started");
            _gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull final SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
    }
}
