package com.LedzinyGameDevelopment.SlitherNeonio.Screens;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade.SawBlade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Scenes.HUD;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.Enemy;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Coin;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.HighScoresFileReader;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.WorldContactListener;

import java.util.Random;

public class PlayScreen implements Screen {


    private BetterSnake game;



    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private HUD hud;
    private World world;
    private Box2DDebugRenderer b2dr;

    private B2WorldCreator creator;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ShapeRenderer shapeRenderer;
    private TextureAtlas atlas;
    private Random random;
    private Music music;
    private HighScoresFileReader highScoresFileReader;

    private Vector2 mousePosition;
    private boolean notTouchedBefore;
    private float velocity;
    private float oldScreenHeight;
    private float oldScreenWidth;
    private float scalingScreenX;
    private float scalingScreenY;
    private double playerAngle;
    private boolean firstTimeScreenResize;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private int touchTimer;
    private float transparency;
    private boolean playerDead;
    private boolean newHighScore;
    private float newHighScoreTimer;
    private Vector2 vel;
    private boolean changedOnHigherSpeed;

    public PlayScreen(BetterSnake game) {
        atlas = new TextureAtlas("objects_sprite.atlas");
        this.game = game;


        gameCam = new OrthographicCamera();
        gamePort = new ExtendViewport(BetterSnake.V_WIDTH / BetterSnake.PPM, BetterSnake.V_HEIGHT / BetterSnake.PPM,
                BetterSnake.V_WIDTH * 3 / BetterSnake.PPM, BetterSnake.V_HEIGHT * 3 / BetterSnake.PPM, gameCam);
        gameCam.zoom = 1.3f;
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / BetterSnake.PPM);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        mousePosition = new Vector2();
        notTouchedBefore = true;
        shapeRenderer = new ShapeRenderer();
        MapProperties prop = map.getProperties();

        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;

        highScoresFileReader = new HighScoresFileReader();
        creator = new B2WorldCreator(this);
        hud = new HUD(game.batch, this);
        creator.getPlayer().getB2body().setLinearVelocity(2.5f, -2.5f);
        playerAngle = 45;
        //creator.getEnemies().add(new Enemy(this, mapPixelWidth / 2 / BetterSnake.PPM, mapPixelHeight / 2 / BetterSnake.PPM));
        velocity = 300;
        world.setContactListener(new WorldContactListener(hud, creator));
        random = new Random();

        scalingScreenX = 1;
        scalingScreenY = 1;
        touchTimer = 4;
        firstTimeScreenResize = true;
        transparency = 1;

        playerDead = false;
        newHighScore = false;
        newHighScoreTimer = 0;
        changedOnHigherSpeed = false;

        music = BetterSnake.manager.get("audio/music/game_background.ogg");
        music.setLooping(true);
        music.play();

