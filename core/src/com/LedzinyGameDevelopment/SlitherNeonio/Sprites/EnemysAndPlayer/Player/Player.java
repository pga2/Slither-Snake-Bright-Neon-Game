package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.RopePart;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;

public class Player extends Snake {

    private Body b2body;
    private int tailTimer;

    public Player(PlayScreen screen, float x, float y, B2WorldCreator creator) {
        super(screen, x, y, creator);
        setPosition(x, y);
        for(int i = 1; i <= 2; i++) {
            tailArray.add(new PlayerTail(screen, 90, this.getX() - this.getHeight() / 2f, this.getY() - this.getHeight() / 2f,
                    snakeBodyTexture, color, this, scale, 1));
        }
        tailTimer = 4;

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth() / 2, getY() + getWidth() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / BetterSnake.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = BetterSnake.NOTHING_BIT;
        ropeB2body = world.createBody(bdef);
        ropeB2body.createFixture(fdef).setUserData(this);

        ropes.add(new RopePart(screen, x, y, creator, scale, this));

    }



    public void update(float dt, double angle) {

        // changes snake scale after eating coin
        setScale(scale, scale);
        if(isChangeScale()) {
            BetterSnake.manager.get("audio/sounds/coin.ogg", Sound.class).play();
            redefineSnake();
            ropes.get(ropes.size - 1).createNewJoints();
            setChangeScale(false);
        }

        // increasing snake length when scale up and decreasing when scale down
        if(tailArray.size < (30 + 30 * (scale - 0.5f) * 1.8f)) {
            PlayerTail tail = new PlayerTail(screen, angle, getX(), getY(), snakeBodyTexture, neonColorChanger.getColor(), this, scale, screen.getTransparency());
            tail.getB2body().setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
            tail.setScale(scale);
            tailArray.add(tail);
        } else if(tailArray.size > (30 + 30 * (scale - 0.5f) * 1.8f) + 1) {
            world.destroyBody(tailArray.get(0).getB2body());
            tailArray.removeIndex(0);
            redefineSnake();
            ropes.get(ropes.size - 1).createNewJoints();
        }

        // calculate snake color
        neonColorChanger.calculateColors();
        color  = neonColorChanger.getColor();

        //set rotation to snake and snake b2body definiton
        setRotation((float) angle - 180);
        if(angle != 0) {
            float degreesToRadians = (float) ((Math.PI / 180) * (angle - 180));
            b2body.setTransform(b2body.getWorldCenter(), degreesToRadians);
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        eyes.update(dt, angle, b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        eyes.setScale(scale);

        //makes snake to not split when increasing speed
        if(previousVelocity < screen.getVelocity() && tailTimer > 1) {
            tailTimer = Math.round((float) tailTimer / 2);
        }
        previousVelocity = screen.getVelocity();

        // creates new part of snake tail and removing last part of tail to simulate move
        if (tailTimer <= 1) {
            PlayerTail tail = new PlayerTail(screen, angle, getX(), getY(), snakeBodyTexture, neonColorChanger.getColor(), this, scale, screen.getTransparency());
            tail.getB2body().setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
            tail.setScale(scale);
            tailArray.add(tail);
            world.destroyBody(tailArray.get(0).getB2body());
            tailArray.removeIndex(0);
            tailTimer = Math.round(scale * 2400 / screen.getVelocity() / (Gdx.graphics.getDeltaTime() * 60));

            //checks if distance difference between first part of tail and snake is to big, if difference is to big, adds smaller amount to tail timer
            if(tailTimer > previosTailTimer + 1) {
                tailTimer = previosTailTimer + 1;
            }
            previosTailTimer = Math.round(scale * 2400 / screen.getVelocity() / (Gdx.graphics.getDeltaTime() * 60));
        } else {
            tailArray.get(0).setPosition(tailArray.get(0).getX() - ((tailArray.get(0).getX() - tailArray.get(1).getX()) / tailTimer),
                    tailArray.get(0).getY() - ((tailArray.get(0).getY() - tailArray.get(1).getY()) / tailTimer));
            tailArray.get(0).getB2body().setTransform(tailArray.get(0).getX() - ((tailArray.get(0).getX() - tailArray.get(1).getX()) / tailTimer) + getWidth() / 2,
                    tailArray.get(0).getY() - ((tailArray.get(0).getY() - tailArray.get(1).getY()) / tailTimer) + getHeight() / 2, 0);
            tailTimer--;
        }

        if(Math.round(scale * 2400 / screen.getVelocity() / (0.003 * 60)) < tailTimer)
            tailTimer = Math.round(scale * 2400 / screen.getVelocity() / (Gdx.graphics.getDeltaTime() * 60));

        for(SnakeTail playerTail : tailArray) {
            playerTail.update(dt);
        }

        super.update(dt);
    }


    public void defineSnake() {

        //snake head definition
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        snakeWidth = (64 * scale) / BetterSnake.PPM;
        shape.setRadius((64 * scale * 0.73f) / BetterSnake.PPM);
        shape.setPosition(new Vector2(0, (14 * scale) / BetterSnake.PPM));
        fdef.filter.categoryBits = BetterSnake.PLAYER_BIT;
        fdef.filter.maskBits = BetterSnake.OBJECT_BIT | BetterSnake.ENEMY_BIT | BetterSnake.ENEMY_TAIL_BIT |
                BetterSnake.COIN_BIT | BetterSnake.PLAYER_TAIL_BIT | BetterSnake.WORLD_BORDER_BIT | BetterSnake.KILL_AREA_BIT |
                BetterSnake.SAW_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // snake tail body part definition that moves with snake
        fdef = new FixtureDef();
        shape = new CircleShape();
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.PLAYER_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // snake tongue definition that makes coin move to snake
        PolygonShape tongue = new PolygonShape();
        float[] toungeVertices = {-90f / BetterSnake.PPM * scale, 200f / BetterSnake.PPM * scale, 90f / BetterSnake.PPM * scale, 200f / BetterSnake.PPM * scale,
                -60f / BetterSnake.PPM * scale, 60f / BetterSnake.PPM * scale, 60f / BetterSnake.PPM * scale, 60f / BetterSnake.PPM * scale};
        tongue.set(toungeVertices);
        fdef.filter.categoryBits = BetterSnake.TOUNGE_BIT;
        fdef.shape = tongue;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void redefineSnake() {

        //snake head definition
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        snakeWidth = (64 * scale) / BetterSnake.PPM;
        shape.setRadius((64 * scale * 0.73f) / BetterSnake.PPM);
        shape.setPosition(new Vector2(0, (14 * scale) / BetterSnake.PPM));
        fdef.filter.categoryBits = BetterSnake.PLAYER_BIT;
        fdef.filter.maskBits = BetterSnake.OBJECT_BIT | BetterSnake.ENEMY_BIT | BetterSnake.ENEMY_TAIL_BIT |
                BetterSnake.COIN_BIT | BetterSnake.PLAYER_TAIL_BIT | BetterSnake.WORLD_BORDER_BIT | BetterSnake.KILL_AREA_BIT |
                BetterSnake.SAW_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // snake tail body part definition that moves with snake
        fdef = new FixtureDef();
        shape = new CircleShape();
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.PLAYER_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // snake tongue definition that makes coin move to snake
        PolygonShape tongue = new PolygonShape();
        float[] toungeVertices = {-90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale, 90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale,
                -60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale};
        tongue.set(toungeVertices);
        fdef.filter.categoryBits = BetterSnake.TOUNGE_BIT;
        fdef.shape = tongue;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

    }



    public World getWorld() {
        return world;
    }

    public Body getB2body() {
        return b2body;
    }

    public void setPlayerDead(boolean playerDead) {
        screen.setPlayerDead(playerDead);
    }



}
