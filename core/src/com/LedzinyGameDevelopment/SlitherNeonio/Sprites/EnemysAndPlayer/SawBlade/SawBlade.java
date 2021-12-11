package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade;

import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class SawBlade extends Sprite {

    public enum State {RUNNING, WAITING, DRAGGINGBYSNAKE, TOUCHEDBYROPE}
    private PlayScreen screen;
    private B2WorldCreator creator;
    private World world;
    private TextureAtlas atlas;
    private boolean removeBody;
    private Body b2body;
    private State state;
    private float stateTimer;
    private Random random;
    private int direction;
    private boolean outOfBoundsBody;
    private float radius;
    private Snake snake;
    private int positionToRopeTimer;
    private float sawScale;

    public SawBlade(PlayScreen screen, float x, float y, B2WorldCreator creator) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.atlas = screen.getAtlas();
        this.creator = creator;

        random = new Random();
        setRegion(atlas.findRegion("saw-blade"), 0, 0, 256, 256);
        setPosition(x, y);
        setBounds(getX(), getY(), 128 / BetterSnake.PPM, 128 / BetterSnake.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);

        sawScale = 3;
        defineSaw();
        state = State.WAITING;
        stateTimer = 0;
        positionToRopeTimer = 30;
    }

    public void update(float dt, float x, float y) {
        if(removeBody) {
            updateDestroyBody();
        } else {
            switch (state) {
                case WAITING:
                    stateTimer += Gdx.graphics.getDeltaTime();
                    b2body.setLinearVelocity(0, 0);
                    setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
                    if (stateTimer > 4) {
                        state = State.RUNNING;
                        direction = random.nextInt(4);
                    } else {
                        setRotation(getRotation() - (stateTimer * 6));
                        //setColor((stateTimer * 31) / 255f, (stateTimer * 13) / 255, (stateTimer * 5) / 255, 1);
                        setColor(1, (255 - stateTimer * 60) / 255, (255 - stateTimer * 60) / 255, 1);
                    }
                    break;
                case RUNNING:
                    stateTimer -= Gdx.graphics.getDeltaTime() * 12;
                    if (stateTimer < 0) {
                        state = State.WAITING;
                    } else {
                        switch (direction) {
                            case 0:
                                b2body.setLinearVelocity(stateTimer * 480 * Gdx.graphics.getDeltaTime(), 0);
                                break;
                            case 1:
                                b2body.setLinearVelocity(-stateTimer * 480 * Gdx.graphics.getDeltaTime(), 0);
                                break;
                            case 2:
                                b2body.setLinearVelocity(0, stateTimer * 480 * Gdx.graphics.getDeltaTime());
                                break;
                            case 3:
                                b2body.setLinearVelocity(0, -stateTimer * 480 * Gdx.graphics.getDeltaTime());
                                break;
                        }
                        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
                        setRotation(getRotation() - stateTimer * 3);
                        setColor(1, (255 - stateTimer * 60) / 255, (255 - stateTimer * 60) / 255, 1);
                    }
                    break;
                case TOUCHEDBYROPE:
                    if(positionToRopeTimer > 3) {
                        setScale(sawScale + ((snake.getScale() * 1.2f - sawScale) * ((31f - positionToRopeTimer) / 30)));
                    } else if(positionToRopeTimer == 3) {
                        sawScale = snake.getScale() * 1.2f;
                        redefineSaw();
                    }
                    if(getSnake().getRope() != null) {
                        setPosition(getX() - ((getX() - snake.getRope().getSawConnectorB2body().getPosition().x - 0.1f) / positionToRopeTimer),
                                getY() - ((getY() - snake.getRope().getSawConnectorB2body().getPosition().y - 0.1f) / positionToRopeTimer));
                        b2body.setTransform(getX() - ((getX() - snake.getRope().getSawConnectorB2body().getPosition().x) / positionToRopeTimer),
                                getY() - ((getY() - snake.getRope().getSawConnectorB2body().getPosition().y) / positionToRopeTimer) + getHeight() / 2, 0);
                        setRotation(getRotation() - (stateTimer * 6));
                        if (stateTimer < 4)
                            stateTimer += Gdx.graphics.getDeltaTime();
                        positionToRopeTimer--;
                        if (positionToRopeTimer == 0) {


                            b2body.setTransform(snake.getRope().getSawConnectorB2body().getPosition().x, snake.getRope().getSawConnectorB2body().getPosition().y, 0);
                            setPosition(snake.getRope().getSawConnectorB2body().getPosition().x - getWidth() / 2,
                                    snake.getRope().getSawConnectorB2body().getPosition().y - getHeight() / 2);
                            positionToRopeTimer = 15;
                            state = State.DRAGGINGBYSNAKE;

                            snake.getRope().addSawBlade(this);
                            snake.getRope().setConnectsaw(true);
                            setColor(1, 15 / 255f, 15 / 255f, 1);
                        }
                    }
                    break;
                case DRAGGINGBYSNAKE:
                    setPosition(b2body.getPosition().x - getWidth() / 2,
                            b2body.getPosition().y - getHeight() / 2);
                    setRotation(getRotation() - (stateTimer * 6));
                    if(stateTimer < 4)
                        stateTimer += Gdx.graphics.getDeltaTime();
                    if(snake.getRope().getSawBlade() != this && snake.getRope().getSawBlades().size > 1) {
                        for(int i = 0; i < snake.getRope().getSawBlades().size; i++) {
                            SawBlade sawBlade = snake.getRope().getSawBlades().get(i);
                            if(sawBlade != this) {
                                snake.getRope().getSawBlades().removeIndex(i);
                                break;
                            }
                        }
                    }
                    if(snake.getRope().getSawBlade() != this) {
                        state = State.WAITING;
                        snake = null;
                    }
                    break;
            }
        }
    }

    private void defineSaw() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        radius = 56.66f * sawScale / BetterSnake.PPM;
        shape.setRadius(radius);
        fdef.filter.categoryBits = BetterSnake.SAW_BIT;
        fdef.filter.maskBits = BetterSnake.ENEMY_TAIL_BIT | BetterSnake.PLAYER_TAIL_BIT | BetterSnake.ROPE_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        setScale(sawScale);
    }

    public void redefineSaw() {
        if(b2body != null)
            world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        radius = 56.66f * sawScale / BetterSnake.PPM;
        shape.setRadius(radius);
        fdef.filter.categoryBits = BetterSnake.SAW_BIT;
        fdef.filter.maskBits = BetterSnake.ENEMY_TAIL_BIT | BetterSnake.PLAYER_TAIL_BIT | BetterSnake.ROPE_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        setScale(sawScale);
    }

    public void updateDestroyBody() {
        for (int i = 0; i < creator.getSawBlades().size; i++) {
            SawBlade sawBlade = creator.getSawBlades().get(i);
            if (sawBlade.equals(this)) {
                b2body.setUserData(null);
                world.destroyBody(b2body);
                creator.getSawBlades().removeIndex(i);
                b2body = null;
                removeBody = true;
                break;
            }
        }

        if(getSnake() != null && getSnake().getRope() != null && getSnake().getRope().getSawBlades().size > 0) {
            getSnake().getRope().getSawBlades().removeIndex(0);
        }
    }

    public boolean isRemoveBody() {
        return removeBody;
    }

    public void setRemoveBody(boolean removeBody) {
        this.removeBody = removeBody;
    }

    public void setOutOfBoundsBody(boolean outOfBoundsBody) {
        this.outOfBoundsBody = outOfBoundsBody;
    }

    public Body getB2body() {
        return b2body;
    }

    public float getRadius() {
        return radius;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public Snake getSnake() {
        return snake;
    }

    public void setSawScale(float sawScale) {
        this.sawScale = sawScale;
    }

    public float getSawScale() {
        return sawScale;
    }
}
