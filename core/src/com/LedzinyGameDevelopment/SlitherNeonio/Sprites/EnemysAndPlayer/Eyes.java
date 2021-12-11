package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;

public class Eyes extends Sprite {
    public Eyes(TextureRegion eyesTexture) {


        setBounds(0, 0, 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setRegion(eyesTexture);
        setOrigin(getWidth() / 2, getHeight() / 2);

    }

    public void update(float dt, double angle, float x, float y) {
        setPosition(x, y);
        setRotation((float) angle - 180);
    }
}
