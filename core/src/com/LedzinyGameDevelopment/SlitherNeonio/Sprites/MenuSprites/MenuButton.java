package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.MenuSprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.MenuScreen;

public class MenuButton extends Sprite {

    private final TextureAtlas atlas;
    private final int button;
    private float scale;
    private MenuScreen screen;
    private World world;
    private Body body;
    private float width, height;
    float posX, posY;
    private float animationSpeed;
    private Body b2body;
    private FixtureDef fdef;

    public MenuButton(float posX, float posY, float width, float height, float scale, int button, MenuScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.atlas = screen.getAtlas();
        this.posX = posX;
        this.posY = posY;
        this.width = width / BetterSnake.PPM;
        this.height = height / BetterSnake.PPM;
        this.scale = scale;
        this.button = button;
        setBounds(0, 0, this.width, this.height);
        setRegion(atlas.findRegion("menu"), 0, 128 * button, 256, 128);
        setOrigin(getWidth() / 2, getHeight() / 2);

        setScale(2);
        animationSpeed = 0;
        setPosition(posX, posY);
        define();
    }

    public void update(float dt) {

        // starts animation after clicking
        if(screen.getAnimationState() != screen.getCurrentState()) {
            animation();
        }
    }

    // checking if mouse position equals button position
    public boolean mouseOver(Vector2 mousePosition) {
        if(mousePosition.x > posX - width / 2 * scale + width / 2 && mousePosition.x < posX + width / 2 * scale + width / 2
                && mousePosition.y > posY - height / 2 * scale + height / 2 && mousePosition.y < posY + height / 2 * scale + height / 2)
            return true;
        else
            return false;
    }

    //makes button moves after clicking
    public void animation() {
        if(button == 0 || button == 1) {
            b2body.applyLinearImpulse(new Vector2(90 * Gdx.graphics.getDeltaTime(), 0), b2body.getPosition(), true);
            setPosition(b2body.getPosition().x, b2body.getPosition().y);
        } else if(button == 2) {
            b2body.applyLinearImpulse(new Vector2(-90 * Gdx.graphics.getDeltaTime(), 0), b2body.getPosition(), true);
            setPosition(b2body.getPosition().x, b2body.getPosition().y);
        }
    }

    public void define() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(posX, posY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius((12) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.NOTHING_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public int getButton() {
        return button;
    }
}
