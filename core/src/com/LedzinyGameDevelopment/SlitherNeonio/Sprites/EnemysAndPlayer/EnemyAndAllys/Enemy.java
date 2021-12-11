package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.Player;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Coin;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;

import java.util.Random;

public class Enemy extends Snake {

    private final int snakeType;
    private Random random;
    private EnemyMovementLogic enemyMovementLogic;


    private float velocity;

    private int tailTimer;
    private float transparency;
    private boolean boostSpeed;
    private Item coinFromCoinFinder;
    private boolean coinInCoinFinder;
    private boolean outOfBoundsBody;
    private Player playerToKill;
    private boolean changedOnHigherSpeed;

    public Enemy(PlayScreen screen, float x, float y, B2WorldCreator creator) {
        super(screen, x, y, creator);
        random = new Random();

        //choosing snake scale when creating new one
        scale = random.nextFloat() + 0.5f;
        if (scale > 1.40) {
            scale = random.nextFloat() + scale;
        }
        if (scale > 2.30) {
            scale = (random.nextInt(45) / 10f) + scale;
        }

        snakeType = random.nextInt(6);
        velocity = 300;
        setPosition(x, y);
        transparency = 1;
        for (int i = 1; i <= 2; i++) {
            tailArray.add(new EnemyTail(screen, 90, getX() - getWidth() / 2,
                    getY() - getHeight() / 2, snakeBodyTexture, color, this, scale, transparency));
        }
        angle = -135;
        enemyMovementLogic = new EnemyMovementLogic(this, angle, screen, creator);
        tailTimer = 4;
        boostSpeed = false;
        coinInCoinFinder = false;
        changedOnHigherSpeed = false;

    }



