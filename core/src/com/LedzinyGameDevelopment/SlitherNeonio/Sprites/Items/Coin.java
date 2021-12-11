package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;

import java.util.Random;

public class Coin extends Item{

    private TextureRegion coinTexture;
    private Random random;

    public Coin(PlayScreen screen, float x, float y, B2WorldCreator creator) {
        super(screen, x, y, creator);

        setRegion(atlas.findRegion("coins"), random.nextInt(4) * 128, 0, 128, 128);
        setRotation(random.nextInt(360) - 180);
    }

    public Coin(PlayScreen screen, float x, float y, B2WorldCreator creator, float scale) {
        super(screen, x, y, creator, scale);

        setRegion(atlas.findRegion("coins"), random.nextInt(4) * 128, 0, 128, 128);
        setRotation(random.nextInt(360) - 180);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void defineItem() {
        random = new Random();
        if(scale == null)
            scale = random.nextFloat() + 0.5f;
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(30 * scale / BetterSnake.PPM);
        fdef.filter.categoryBits = BetterSnake.COIN_BIT;
        fdef.isSensor = true;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        setScale(scale, scale);
    }

    public float getScale() {
        return scale;
    }
}
