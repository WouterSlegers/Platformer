package com.wjslegers.platformer.Utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

public class Jukebox {
    SoundPool _soundPool = null;
    private static final int MAX_STREAMS = 4;
    private static int _backgroundStreamId = 0;
    public static float POWER_DOWN_LENGTH = 0;

    public static int HURT = 0;
    public static int PICK_UP = 0;
    public static int DENIED = 0;
    public static int POWER_UP = 0;
    public static int POWER_DOWN = 0;
    public static int COIN_DESTROY = 0;

    public static int BACKGROUND_EXCITE = 0;
    public static int BACKGROUND_CHILL = 0;
    public static int BACKGROUND_ODD = 0;

    public Jukebox(final Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(MAX_STREAMS)
                .build();
        loadSounds(context);
    }

    private void loadSounds(final Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("hurt.wav");
            HURT = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("pick_up2.wav");
            PICK_UP = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("denied.wav");
            DENIED = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("power_up2.wav");
            POWER_UP = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("coin_destroy.wav");
            COIN_DESTROY = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("power_down.wav");
            POWER_DOWN = _soundPool.load(descriptor, 1);
            POWER_DOWN_LENGTH = 1.35f; //Sorry for hardcoding, but making a Mediaplayer just to retrieve this seemed silly and was the only solution I found
            descriptor = assetManager.openFd("exciting_loop.wav");
            BACKGROUND_EXCITE = _soundPool.load(descriptor, 3);
            descriptor = assetManager.openFd("chill_loop.wav");
            BACKGROUND_CHILL = _soundPool.load(descriptor, 3);
            descriptor = assetManager.openFd("odd_loop.wav");
            BACKGROUND_ODD = _soundPool.load(descriptor, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void play(final int soundID, final int loop, final int priority) { //-1 to loop, higher priority = higher value
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final float rate = 1.0f; //speed

        if (soundID > 0) {
            if (soundID == BACKGROUND_EXCITE || soundID == BACKGROUND_EXCITE || soundID == BACKGROUND_ODD){
                _soundPool.stop(_backgroundStreamId);
                _backgroundStreamId = _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
            } else {
                _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
            }
        }
    }

    public void destroy() {
        _soundPool.release();
        _soundPool = null;
    }

    public void onResume() {
        _soundPool.autoResume();
    }

    public void onPause() {
        _soundPool.autoPause();
    }

    public void playBackgroundMusic() {
        switch (Utils.RNG.nextInt(3)){
            case 0:
                play(BACKGROUND_EXCITE, -1, 3);
                break;
            case 1:
                play(BACKGROUND_CHILL, -1, 3);
                break;
            case 2:
                play(BACKGROUND_ODD, -1, 3);
                break;
        }
    }
}