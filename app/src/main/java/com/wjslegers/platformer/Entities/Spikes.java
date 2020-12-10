package com.wjslegers.platformer.Entities;

public class Spikes extends StaticEntity {
    public Spikes(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _hurts = true;
    }
}
