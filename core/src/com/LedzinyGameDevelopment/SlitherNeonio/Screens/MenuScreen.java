package com.LedzinyGameDevelopment.SlitherNeonio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Scenes.MenuHighScores;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.MenuEnemy;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.MenuEnemyTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.MenuSprites.MenuButton;

public class MenuScreen implements Screen {

    public enum State {MENU, HIGHSCORES, NOSTATE, NEWGAME}
    private final TextureAtlas atlas;
    private MenuHighScores menuHighScores;
    private MenuEnemy menuEnemy;
    private World world;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader mapLoader;
    private ExtendViewport gamePort;
    private OrthographicCamera gameCam;
    private BetterSnake game;
    private final Music music;

    private Array<MenuButton> buttons;
    private Vector2 mouseVector;
    private boolean changeScreen;
    private State currentState;
    private State previosState;
    private State animationState;
    private float animationTimer;

    public MenuScreen (BetterSnake game) {
        this.game = game;
        atlas = new TextureAtlas("objects_sprite.atlas");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new ExtendViewport(BetterSnake.V_WIDTH / BetterSnake.PPM, BetterSnake.V_HEIGHT / BetterSnake.PPM,
                BetterSnake.V_WIDTH * 3 / BetterSnake.PPM, BetterSnake.V_HEIGHT * 3 / BetterSnake.PPM, gameCam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("menuMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,  1 / BetterSnake.PPM);

        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);

        gameCam.position.x = mapWidth * tilePixelWidth / 2f / BetterSnake.PPM;
        gameCam.position.y = mapHeight * tilePixelHeight / 2f / BetterSnake.PPM;
        world = new World(new Vector2(0, 0), true);
        mouseVector = new Vector2();
        menuEnemy = new MenuEnemy(1536 / BetterSnake.PPM, 768 / BetterSnake.PPM, this);
        buttons = new Array<>();
        changeScreen = false;
        menuHighScores = new MenuHighScores(game);
        currentState = State.MENU;
        previosState = State.NOSTATE;
        animationTimer = 0;
        animationState = State.MENU;

        music = BetterSnake.manager.get("audio/music/game_menu.ogg");
        music.setLooping(true);
        music.play();

    }


    public void show() {

    }

    private void inputHandler(float dt) {
        if(Gdx.input.isTouched()) {
            mouseVector = gamePort.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            for(int i = 0; i < buttons.size; i++) {
                MenuButton button = buttons.get(i);
                if(button.mouseOver(mouseVector)) {
                    if(button.getButton() == 0) {
                        currentState = State.NEWGAME;
                    } else if(button.getButton() == 1) {
                        currentState = State.HIGHSCORES;
                    } else if(button.getButton() == 2) {
                        currentState = State.MENU;
                    }
                }
            }
        }
    }

    private void update(float dt) {
        inputHandler(dt);
        world.step(1/60f, 8, 3);
        menuEnemy.update(dt);
        for(MenuEnemyTail menuEnemyTail : menuEnemy.getTailArray()) {
            menuEnemyTail.update(dt);
        }

        for(MenuButton button : buttons) {
            button.update(dt);
        }
        if(currentState == State.HIGHSCORES) {
            if(animationState != State.HIGHSCORES) {
                if(animationTimer == 0) {
                    BetterSnake.manager.get("audio/sounds/click.ogg", Sound.class).play();
                }
                animationTimer += dt;
                if(animationTimer > 0.6) {
                    animationTimer = 0;
                    animationState = State.HIGHSCORES;
                }
            } else if(previosState != State.HIGHSCORES) {
                buttons.clear();
                buttons.add(new MenuButton((1536 - 128) / BetterSnake.PPM, (768 - 400) / BetterSnake.PPM, 256, 128, 2, 2, this)); // back button
                previosState = State.HIGHSCORES;
            }

        } else if(currentState == State.MENU) {
            if(animationState != State.MENU) {
                if(animationTimer == 0) {
                    BetterSnake.manager.get("audio/sounds/click.ogg", Sound.class).play();
                }
                animationTimer += dt;
                if(animationTimer > 0.6) {
                    animationTimer = 0;
                    animationState = State.MENU;
                }
            } else if(previosState != State.MENU) {
                buttons.clear();
                buttons.add(new MenuButton((1536 - 128) / BetterSnake.PPM, (768 + 160) / BetterSnake.PPM, 256, 128, 2, 0, this)); // play button
                buttons.add(new MenuButton((1536 - 128) / BetterSnake.PPM, (768 - 160) / BetterSnake.PPM, 256, 128, 2, 1, this)); // high scores button
                previosState = State.MENU;
            }
        } else if(currentState == State.NEWGAME) {
            if(animationState != State.NEWGAME) {
                if(animationTimer == 0) {
                    BetterSnake.manager.get("audio/sounds/click.ogg", Sound.class).play();
                }
                animationTimer += dt;
                if(animationTimer > 0.6) {
                    animationTimer = 0;
                    animationState = State.NEWGAME;
                }
            } else {
                changeScreen = true;
            }
        }

        gameCam.update();
        renderer.setView(gameCam);
    }

    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        for(MenuEnemyTail menuEnemyTail : menuEnemy.getTailArray()) {
            menuEnemyTail.draw(game.batch);
        }
        menuEnemy.draw(game.batch);
        menuEnemy.getEyes().draw(game.batch);
        for(MenuButton button : buttons) {
            button.draw(game.batch);
        }
        game.batch.end();
        game.batch.setProjectionMatrix(menuHighScores.getStage().getCamera().combined);
        if(previosState == State.HIGHSCORES) {
            menuHighScores.getStage().draw();
        }

        if(changeScreen) {
            game.setScreen(new PlayScreen(game));
            music.stop();
            dispose();
        }
    }



    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public void pause() {

    }

    public void resume() {

    }

    public void hide() {

    }

    public void dispose() {
        atlas.dispose();
        map.dispose();
        renderer.dispose();
        world.dispose();
        menuHighScores.dispose();
        //music.dispose();
    }

    public World getWorld() {
        return world;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public State getCurrentState() {
        return currentState;
    }

    public State getAnimationState() {
        return animationState;
    }
}
