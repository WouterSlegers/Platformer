package com.wjslegers.platformer.Levels;

import android.util.Log;

import com.wjslegers.platformer.Entities.ActivationBlock;
import com.wjslegers.platformer.Entities.Coin;
import com.wjslegers.platformer.Entities.Entity;
import com.wjslegers.platformer.Entities.Player;
import com.wjslegers.platformer.Entities.Spikes;
import com.wjslegers.platformer.Entities.StaticEntity;
import com.wjslegers.platformer.Entities.Sword;
import com.wjslegers.platformer.Utils.BitmapPool;
import com.wjslegers.platformer.Utils.Jukebox;

import java.util.ArrayList;

public class LevelManager {
    public int _levelHeight = 0;
    public int _levelWidth = 0;

    public final ArrayList<Entity> _entities = new ArrayList<>();
    public final ArrayList<Entity> _backgroundEntities = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToAdd = new ArrayList<Entity>();
    private final ArrayList<Entity> _backgroundEntitiesToAdd = new ArrayList<Entity>();
    private final ArrayList<Entity> _entitiesToRemove = new ArrayList<>();
    public Player _player = null;
    private BitmapPool _bitmapPool = null;
    public static Jukebox _jukebox = null; //reference set in Game.java
    public int _coinsTotal = 0;

    public LevelManager(final Level map, final BitmapPool bitmapPool) {
        _bitmapPool = bitmapPool;
        loadMapAssets(map);
    }

    public void update(final double dt) {
        _player.update(dt);
        for (Entity e : _entities) {
            e.update(dt);
        }
        checkCollisions();
        addAndRemoveEntities();
    }

    private void checkCollisions() {
        final int count = _entities.size();
        for(Entity e: _entities){
            if (Entity.isAABBOverlapping(e, _player)) {
                if(!_player._invincible && e._hurts){
                    _jukebox.play(Jukebox.HURT, 0, 1);
                }
                e.onCollision(_player);
                _player.onCollision(e);
            }
        }
        Entity a, b;
        for (int i = 0; i < count - 1; i++) {
            a = _entities.get(i);
            for (int j = i + 1; j < count; j++) {
                b = _entities.get(j);
                if (Entity.isAABBOverlapping(a, b)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }

    private void loadMapAssets(final LevelData map) {
        cleanup();
        _levelHeight = map._height;
        _levelWidth = map._width;

        for (int y = 0; y < _levelHeight; y++) {
            final int[] row = map.getRow(y);
            for (int x = 0; x < row.length; x++) {
                int tileID = row[x];
                if (tileID >= 20) {
                    createEntity(LevelData.BACKGROUND, x, y);
                    tileID -= 20;
                }
                if (tileID == LevelData.NO_TILE) {
                    continue; //will not execute anything beyond this but won't quit for-loop like 'break;' would.
                }
                final String spriteName = map.getSpriteName(tileID);
                createEntity(spriteName, x, y);
            }
        }
    }

    private void createEntity(final String spriteName, final int xpos, final int ypos) {
        Entity e = null;

        if (spriteName.equalsIgnoreCase(LevelData.BACKGROUND)){
            e = new StaticEntity(spriteName, xpos, ypos);
            addEntity(e, false);
        } else if (spriteName.equalsIgnoreCase(LevelData.PLAYER)) {
            e = new Player(spriteName, xpos, ypos);
            if (_player == null) {
                _player = (Player) e;
            } //Does not do addEntity, manually check collision and update!
        } else if (spriteName.equalsIgnoreCase(LevelData.SPIKE)) {
            e = new Spikes(spriteName, xpos, ypos);
            addEntity(e, true);
        } else if (spriteName.equalsIgnoreCase(LevelData.ACTIVATION_BLOCK)) {
            e = new ActivationBlock(spriteName, xpos, ypos);
            addEntity(e, true);
        } else if (spriteName.equalsIgnoreCase(LevelData.COIN)) {
            e = new Coin(spriteName, xpos, ypos);
            addEntity(e, true);
            _coinsTotal += 1;
        } else if (spriteName.equalsIgnoreCase(LevelData.SWORD_HOR)) {
            e = new Sword(spriteName, xpos, ypos, true);
            addEntity(e, true);
        } else if (spriteName.equalsIgnoreCase(LevelData.SWORD_VERT)) {
            e = new Sword(spriteName, xpos, ypos, false);
            addEntity(e, true);
        } else {
            e = new StaticEntity(spriteName, xpos, ypos);
            addEntity(e, true);
        }

    }

    private void addAndRemoveEntities() {
        for (Entity e : _entitiesToRemove) {
            _entities.remove(e);
        }
        for (Entity e : _entitiesToAdd) {
            _entities.add(e);
        }
        for (Entity e : _backgroundEntitiesToAdd){
            _backgroundEntities.add(e);
        }//Ever need to delete background?
        _backgroundEntitiesToAdd.clear();
        _entitiesToRemove.clear();
        _entitiesToAdd.clear();
    }

    public void addEntity(final Entity e, boolean foreground) {
        if (e != null) {
            if (foreground){
                _entitiesToAdd.add(e);
            } else {
                _backgroundEntitiesToAdd.add(e);
            }
        }
    }


    public void removeEntity(final Entity e) {
        if (e != null) {
            _entitiesToRemove.add(e);
        }
    }

    private void cleanup() {
        addAndRemoveEntities();
        for (Entity e : _entities) {
            e.destroy();
        }
        for (Entity e : _backgroundEntities) {
            e.destroy();
        }
        _entities.clear();
        _backgroundEntities.clear();
        _player = null;
        _bitmapPool.empty();
    }

    public void destroy() {
        cleanup();
    }

}
