package com.LedzinyGameDevelopment.SlitherNeonio;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.MenuScreen;

public class BetterSnake extends com.badlogic.gdx.Game {
	public static final int V_WIDTH = 1920;
	public static final int V_HEIGHT = 1080;
	public static final float PPM = 100;

	public static final short NOTHING_BIT = 0;
	public static final short PLAYER_BIT = 1;
	public static final short PLAYER_TAIL_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ENEMY_TAIL_BIT = 8;
	public static final short OBJECT_BIT = 16;
	public static final short COIN_BIT = 32;
	public static final short WORLD_BORDER_BIT = 64;
	public static final short KILL_AREA_BIT = 128;
	public static final short TOUNGE_BIT = 256;
	public static final short COIN_FINDER_BIT = 512;
	public static final short SAW_BIT = 1024;
	public static final short ROPE_BIT = 2048;


	public SpriteBatch batch;
	public static AssetManager manager;

	public void create () {
		batch = new SpriteBatch();

		manager = new AssetManager();
		manager.load("audio/music/game_menu.ogg", Music.class);
		manager.load("audio/music/game_background.ogg", Music.class);
		manager.load("audio/sounds/coin.ogg", Sound.class);
		manager.load("audio/sounds/click.ogg", Sound.class);
		manager.load("audio/sounds/game_over.ogg", Sound.class);
		manager.load("audio/sounds/achievement.ogg", Sound.class);
		manager.finishLoading();

		setScreen(new MenuScreen(this));

	}

	public void render () {
		super.render();
	}
	
	public void dispose () {
		batch.dispose();

	}
}
