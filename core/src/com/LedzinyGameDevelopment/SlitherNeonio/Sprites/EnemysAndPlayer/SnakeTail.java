package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.Enemy;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.Player;

public class SnakeTail extends Sprite {

    protected final Color color;
    protected final Color black = new Color(10/ 255, 10 / 255, 8 / 255, 1);
    protected Body b2body;
    protected World world;
    protected PlayScreen screen;

    protected FixtureDef fdef;

    protected double angle;
    protected float posX;
    protected float posY;
    protected float scale;
    protected Snake snake;
    protected float transparency;
    protected boolean snakeTouchesHisTail;
    protected boolean redefineTail;

    public SnakeTail(PlayScreen screen, double angle, float posX, float posY, TextureRegion texture, Color color, Snake snake, float scale, float transparency) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.angle = angle;
        this.posX = posX;
        this.posY = posY;
        this.scale = scale;
        this.color = color;
        this.snake = snake;
        this.transparency = transparency;
        setColor(color);
        defineTail();
        setBounds( posX, posY, 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setRegion(texture);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(scale);
        setRotation((float) angle - 180);
        snakeTouchesHisTail = false;
        redefineTail = false;

    }

    public void update(float dt) {
        setRotation((float) angle - 180);

        // setting transparency when snake increase speed
        if (snake instanceof Player) {
            if (screen.getVelocity() >= 600) {
                if(transparency > 0.3) {
                    transparency -= 0.025;
                }
                setColor(color.r, color.g, color.b, transparency);
            } else {
                if(transparency < 1) {
                    transparency += 0.025;
                }
                if(transparency > 1) {
                    transparency = 1;
                }
                setColor(color.r, color.g, color.b, transparency);
            }
        }
        if(snake instanceof Enemy) {
            if(((Enemy) snake).getVelocity() >= 600) {
                if(transparency > 0.3) {
                    transparency -= 0.025;
                }
                setColor(color.r, color.g, color.b, transparency);
            } else {
                if(transparency < 1) {
                    transparency += 0.025;
                }
                if(transparency > 1) {
                    transparency = 1;
                }
                setColor(color.r, color.g, color.b, transparency);
            }
        }

    }

    public void defineTail() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.shape = shape;
    }


    public Body getB2body() {
        return b2body;
    }

    public Snake getSnake() {
        return snake;
    }

    public void setSnakeTouchesHisTail(boolean snakeTouchesHisTail) {
        this.snakeTouchesHisTail = snakeTouchesHisTail;
    }

    public boolean isSnakeTouchesHisTail() {
        return snakeTouchesHisTail;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public float getScale() {
        return scale;
    }

}
