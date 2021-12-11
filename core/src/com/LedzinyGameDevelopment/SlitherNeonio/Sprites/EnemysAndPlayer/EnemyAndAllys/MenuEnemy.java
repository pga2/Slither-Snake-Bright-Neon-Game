package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.MenuScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Eyes;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.NeonColorChanger;

import java.util.Random;

public class MenuEnemy extends Sprite {

    private Eyes eyes;
    private World world;
    private double angle;
    private Color color;
    private NeonColorChanger neonColorChanger;
    private TextureRegion snakeBodyTexture;
    private TextureRegion snakeHeadTexture;
    private TextureRegion snakeEyesTexture;

    private MenuScreen screen;
    private Random random;
    private double newAngle;


    private float velocity;

    private int tailTimer;
    private float transparency;
    private float scale;
    private Array<MenuEnemyTail> tailArray;
    private Body b2body;
    private FixtureDef fdef;
    private Vector2 vel;
    private int timer;
    private int timerMapMiddle;
    private int previosTailTimer;

    public MenuEnemy(float x, float y, MenuScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        random = new Random();
        snakeHeadTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 0, 0, 128, 128);
        snakeBodyTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 128, 0, 128, 128);
        snakeEyesTexture = new TextureRegion(screen.getAtlas().findRegion("transparent_champions"), 256, 0, 128, 128);
        scale = 1f;
        setBounds(0, 0, 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setRegion(snakeHeadTexture);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(scale);
        eyes = new Eyes(snakeEyesTexture);
        setPosition(x, y);
        defineSnake();
        tailArray = new Array<>();
        neonColorChanger = new NeonColorChanger(0);
        color = neonColorChanger.getColor();
        for (int i = 1; i <= 30; i++) {
            tailArray.add(new MenuEnemyTail(90, getX() - getWidth() / 2,
                    getY() - getHeight() / 2, snakeBodyTexture, color, scale, screen));
        }

        angle = -135;
        tailTimer = 4;
        timer = random.nextInt(10);
        timerMapMiddle = 10;
        velocity = 450;

    }


    public void update(float dt) {

        // makes snake calculate his color
        neonColorChanger.calculateColors();
        color = neonColorChanger.getColor();
        setColor(color.r, color.g, color.b, 1);

        // calculating menu snake movement logic
        enemyMovementLogic();
        b2body.setLinearVelocity(vel);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation((float) angle - 180);
        eyes.update(dt, angle, b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        eyes.setScale(scale);

        // removing last part of tail and creating new one to simulate move
        if (tailTimer <= 1) {
            MenuEnemyTail tail = new MenuEnemyTail(angle, getX(), getY(), snakeBodyTexture, color, scale, screen);
            tail.getB2body().setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
            tail.setScale(scale);
            tailArray.add(tail);
            world.destroyBody(tailArray.get(0).getB2body());
            tailArray.removeIndex(0);
            tailTimer = Math.round(scale * 2400 / velocity / (Gdx.graphics.getDeltaTime() * 60));

            //checks if distance difference between first part of tail and snake is to big, if difference is to big, adds smaller amount to tail timer
            if(tailTimer > previosTailTimer + 1) {
                tailTimer = previosTailTimer + 1;
            }
            previosTailTimer = Math.round(scale * 2400 / velocity / (Gdx.graphics.getDeltaTime() * 60));
        } else {
            tailArray.get(0).setPosition(tailArray.get(0).getX() - ((tailArray.get(0).getX() - tailArray.get(1).getX()) / tailTimer),
                    tailArray.get(0).getY() - ((tailArray.get(0).getY() - tailArray.get(1).getY()) / tailTimer));
            tailArray.get(0).getB2body().setTransform(tailArray.get(0).getX() - ((tailArray.get(0).getX() - tailArray.get(1).getX()) / tailTimer) + getWidth() / 2,
                    tailArray.get(0).getY() - ((tailArray.get(0).getY() - tailArray.get(1).getY()) / tailTimer) + getHeight() / 2, 0);

            tailTimer--;
        }

        if(Math.round(scale * 2400 / velocity / (0.003 * 60)) < tailTimer)
            tailTimer = Math.round(scale * 2400 / velocity / (Gdx.graphics.getDeltaTime() * 60));

        for (MenuEnemyTail menuEnemyTail : tailArray) {
            menuEnemyTail.update(dt);
        }
    }

    public void defineSnake() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius((64 * scale) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.ENEMY_BIT;
        fdef.filter.maskBits = BetterSnake.OBJECT_BIT | BetterSnake.PLAYER_BIT | BetterSnake.ENEMY_BIT | BetterSnake.PLAYER_TAIL_BIT |
                BetterSnake.COIN_BIT | BetterSnake.ENEMY_TAIL_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void enemyMovementLogic() {
        //makes snake to stay in the users view
        if (getX() * BetterSnake.PPM > 2240) {
            if (angle < 90)
                newAngle = -(random.nextInt(16) + 75);
            else
                newAngle = -(random.nextInt(16) + 90);
        } else if (getX() * BetterSnake.PPM < 832) {
            if (angle < -90)
                newAngle = random.nextInt(16) + 75;
            else
                newAngle = random.nextInt(16) + 90;
        } else if (getY() * BetterSnake.PPM > 952) {
            if (angle < 0)
                newAngle = -(random.nextInt(16));
            else
                newAngle = random.nextInt(16);
        } else if (getY() * BetterSnake.PPM < 584) {
            if (angle >= 0)
                newAngle = random.nextInt(15) + 165;
            else
                newAngle = random.nextInt(16) - 179;
        } else {
            if(timerMapMiddle == 0) {
                newAngle = random.nextInt(360) - 180;
                timerMapMiddle = random.nextInt(15);
            } else {
                timerMapMiddle--;
            }
        }
        float deltaTime = Gdx.graphics.getDeltaTime();

        // makes snake making circle when turning back
        if(Math.abs(angle - newAngle) > deltaTime * 400 / (scale * 2)) {
            float distanceToZero;
            if ((angle <= 0 && newAngle <= 0) || (angle >= 0 && newAngle >= 0)) {
                if (newAngle < angle) {
                    angle -= deltaTime * 400 / (scale * 2);
                } else {
                    angle += deltaTime * 400 / (scale * 2);
                }
            } else if (angle < 0 && newAngle > 0) {
                distanceToZero = (float) (Math.abs(angle) + Math.abs(newAngle));
                if (distanceToZero < 180) {
                    angle += deltaTime * 400 / (scale * 2);
                } else {
                    angle -= deltaTime * 400 / (scale * 2);
                }
            } else if (angle > 0 && newAngle < 0) {
                distanceToZero = (float) (Math.abs(angle) + Math.abs(newAngle));
                if (distanceToZero < 180) {
                    angle -= deltaTime * 400 / (scale * 2);
                } else {
                    angle += deltaTime * 400 / (scale * 2);
                }
            }
        } else {
            angle = newAngle;
        }
        if(angle > 180)
            angle = -180 + (180 - angle);
        else if (angle < -180)
            angle = 360 + angle;
        // transforms angle to velocity
        if (angle < 90 && angle > -90) {
            vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (float) angle / 90, Gdx.graphics.getDeltaTime() * velocity * (90 - Math.abs((float) angle)) / -90);
        } else {
            int i = angle <= 0 ? 90 : -90;
            vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) angle) - 180) / i, Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) angle) - 90) / 90);
        }

        while (true) {
            if (Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.y, 2)) > velocity * Gdx.graphics.getDeltaTime() * 0.7) {
                vel = new Vector2(vel.x * 0.98f, vel.y * 0.98f);
            } else {
                break;
            }
        }

    }

    public Array<MenuEnemyTail> getTailArray() {
        return tailArray;
    }

    public Eyes getEyes() {
        return eyes;
    }

}