    public void update(float dt) {

        //changing scale after eating coin
        setScale(scale, scale);
        if(isChangeScale()) {
            if(!outOfBoundsBody)
                redefineSnake();
            setChangeScale(false);
            if(ropes.size > 0)
                ropes.get(ropes.size - 1).createNewJoints();
        }

        //calculating enemy color
        neonColorChanger.calculateColors();
        color = neonColorChanger.getColor();
        setColor(color.r, color.g, color.b, transparency);

        // increasing snake length when enemy is bigger
        if(!outOfBoundsBody) {
            if (tailArray.size < (30 + 30 * (scale - 0.5f) * 1.8f)) {
                EnemyTail tail = new EnemyTail(screen, enemyMovementLogic.getAngle(), getX(), getY(), snakeBodyTexture, color, this, scale, transparency);
                tail.getB2body().setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
                tail.setScale(scale);
                tailArray.add(tail);
            } else if (tailArray.size > (30 + 30 * (scale - 0.5f) * 1.8f) + 1) {
                world.destroyBody(tailArray.get(0).getB2body());
                tailArray.removeIndex(0);
                if (!outOfBoundsBody)
                    redefineSnake();
                if(ropes.size > 0)
                    ropes.get(ropes.size - 1).createNewJoints();
            }
        }

        // calculating enemy velocity and angle
        enemyMovementLogic.start(velocity);
        b2body.setLinearVelocity(enemyMovementLogic.getVel());
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation((float) enemyMovementLogic.getAngle() - 180);
        if(getRope() != null && ropeB2body != null) {
            ropeB2body.setLinearVelocity(enemyMovementLogic.getVel());
            if (b2body.getPosition().x != ropeB2body.getPosition().x ||
                    b2body.getPosition().y != ropeB2body.getPosition().y)
                ropeB2body.setTransform(b2body.getPosition().x,
                        b2body.getPosition().y, 1);
        }

        //makes enemy b2body change angle
        if(angle != 0) {
            float degreesToRadians = (float) ((Math.PI / 180) * (enemyMovementLogic.getAngle() - 180));
            b2body.setTransform(b2body.getWorldCenter(), degreesToRadians);
        }

        eyes.update(dt, enemyMovementLogic.getAngle(), b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        eyes.setScale(scale);

        //this part makes enemy to not split when increasing speed
        if(previousVelocity < screen.getVelocity() && tailTimer > 1) {
            tailTimer = Math.round((float) tailTimer / 2);
        }
        previousVelocity = screen.getVelocity();

        // makes enemy remove last tail part and create new one to simulate move
        if(!outOfBoundsBody) {
            if (tailTimer <= 1) {
                EnemyTail tail = new EnemyTail(screen, enemyMovementLogic.getAngle(), getX(), getY(), snakeBodyTexture, color, this, scale, transparency);
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
        }

        if(Math.round(scale * 2400 / velocity / (0.003 * 60)) < tailTimer)
            tailTimer = Math.round(scale * 2400 / velocity / (Gdx.graphics.getDeltaTime() * 60));

        if(snakeType == 0 || boostSpeed) {
            increaseVel();
            if(getRope() != null && !changedOnHigherSpeed) {
                if(getRope().getSawBlade() != null) {
                    getRope().getSawBlade().getB2body().applyLinearImpulse(enemyMovementLogic.getVel().x * (float) Math.pow(scale, 2) / 2, enemyMovementLogic.getVel().y * (float) Math.pow(scale, 2) / 2,
                            getRope().getSawBlade().getB2body().getPosition().x, getRope().getB2body().getPosition().y, true);
                } else {
                    getRope().getSawConnectorB2body().applyLinearImpulse(enemyMovementLogic.getVel().x * (float) Math.pow(scale, 2) / 6, enemyMovementLogic.getVel().y * (float) Math.pow(scale, 2) / 6,
                            getRope().getSawConnectorB2body().getPosition().x, getRope().getB2body().getPosition().y, true);
                }
                changedOnHigherSpeed = true;
            }
        } else {
            if(transparency < 1) {
                transparency += 0.025;
            }
            if(transparency > 1) {
                transparency = 1;
            }
            setColor(color.r, color.g, color.b, transparency);
            velocity = 300;
            changedOnHigherSpeed = false;
        }
        for (SnakeTail enemyTail : tailArray) {
            enemyTail.update(dt);
        }

        super.update(dt);
    }


    public void defineSnake() {

        // head body definition
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        snakeWidth = (64 * scale) / BetterSnake.PPM;
        shape.setRadius((64 * scale * 0.73f) / BetterSnake.PPM);
        shape.setPosition(new Vector2(0, (14 * scale) / BetterSnake.PPM));
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.ENEMY_BIT;
        fdef.filter.maskBits = BetterSnake.OBJECT_BIT | BetterSnake.PLAYER_BIT | BetterSnake.ENEMY_BIT | BetterSnake.PLAYER_TAIL_BIT |
                BetterSnake.COIN_BIT | BetterSnake.ENEMY_TAIL_BIT | BetterSnake.SAW_BIT;
        b2body.createFixture(fdef).setUserData(this);

        //tail part that moves every frame
        fdef = new FixtureDef();
        shape = new CircleShape();
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.ENEMY_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // tongue that makes coins move to you
        PolygonShape tongue = new PolygonShape();
        float[] toungeVertices = {-90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale, 90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale,
                -60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale};
        tongue.set(toungeVertices);
        fdef.filter.categoryBits = BetterSnake.TOUNGE_BIT;
        fdef.shape = tongue;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // this b2body checks if there is coin that snake can eat
        PolygonShape coinFinder = new PolygonShape();
        float[] coinFinderVertices = {-580 / BetterSnake.PPM * scale, 800 / BetterSnake.PPM * scale, 580 / BetterSnake.PPM * scale, 800 / BetterSnake.PPM * scale,
                -60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale};
        coinFinder.set(coinFinderVertices);
        fdef.filter.categoryBits = BetterSnake.COIN_FINDER_BIT;
        fdef.shape = coinFinder;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // this checks if there is another snake behind that this one can kill
        PolygonShape killAreaShape = new PolygonShape();
        float[] killAreaShapeVertices = {-400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -40 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, 400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -40 * (scale * 0.5f + 0.25f) / BetterSnake.PPM,
                -400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -900 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, 400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -900 * (scale * 0.5f + 0.25f) / BetterSnake.PPM};
        killAreaShape.set(killAreaShapeVertices);
        fdef.filter.categoryBits = BetterSnake.KILL_AREA_BIT;
        fdef.shape = killAreaShape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void redefineSnake() {
        // head body definition
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
        fdef.filter.categoryBits = BetterSnake.ENEMY_BIT;
        fdef.filter.maskBits = BetterSnake.OBJECT_BIT | BetterSnake.PLAYER_BIT | BetterSnake.ENEMY_BIT | BetterSnake.PLAYER_TAIL_BIT |
                BetterSnake.COIN_BIT | BetterSnake.ENEMY_TAIL_BIT | BetterSnake.SAW_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //tail part that moves every frame
        fdef = new FixtureDef();
        shape = new CircleShape();
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = BetterSnake.ENEMY_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // Tounge that makes coins move to you
        PolygonShape tongue = new PolygonShape();
        float[] toungeVertices = {-90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale, 90 / BetterSnake.PPM * scale, 200 / BetterSnake.PPM * scale,
                -60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale};
        tongue.set(toungeVertices);
        fdef.filter.categoryBits = BetterSnake.TOUNGE_BIT;
        fdef.shape = tongue;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // this b2body checks if there is coin that snake can eat
        PolygonShape coinFinder = new PolygonShape();
        float[] coinFinderVertices = {-580 / BetterSnake.PPM * scale, 800 / BetterSnake.PPM * scale, 580 / BetterSnake.PPM * scale, 800 / BetterSnake.PPM * scale,
                -60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale, 60 / BetterSnake.PPM * scale};
        coinFinder.set(coinFinderVertices);
        fdef.filter.categoryBits = BetterSnake.COIN_FINDER_BIT;
        fdef.shape = coinFinder;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        // this checks if there is another snake behind that this one can kill
        PolygonShape killAreaShape = new PolygonShape();
        float[] killAreaShapeVertices = {-400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, 0 * (scale * 0.5f + 0.25f) / BetterSnake.PPM - (creator.getPlayer().getSnakeWidth() * 0.73f),
                400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, 0 * (scale * 0.5f + 0.25f) / BetterSnake.PPM - (creator.getPlayer().getSnakeWidth() * 0.73f),
        -400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -900 * (scale * 0.5f + 0.25f) / BetterSnake.PPM - (creator.getPlayer().getSnakeWidth() * 0.73f),
                400 * (scale * 0.5f + 0.25f) / BetterSnake.PPM, -900 * (scale * 0.5f + 0.25f) / BetterSnake.PPM - (creator.getPlayer().getSnakeWidth() * 0.73f)};
        killAreaShape.set(killAreaShapeVertices);
        fdef.filter.categoryBits = BetterSnake.KILL_AREA_BIT;
        fdef.shape = killAreaShape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

    }

    //enemy definition when out of world map bounds
    public void outOfBoundsBody(){
        outOfBoundsBody = true;
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        snakeWidth = (64 * scale) / BetterSnake.PPM;
        shape.setRadius((64 * scale * 0.8f) / BetterSnake.PPM);
        fdef.filter.categoryBits = BetterSnake.NOTHING_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    // making enemy speed increase
    public void increaseVel() {
        if (scale > 0.5) {
            velocity = 600;
            scale -= 0.0001f;
            int var = random.nextInt(20);
            Item coin;
            if (var == 0) {
                coin = new Coin(screen, tailArray.get(0).getX(), tailArray.get(0).getY(), creator);
                creator.getCoins().add(coin);
                creator.getCoinsFromSnake().add(coin);
            }
            if(transparency > 0.5) {
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
            velocity = 300;
        }
    }

    public float getVelocity() {
        return velocity;
    }

    public EnemyMovementLogic getEnemyMovementLogic() {
        return enemyMovementLogic;
    }

    public void setBoostSpeed(boolean boostSpeed) {
        this.boostSpeed = boostSpeed;
    }

    public boolean isCoinInCoinFinder() {
        return coinInCoinFinder;
    }

    public void setCoinInCoinFinder(boolean coinInCoinFinder) {
        this.coinInCoinFinder = coinInCoinFinder;
    }

    public void setCoinFromCoinFinder(Item coinFromCoinFinder) {
        this.coinFromCoinFinder = coinFromCoinFinder;
    }

    public Item getCoinFromCoinFinder() {
        return coinFromCoinFinder;
    }

    public boolean isOutOfBoundsBody() {
        return outOfBoundsBody;
    }

    public void setPlayerToKill(Player playerToKill) {
        this.playerToKill = playerToKill;
    }

    public Player getPlayerToKill() {
        return playerToKill;
    }

    public int getSnakeType() {
        return snakeType;
    }
}
