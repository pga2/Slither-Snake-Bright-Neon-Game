package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;

public abstract class Item extends Sprite {

    protected B2WorldCreator creator;
    protected World world;
    protected PlayScreen screen;
    protected Body b2body;
    protected TextureAtlas atlas;
    protected boolean removeBody;
    protected Float scale;
    protected Snake snake;


    public Item(PlayScreen screen, float x, float y, B2WorldCreator creator) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.atlas = screen.getAtlas();
        this.creator = creator;

        setPosition(x, y);
        setBounds(getX(), getY(), 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);
        defineItem();
    }

    public Item(PlayScreen screen, float x, float y, B2WorldCreator creator, float scale) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.atlas = screen.getAtlas();
        this.creator = creator;
        this.scale = scale;

        setPosition(x, y);
        setBounds(getX(), getY(), 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);
        defineItem();
    }

    public Item(PlayScreen screen, float x, float y, B2WorldCreator creator, float scale, Snake snake) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.atlas = screen.getAtlas();
        this.creator = creator;
        this.scale = scale;
        this.snake = snake;

        setPosition(x, y);
        setBounds(getX(), getY(), 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);
        defineItem();
    }

    public  void update(float dt){
        if(removeBody) {
            updateDestroyBody();
        }
    }

    public abstract void defineItem();

    public void destroyBody() {
        removeBody = true;
    }

    public boolean isRemoveBody() {
        return removeBody;
    }

    //removes coin from coins list when coin is being destroyed
    public void updateDestroyBody() {
        world.destroyBody(b2body);
        if(this instanceof Coin) {
            for(int i = 0; i < creator.getCoins().size; i++) {
                Item coin = creator.getCoins().get(i);
                if(coin.equals(this)) {
                    creator.getCoins().removeIndex(i);
                }
            }
        }
    }

    public Body getB2body() {
        return b2body;
    }

    public Snake getSnake() {
        return snake;
    }
}
