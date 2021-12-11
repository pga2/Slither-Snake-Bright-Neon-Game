package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.MenuScreen;

public class MenuEnemyTail extends Sprite {

    private World world;
    private MenuScreen screen;
    private Body b2body;
    private FixtureDef fdef;
    private float scale;

    public MenuEnemyTail(double angle, float posX, float posY, TextureRegion texture, Color color, float scale, MenuScreen screen) {
        this.screen = screen;
        this.scale = scale;
        world = screen.getWorld();
        setColor(color);
        setBounds( posX, posY, 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setRegion(texture);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(scale);
        setRotation((float) angle - 180);
        defineEnemyTail();
    }

    public void update(float dt) {
    }



    public void defineEnemyTail() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(64 * scale / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.ENEMY_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public Body getB2body() {
        return b2body;
    }
}
