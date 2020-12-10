package com.wjslegers.platformer.Entities;

public class ActivationBlock extends StaticEntity {
    public ActivationBlock(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _activatesPlayer = true;
    }
}