        //rope.getRopeParts().get(1).getB2body().applyLinearImpulse(new Vector2(1, 1), rope.getRopeParts().get(1).getB2body().getPosition(), true);
        //rope.getRopeParts().get(6).getB2body().applyLinearImpulse(new Vector2(-1, 0), rope.getRopeParts().get(6).getB2body().getPosition(), true);

    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        // analog stick features (if touched and moved change snake direction)
        if(!playerDead) {
            if (Gdx.input.isTouched(0) && notTouchedBefore) {
                notTouchedBefore = false;
                mousePosition.x = Gdx.input.getX(0);
                mousePosition.y = Gdx.input.getY(0);
                if(vel == null) {
                    vel = new Vector2(0, 0);
                }
            } else if (Gdx.input.isTouched(0)) {
                if (Gdx.input.getX(0) != mousePosition.x || Gdx.input.getY(0) != mousePosition.y) {

                    // changes distance x and y to angle
                    double newAngle = Math.toDegrees(Math.atan2(Gdx.input.getX(0) - mousePosition.x, Gdx.input.getY(0) - mousePosition.y));
                    float deltaTime = Gdx.graphics.getDeltaTime();

                    //this makes snake to create circle when turning back for example
                    if (Math.abs(playerAngle - newAngle) > deltaTime * 400 / (creator.getPlayer().getScale() * 2)) {
                        float distanceToZero;
                        if ((playerAngle <= 0 && newAngle <= 0) || (playerAngle >= 0 && newAngle >= 0)) {
                            if (newAngle < playerAngle) {
                                playerAngle -= deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            } else {
                                playerAngle += deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            }
                        } else if (playerAngle < 0 && newAngle > 0) {
                            distanceToZero = (float) (Math.abs(playerAngle) + Math.abs(newAngle));
                            if (distanceToZero < 180) {
                                playerAngle += deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            } else {
                                playerAngle -= deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            }
                        } else if (playerAngle > 0 && newAngle < 0) {
                            distanceToZero = (float) (Math.abs(playerAngle) + Math.abs(newAngle));
                            if (distanceToZero < 180) {
                                playerAngle -= deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            } else {
                                playerAngle += deltaTime * 400 / (creator.getPlayer().getScale() * 2);
                            }
                        }
                    } else {
                        playerAngle = newAngle;
                    }
                    if (playerAngle > 180)
                        playerAngle = -180 + (180 - playerAngle);
                    else if (playerAngle < -180)
                        playerAngle = 360 + playerAngle;
                }

                if (playerAngle <= 90 && playerAngle >= -90)
                    vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (float) playerAngle / 90, Gdx.graphics.getDeltaTime() * velocity * (90 - Math.abs((float) playerAngle)) / -90);
                else {
                    int i = playerAngle <= 0 ? 90 : -90;
                    vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) playerAngle) - 180) / i, Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) playerAngle) - 90) / 90);
                }
                if(velocity < 550) {
                    while (true) {
                        if (Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.y, 2)) > velocity * Gdx.graphics.getDeltaTime() * 0.7) {
                            vel = new Vector2(vel.x * 0.98f, vel.y * 0.98f);
                        } else {
                            break;
                        }
                    }
                }
                creator.getPlayer().getB2body().setLinearVelocity(vel.x, vel.y);
                touchTimer = 4;
            } else {
                if (touchTimer == 0) {
                    notTouchedBefore = true;
                    touchTimer = 4;
                } else
                    touchTimer--;

                if (playerAngle <= 90 && playerAngle >= -90)
                    vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (float) playerAngle / 90, Gdx.graphics.getDeltaTime() * velocity * (90 - Math.abs((float) playerAngle)) / -90);
                else {
                    int i = playerAngle <= 0 ? 90 : -90;
                    vel = new Vector2(Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) playerAngle) - 180) / i, Gdx.graphics.getDeltaTime() * velocity * (Math.abs((float) playerAngle) - 90) / 90);
                }
                if(velocity < 550) {
                    while (true) {
                        if (Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.y, 2)) > velocity * Gdx.graphics.getDeltaTime() * 0.7) {
                            vel = new Vector2(vel.x * 0.98f, vel.y * 0.98f);
                        } else {
                            break;
                        }
                    }
                }
                creator.getPlayer().getB2body().setLinearVelocity(vel.x, vel.y);
            }
            Color color = creator.getPlayer().getColor();

            // this makes players movement speed increase
            if (Gdx.input.isTouched(1) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                if (creator.getPlayer().getScale() > 0.5) {
                    velocity = 600;
                    float multiplier = (creator.getPlayer().getScale() > 1) ? 1 / (60 * Gdx.graphics.getDeltaTime()) : 3 / (60 * Gdx.graphics.getDeltaTime());
                    creator.getPlayer().setScale(creator.getPlayer().getScale() - (0.0002f / (creator.getPlayer().getScale() * multiplier)));
                    int var = random.nextInt(20);
                    Item coin;
                    if (var == 0) {
                        coin = new Coin(this, creator.getPlayer().getTailArray().get(0).getX(), creator.getPlayer().getTailArray().get(0).getY(), creator);
                        creator.getCoins().add(coin);
                        creator.getCoinsFromSnake().add(coin);
                    }
                    if (transparency > 0.5) {
                        transparency -= 0.025;
                    }

                    if(!changedOnHigherSpeed) {
                        if(creator.getPlayer().getRope().getSawBlade() != null && creator.getPlayer().getRope().getSawBlade().getB2body() != null) {
                            creator.getPlayer().getRope().getSawBlade().getB2body().applyLinearImpulse(vel.x * (float) Math.pow(creator.getPlayer().getScale(), 2) / 3, vel.y * (float) Math.pow(creator.getPlayer().getScale(), 2) / 3,
                                    creator.getPlayer().getRope().getSawBlade().getB2body().getPosition().x, creator.getPlayer().getRope().getB2body().getPosition().y, true);
                        } else {
                            creator.getPlayer().getRope().getSawConnectorB2body().applyLinearImpulse(vel.x * (float) Math.pow(creator.getPlayer().getScale(), 2) / 6, vel.y * (float) Math.pow(creator.getPlayer().getScale(), 2) / 6,
                                    creator.getPlayer().getRope().getSawConnectorB2body().getPosition().x, creator.getPlayer().getRope().getB2body().getPosition().y, true);
                        }
                        changedOnHigherSpeed = true;
                    }
                    creator.getPlayer().setColor(color.r, color.g, color.b, transparency);
                } else {
                    velocity = 300;
                    if (transparency < 1) {
                        transparency += 0.025;
                    }
                    if (transparency > 1) {
                        transparency = 1;
                    }
                    creator.getPlayer().setColor(color.r, color.g, color.b, transparency);
                }
            } else {
                velocity = 300;
                changedOnHigherSpeed = false;

                if (transparency < 1) {
                    transparency += 0.025;
                }
                if (transparency > 1) {
                    transparency = 1;
                }
                creator.getPlayer().setColor(color.r, color.g, color.b, transparency);
            }
        }
        creator.getPlayer().getRopeB2body().setLinearVelocity(vel.x, vel.y);
        if (creator.getPlayer().getB2body().getPosition().x != creator.getPlayer().getRopeB2body().getPosition().x ||
                creator.getPlayer().getB2body().getPosition().y != creator.getPlayer().getRopeB2body().getPosition().y)
            creator.getPlayer().getRopeB2body().setTransform(creator.getPlayer().getB2body().getPosition().x,
                    creator.getPlayer().getB2body().getPosition().y, 1);

    }


    public void update(float dt) {

        gameCam.zoom = 1.3f + 1.3f * (creator.getPlayer().getScale() - 0.5f) * 0.5f;
        //gameCam.zoom = 8f;
        handleInput(dt);
        world.step(1/60f, 8, 3);
        hud.update(dt);
        creator.getPlayer().update(dt, playerAngle);

        creator.getPlayer().getRope().update(dt);

        creator.update(dt); // with enemy.update() and coin.update()
        for(Item coin : creator.getCoins()) {
            coin.update(dt);
        }

        if(highScoresFileReader.getScoresList().size() > 0) {
            if ((creator.getPlayer().getScale() * 1000) - 500 > highScoresFileReader.getScoresList().get(0) && newHighScoreTimer < 2.5f) {
                if(newHighScoreTimer == 0) {
                    BetterSnake.manager.get("audio/sounds/achievement.ogg", Sound.class).play();
                }
                hud.setNewHighScore(true);
                newHighScoreTimer += Gdx.graphics.getDeltaTime();
            } else {
                hud.setNewHighScore(false);
            }
        }
        gameCam.position.x = creator.getPlayer().getB2body().getPosition().x;
        gameCam.position.y = creator.getPlayer().getB2body().getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
        //Gdx.app.log("    FPS: ", String.valueOf(1 / Gdx.graphics.getDeltaTime()));
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        //drawing analog stick features
        if(!notTouchedBefore) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(mousePosition.x * scalingScreenX, (gamePort.getScreenHeight() - mousePosition.y) * scalingScreenY, 30);
            shapeRenderer.circle(Gdx.input.getX() * scalingScreenX, (gamePort.getScreenHeight() - Gdx.input.getY()) * scalingScreenY, 30);
            shapeRenderer.end();
        }
        //drawing game objects
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        for(Item item : creator.getCoins()) {
            item.draw(game.batch);
        }
        for(SnakeTail playerTail : creator.getPlayer().getTailArray()) {
            playerTail.draw(game.batch);
        }
        creator.getPlayer().draw(game.batch);

        creator.getPlayer().getEyes().draw(game.batch);

        creator.getPlayer().getRope().draw(game.batch);

        for(Snake enemy : creator.getEnemies()) {
            if(!((Enemy) enemy).isOutOfBoundsBody()) {
                for (SnakeTail enemyTail : enemy.getTailArray()) {
                    enemyTail.draw(game.batch);
                }
                enemy.draw(game.batch);
                enemy.getEyes().draw(game.batch);
                if(enemy.getRope() != null)
                    enemy.getRope().draw(game.batch);
            }
        }
        for(SawBlade sawBlade : creator.getSawBlades()) {
            sawBlade.draw(game.batch);
        }

        //Gdx.app.log("FPS: ", String.valueOf(1 / Gdx.graphics.getDeltaTime()));

        game.batch.end();
        game.batch.setProjectionMatrix(hud.getStage().getCamera().combined);
        game.batch.setProjectionMatrix(hud.getBorderStage().getCamera().combined);
        //b2dr.render(creator.getPlayer().getWorld(), gameCam.combined);
        hud.getStage().draw();
        hud.getBorderStage().draw();

        if(playerDead) {
            game.setScreen(new GameOverScreen(game, Math.round((creator.getPlayer().getScale() * 1000) - 500)));
            music.stop();
            dispose();
        }


    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        if(firstTimeScreenResize) {
            oldScreenHeight = gamePort.getScreenHeight();
            oldScreenWidth = gamePort.getScreenWidth();
            firstTimeScreenResize = false;
        } else {
            scalingScreenX = oldScreenWidth / width;
            scalingScreenY = oldScreenHeight / height;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        atlas.dispose();
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        //music.dispose();
    }



    public int getMapPixelWidth() {
        return mapPixelWidth;
    }

    public int getMapPixelHeight() {
        return mapPixelHeight;
    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public float getVelocity() {
        return velocity;
    }

    public double getPlayerAngle() {
        return playerAngle;
    }

    public float getTransparency() {
        return transparency;
    }

    public void setPlayerDead(boolean playerDead) {
        this.playerDead = playerDead;
    }

    public B2WorldCreator getCreator() {
        return creator;
    }


}
