package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.RopePart;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Coin;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.NeonColorChanger;

public class Snake extends Sprite {

    protected PlayScreen screen;
    protected World world;
    protected Body b2body;
    protected Body ropeB2body;

    protected Array<SnakeTail> tailArray;
    protected Array<Item> coinsArray;
    protected TextureRegion snakeBodyTexture;
    protected TextureRegion snakeHeadTexture;
    protected TextureRegion snakeEyesTexture;
    protected FixtureDef fdef;
    protected Eyes eyes;
    protected NeonColorChanger neonColorChanger;
    protected Array<RopePart> ropes;

    protected double angle;
    protected B2WorldCreator creator;
    protected int speedBoost;
    protected float scale;
    protected boolean changeScale;
    protected boolean removeSnake;
    protected float snakeWidth;
    protected float previousVelocity;
    protected Color color;
    protected Item coin;
    protected int previosTailTimer;

    public Snake(PlayScreen screen, float posX, float posY, B2WorldCreator creator) {
        this.creator = creator;
        this.screen = screen;
        this.world = screen.getWorld();

        setPosition(posX, posY);
        snakeWidth = 32 / BetterSnake.PPM;
        scale = 0.5f;
        coinsArray = new Array<>();
        defineSnake();
        snakeHeadTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 0, 0, 128, 128);
        snakeBodyTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 128, 0, 128, 128);
        snakeEyesTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 256, 0, 128, 128);

        setBounds(0, 0, 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setRegion(snakeHeadTexture);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(scale, scale);

        tailArray = new Array<>();
        ropes = new Array<>();
        angle = 0;
        eyes = new Eyes(snakeEyesTexture);
        neonColorChanger = new NeonColorChanger(0);
        color = neonColorChanger.getColor();
        changeScale = false;
        removeSnake = false;
        speedBoost = 0;
        previousVelocity = 300;

    }

    public void update(float dt) {

        // makes coin move to snake when tongue touches coin
        for(int i = 0; i < coinsArray.size; i++) {
            Item coin = coinsArray.get(i);
            if(!coin.isRemoveBody()) {
                coin.setPosition(coin.getX() - ((coin.getX() - getX()) / 15),
                        coin.getY() - ((coin.getY() - getY()) / 15));
                coin.getB2body().setTransform(coin.getX() - ((coin.getX() - getX()) / 15) + getWidth() / 2,
                        coin.getY() - ((coin.getY() - getY()) / 15) + getHeight() / 2, 0);
            } else {
                coinsArray.removeIndex(i);
            }
        }

    }

    public void removeSnakeUpdate(float dt) {

        // removes snake and creates coins on snake tail position
        if(removeSnake) {
            if(getRope() != null && ropeB2body != null) {
                world.destroyBody(ropes.get(ropes.size - 1).getB2body());
                world.destroyBody(ropes.get(ropes.size - 1).getSawConnectorB2body());
                world.destroyBody(ropeB2body);
            }
            if(getRope() != null && ropes.get(ropes.size - 1).getSawBlade() != null)
                ropes.get(ropes.size - 1).getSawBlade().setRemoveBody(true);
            for(SnakeTail tailPart : tailArray) {
                coin = new Coin(screen, tailPart.getX(), tailPart.getY(), creator, scale);
                creator.getCoins().add(coin);
                creator.getCoinsFromSnake().add(coin);
                world.destroyBody(tailPart.b2body);
            }

            world.destroyBody(b2body);
            for(int i = 0; i < creator.getEnemies().size; i++) {
                Snake enemy = creator.getEnemies().get(i);
                if(enemy.equals(this)) {
                    creator.getEnemies().removeIndex(i);
                }
            }
            if(!(creator.getEnemies().size > (15 + (15 * (creator.getPlayer().getScale() - 0.495f) * 0.5f)) + 1))
                creator.createEnemy();
            removeSnake = false;

        }
    }

    public void defineSnake() {
    }

    public void redefineSnake() {};


    public int getSpeedBoost() {
        return speedBoost;
    }

    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isChangeScale() {
        return changeScale;
    }

    public void setChangeScale(boolean changeScale) {
        this.changeScale = changeScale;
    }

    public void setRemoveSnake(boolean removeSnake) {

        this.removeSnake = removeSnake;
    }

    public float getSnakeWidth() {
        return snakeWidth;
    }

    public Array<Item> getCoinsArray() {
        return coinsArray;
    }

    public Color getColor() {
        return color;
    }

    public Array<SnakeTail> getTailArray() {
        return tailArray;
    }

    public World getWorld() {
        return world;
    }

    public Body getB2body() {
        return b2body;
    }

    public Eyes getEyes() {
        return eyes;
    }


    public Body getRopeB2body() {
        return ropeB2body;
    }

    public RopePart getRope() {
        if(ropes.size > 0) {
            return ropes.get(ropes.size - 1);
        } else {
            return  null;
        }
    }

    public void setRopeB2body(Body ropeB2body) {
        this.ropeB2body = ropeB2body;
    }

    public void addRopes(RopePart rope) {
        ropes.add(rope);
    }

    public void newRopesList() {
        ropes = new Array<>();
    }
}
