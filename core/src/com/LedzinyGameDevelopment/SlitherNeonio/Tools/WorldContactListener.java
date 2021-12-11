package com.LedzinyGameDevelopment.SlitherNeonio.Tools;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade.SawBlade;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.RopePart;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Scenes.HUD;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.Enemy;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.EnemyTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.Player;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.PlayerTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Coin;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;

public class WorldContactListener implements ContactListener {

    private HUD hud;
    private B2WorldCreator creator;

    public WorldContactListener(HUD hud, B2WorldCreator creator) {

        this.hud = hud;
        this.creator = creator;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case BetterSnake.ENEMY_BIT | BetterSnake.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {
                    if (((Enemy) fixA.getUserData()).getScale() > ((Player) fixB.getUserData()).getScale()) {
                        ((Player) fixB.getUserData()).setPlayerDead(true);
                    } else {
                        ((Enemy) fixA.getUserData()).setRemoveSnake(true);
                    }
                } else {
                    if (((Enemy) fixB.getUserData()).getScale() > ((Player) fixA.getUserData()).getScale()) {
                        ((Player) fixA.getUserData()).setPlayerDead(true);
                    } else {
                        ((Enemy) fixB.getUserData()).setRemoveSnake(true);
                    }
                }

                break;
            case BetterSnake.PLAYER_BIT | BetterSnake.ENEMY_TAIL_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).setPlayerDead(true);
                } else {
                    ((Player) fixB.getUserData()).setPlayerDead(true);
                }
                break;

            case BetterSnake.ENEMY_BIT | BetterSnake.PLAYER_TAIL_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {

                    ((Snake) fixA.getUserData()).setRemoveSnake(true);
                } else {

                    ((Snake) fixB.getUserData()).setRemoveSnake(true);
                }
                break;

            // checks if snake is making circle
            case BetterSnake.ENEMY_BIT | BetterSnake.ENEMY_TAIL_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {
                    if (fixB.getUserData() instanceof EnemyTail) {
                        if (!(fixA.getUserData().equals(((EnemyTail) fixB.getUserData()).getSnake()))) {

                            ((Snake) fixA.getUserData()).setRemoveSnake(true);
                        } else {
                            Snake enemy = (Enemy) fixA.getUserData();
                            if (enemy.getTailArray().size > 10) {
                                for (int i = 0; i < enemy.getTailArray().size - 8; i++) {
                                    if (enemy.getTailArray().get(i) == fixB.getUserData()) {
                                        ((EnemyTail) fixB.getUserData()).setSnakeTouchesHisTail(true);
                                    }
                                }
                            }
                        }
                    } else {
                        if (!fixA.getUserData().equals(fixB.getUserData())) {
                            ((Enemy) fixA.getUserData()).setRemoveSnake(true);
                        }
                    }
                } else {
                    if (fixA.getUserData() instanceof EnemyTail) {
                        if (!(fixB.getUserData().equals(((EnemyTail) fixA.getUserData()).getSnake()))) {

                            ((Snake) fixB.getUserData()).setRemoveSnake(true);
                        } else {
                            Snake enemy = (Enemy) fixB.getUserData();
                            if (enemy.getTailArray().size > 10) {
                                for (int i = 0; i < enemy.getTailArray().size - 8; i++) {
                                    if (enemy.getTailArray().get(i) == fixA.getUserData()) {
                                        ((EnemyTail) fixA.getUserData()).setSnakeTouchesHisTail(true);
                                    }
                                }
                            }
                        }
                    } else {
                        if (!fixB.getUserData().equals(fixA.getUserData())) {
                            ((Enemy) fixB.getUserData()).setRemoveSnake(true);
                        }
                    }
                }
                break;

            // checks if snake is making circle
            case BetterSnake.PLAYER_BIT | BetterSnake.PLAYER_TAIL_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    if (fixA.getUserData().equals(((PlayerTail) fixB.getUserData()).getSnake())) {
                        Snake player = (Player) fixA.getUserData();
                        if (player.getTailArray().size > 10) {
                            for (int i = 0; i < player.getTailArray().size - 8; i++) {
                                if (player.getTailArray().get(i) == fixB.getUserData()) {
                                    ((PlayerTail) fixB.getUserData()).setSnakeTouchesHisTail(true);
                                }
                            }
                        }
                    }
                } else {
                    if (fixA.getUserData().equals(((PlayerTail) fixB.getUserData()).getSnake())) {
                        Snake player = (Player) fixB.getUserData();
                        if (player.getTailArray().size > 10) {
                            for (int i = 0; i < player.getTailArray().size - 8; i++) {
                                if (player.getTailArray().get(i) == fixA.getUserData()) {
                                    ((PlayerTail) fixA.getUserData()).setSnakeTouchesHisTail(true);
                                }
                            }
                        }
                    }
                }
                break;

            case BetterSnake.PLAYER_BIT | BetterSnake.COIN_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).setSpeedBoost(((Player) fixA.getUserData()).getSpeedBoost() + 1);
                    if (!((Coin) fixB.getUserData()).isRemoveBody()) {
                        if (((Player) fixA.getUserData()).getScale() < 3) {
                            ((Player) fixA.getUserData()).setScale(((Player) fixA.getUserData()).getScale() + (((Coin) fixB.getUserData()).getScale() /
                                    250 / (((Player) fixA.getUserData()).getScale() * 3)));
                        } else {
                            ((Player) fixA.getUserData()).setScale(((Player) fixA.getUserData()).getScale() + (((Coin) fixB.getUserData()).getScale() /
                                    250 / (((Player) fixA.getUserData()).getScale() * 4)));
                        }

                        ((Player) fixA.getUserData()).setChangeScale(true);
                        ((Coin) fixB.getUserData()).destroyBody();
                    }
                } else {
                    ((Player) fixB.getUserData()).setSpeedBoost(((Player) fixB.getUserData()).getSpeedBoost() + 1);
                    if (!((Coin) fixA.getUserData()).isRemoveBody()) {
                        if (((Player) fixB.getUserData()).getScale() < 3) {
                            ((Player) fixB.getUserData()).setScale(((Player) fixB.getUserData()).getScale() + (((Coin) fixA.getUserData()).getScale() /
                                    250 / (((Player) fixB.getUserData()).getScale() * 3)));
                        } else {
                            ((Player) fixB.getUserData()).setScale(((Player) fixB.getUserData()).getScale() + (((Coin) fixA.getUserData()).getScale() /
                                    250 / (((Player) fixB.getUserData()).getScale() * 4)));
                        }

                        ((Player) fixB.getUserData()).setChangeScale(true);
                        ((Coin) fixA.getUserData()).destroyBody();
                    }
                }
                break;

            case BetterSnake.ENEMY_BIT | BetterSnake.COIN_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).setSpeedBoost(((Enemy) fixA.getUserData()).getSpeedBoost() + 1);
                    if (!((Coin) fixB.getUserData()).isRemoveBody()) {
                        if (((Enemy) fixA.getUserData()).getScale() < 3) {
                            ((Enemy) fixA.getUserData()).setScale(((Enemy) fixA.getUserData()).getScale() + (((Coin) fixB.getUserData()).getScale() /
                                    250 / (((Enemy) fixA.getUserData()).getScale() * 3)));
                        } else {
                            ((Enemy) fixA.getUserData()).setScale(((Enemy) fixA.getUserData()).getScale() + (((Coin) fixB.getUserData()).getScale() /
                                    250 / (((Enemy) fixA.getUserData()).getScale() * 4)));
                        }

                        ((Enemy) fixA.getUserData()).setChangeScale(true);
                        ((Coin) fixB.getUserData()).destroyBody();
                    }
                } else {
                    ((Enemy) fixB.getUserData()).setSpeedBoost(((Enemy) fixB.getUserData()).getSpeedBoost() + 1);
                    if (!((Coin) fixA.getUserData()).isRemoveBody()) {

                        if (((Enemy) fixB.getUserData()).getScale() < 3) {
                            ((Enemy) fixB.getUserData()).setScale(((Enemy) fixB.getUserData()).getScale() + (((Coin) fixA.getUserData()).getScale() /
                                    250 / (((Enemy) fixB.getUserData()).getScale() * 3)));
                        } else {
                            ((Enemy) fixB.getUserData()).setScale(((Enemy) fixB.getUserData()).getScale() + (((Coin) fixA.getUserData()).getScale() /
                                    250 / (((Enemy) fixB.getUserData()).getScale() * 4)));
                        }
                        ((Enemy) fixB.getUserData()).setChangeScale(true);
                        ((Coin) fixA.getUserData()).destroyBody();

                    }
                }
                break;

            case BetterSnake.TOUNGE_BIT | BetterSnake.COIN_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.TOUNGE_BIT) {
                    boolean contains = false;
                    for (Item coin : ((Snake) fixA.getUserData()).getCoinsArray()) {
                        if (coin.equals(fixB.getUserData())) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        ((Snake) fixA.getUserData()).getCoinsArray().add((Coin) fixB.getUserData());
                    }
                } else {
                    boolean contains = false;
                    for (Item coin : ((Snake) fixB.getUserData()).getCoinsArray()) {
                        if (coin.equals(fixA.getUserData())) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        ((Snake) fixB.getUserData()).getCoinsArray().add((Coin) fixA.getUserData());
                    }
                }
                break;

            // checks if there is coin that snake can eat
            case BetterSnake.COIN_BIT | BetterSnake.COIN_FINDER_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.COIN_FINDER_BIT) {
                    if (!((Item) fixB.getUserData()).isRemoveBody()) {
                        if (((Enemy) fixA.getUserData()).getCoinFromCoinFinder() != null) {
                            if (((Coin) ((Enemy) fixA.getUserData()).getCoinFromCoinFinder()).getScale() < ((Coin) fixB.getUserData()).getScale()) {
                                ((Enemy) fixA.getUserData()).setCoinFromCoinFinder((Item) fixB.getUserData());
                                ((Enemy) fixA.getUserData()).setCoinInCoinFinder(true);
                            }
                        } else {
                            ((Enemy) fixA.getUserData()).setCoinFromCoinFinder((Item) fixB.getUserData());
                            ((Enemy) fixA.getUserData()).setCoinInCoinFinder(true);
                        }
                    }
                } else {
                    if (!((Item) fixA.getUserData()).isRemoveBody()) {
                        if (((Enemy) fixB.getUserData()).getCoinFromCoinFinder() != null) {
                            if (((Coin) ((Enemy) fixB.getUserData()).getCoinFromCoinFinder()).getScale() < ((Coin) fixA.getUserData()).getScale()) {
                                ((Enemy) fixB.getUserData()).setCoinFromCoinFinder((Item) fixA.getUserData());
                                ((Enemy) fixB.getUserData()).setCoinInCoinFinder(true);
                            }
                        } else {
                            ((Enemy) fixB.getUserData()).setCoinFromCoinFinder((Item) fixB.getUserData());
                            ((Enemy) fixB.getUserData()).setCoinInCoinFinder(true);
                        }
                    }
                }
                break;

            // checks if player is not anymore in the area that snake he can kill him
            case BetterSnake.PLAYER_BIT | BetterSnake.KILL_AREA_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    ((Enemy) fixB.getUserData()).setPlayerToKill(((Player) fixA.getUserData()));
                } else {
                    ((Enemy) fixA.getUserData()).setPlayerToKill(((Player) fixB.getUserData()));
                }
                break;

            case BetterSnake.WORLD_BORDER_BIT | BetterSnake.PLAYER_BIT:
                HUD.setWorldBoundTouched(true);
                break;

            case BetterSnake.PLAYER_BIT | BetterSnake.SAW_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    if(fixA.getUserData() != ((SawBlade) fixB.getUserData()).getSnake()) {
                        ((Player) fixA.getUserData()).setPlayerDead(true);
                        ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                    }
                } else {
                    if(fixB.getUserData() != ((SawBlade) fixA.getUserData()).getSnake()) {
                        ((Player) fixB.getUserData()).setPlayerDead(true);
                        ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                    }
                }
                break;

            case BetterSnake.ENEMY_BIT | BetterSnake.SAW_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {
                    if(fixA.getUserData() != ((SawBlade) fixB.getUserData()).getSnake()) {
                        ((Enemy) fixA.getUserData()).setRemoveSnake(true);
                        ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                    }
                } else {
                    if(fixB.getUserData() != ((SawBlade) fixA.getUserData()).getSnake()) {
                        ((Enemy) fixB.getUserData()).setRemoveSnake(true);
                        ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                    }
                }
                break;
            case BetterSnake.PLAYER_TAIL_BIT | BetterSnake.SAW_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.PLAYER_TAIL_BIT) {
                    if (fixA.getUserData() instanceof Player) {
                        if(fixA.getUserData() != ((SawBlade) fixB.getUserData()).getSnake()) {
                            ((Player) fixA.getUserData()).setPlayerDead(true);
                            ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                        }
                    } else {
                        if(((PlayerTail) fixA.getUserData()).getPlayer() != ((SawBlade) fixB.getUserData()).getSnake()) {
                            ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                        }
                    }
                } else {
                    if (fixB.getUserData() instanceof Player) {
                        if(fixB.getUserData() != ((SawBlade) fixA.getUserData()).getSnake()) {
                            ((Player) fixB.getUserData()).setPlayerDead(true);
                            ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                        }
                    } else {
                        if(((PlayerTail) fixB.getUserData()).getPlayer() != ((SawBlade) fixA.getUserData()).getSnake()) {
                            ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                        }
                    }
                }
                break;
            case BetterSnake.ENEMY_TAIL_BIT | BetterSnake.SAW_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.ENEMY_TAIL_BIT) {
                    if (fixA.getUserData() instanceof Enemy) {
                        if(fixA.getUserData() != ((SawBlade) fixB.getUserData()).getSnake()) {
                            ((Enemy) fixA.getUserData()).setRemoveSnake(true);
                            ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                        }
                    } else {
                        if(((EnemyTail) fixA.getUserData()).getSnake() != ((SawBlade) fixB.getUserData()).getSnake()) {
                            ((SawBlade) fixB.getUserData()).setRemoveBody(true);
                        }
                    }
                } else {
                    if (fixB.getUserData() instanceof Enemy) {
                        if(fixB.getUserData() != ((SawBlade) fixA.getUserData()).getSnake()) {
                            ((Enemy) fixB.getUserData()).setRemoveSnake(true);
                            ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                        }
                    } else {
                        if(((EnemyTail) fixB.getUserData()).getSnake() != ((SawBlade) fixA.getUserData()).getSnake()) {
                            ((SawBlade) fixA.getUserData()).setRemoveBody(true);
                        }
                    }
                }
                break;
            case BetterSnake.ROPE_BIT | BetterSnake.SAW_BIT:
                if(fixA.getFilterData().categoryBits == BetterSnake.ROPE_BIT) {
                    if(((SawBlade) fixB.getUserData()).getState() != SawBlade.State.TOUCHEDBYROPE && ((SawBlade) fixB.getUserData()).getSnake() == null) {
                        if(((RopePart) fixA.getUserData()).getSnake() != null && ((RopePart) fixA.getUserData()).getSawBlade() != null && ((RopePart) fixA.getUserData()).getSawBlade() != fixB.getUserData()) {

                                ((RopePart) fixA.getUserData()).getSawBlade().setRemoveBody(true);

                        }
                        ((SawBlade) fixB.getUserData()).setState(SawBlade.State.TOUCHEDBYROPE);
                        ((SawBlade) fixB.getUserData()).setSnake(((RopePart) fixA.getUserData()).getSnake());
                    }
                } else {
                    if (((SawBlade) fixA.getUserData()).getState() != SawBlade.State.TOUCHEDBYROPE && ((SawBlade) fixA.getUserData()).getSnake() == null) {
                        if(((RopePart) fixB.getUserData()).getSnake() != null && ((RopePart) fixB.getUserData()).getSawBlade() != null && ((RopePart) fixB.getUserData()).getSawBlade() != fixA.getUserData()) {

                                ((RopePart) fixB.getUserData()).getSawBlade().setRemoveBody(true);

                        }
                        ((SawBlade) fixA.getUserData()).setState(SawBlade.State.TOUCHEDBYROPE);
                        ((SawBlade) fixA.getUserData()).setSnake(((RopePart) fixB.getUserData()).getSnake());
                    }

                }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case BetterSnake.TOUNGE_BIT | BetterSnake.COIN_BIT:
                if (fixA.getFilterData().categoryBits == BetterSnake.TOUNGE_BIT) {
                    for(int i = 0; i < ((Snake) fixA.getUserData()).getCoinsArray().size; i++ ) {
                        Item coin = ((Snake) fixA.getUserData()).getCoinsArray().get(i);
                        if(coin.equals(fixB.getUserData())) {
                            ((Snake) fixA.getUserData()).getCoinsArray().removeIndex(i);
                        }
                    }

                } else {
                    for (int i = 0; i < ((Snake) fixB.getUserData()).getCoinsArray().size; i++) {
                        Item coin = ((Snake) fixB.getUserData()).getCoinsArray().get(i);
                        if (coin.equals(fixA.getUserData())) {
                            ((Snake) fixB.getUserData()).getCoinsArray().removeIndex(i);
                        }
                    }
                }
                break;

                //checks if snake is not anymore making circle
            case BetterSnake.PLAYER_BIT | BetterSnake.PLAYER_TAIL_BIT:
                if(fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    if(fixA.getUserData().equals(((PlayerTail) fixB.getUserData()).getSnake())) {
                        Snake player = (Player) fixA.getUserData();
                        if(player.getTailArray().size > 10) {
                            for (int i = 0; i < player.getTailArray().size - 8; i++) {
                                if(player.getTailArray().get(i) == fixB.getUserData()) {
                                    ((PlayerTail) fixB.getUserData()).setSnakeTouchesHisTail(false);
                                }
                            }
                        }
                    }
                } else {
                    if(fixA.getUserData().equals(((PlayerTail) fixB.getUserData()).getSnake())) {
                        Snake player = (Player) fixB.getUserData();
                        if(player.getTailArray().size > 10) {
                            for (int i = 0; i < player.getTailArray().size - 8; i++) {
                                if(player.getTailArray().get(i) == fixA.getUserData()) {
                                    ((PlayerTail) fixA.getUserData()).setSnakeTouchesHisTail(false);
                                }
                            }
                        }
                    }
                }
                break;
            //checks if snake is not anymore making circle
            case BetterSnake.ENEMY_BIT | BetterSnake.ENEMY_TAIL_BIT:
                if(fixA.getFilterData().categoryBits == BetterSnake.ENEMY_BIT) {
                    if(fixB.getUserData() instanceof EnemyTail) {
                        if (fixA.getUserData().equals(((EnemyTail) fixB.getUserData()).getSnake())) {
                            Snake enemy = (Enemy) fixA.getUserData();
                            if (enemy.getTailArray().size > 10) {
                                for (int i = 0; i < enemy.getTailArray().size - 8; i++) {
                                    if (enemy.getTailArray().get(i) == fixB.getUserData()) {
                                        ((EnemyTail) fixB.getUserData()).setSnakeTouchesHisTail(false);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    if(fixA.getUserData() instanceof EnemyTail) {
                        if (fixB.getUserData().equals(((EnemyTail) fixA.getUserData()).getSnake())) {
                            Snake enemy = (Enemy) fixB.getUserData();
                            if (enemy.getTailArray().size > 10) {
                                for (int i = 0; i < enemy.getTailArray().size - 8; i++) {
                                    if (enemy.getTailArray().get(i) == fixA.getUserData()) {
                                        ((EnemyTail) fixA.getUserData()).setSnakeTouchesHisTail(false);
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            // checks if player is not anymore in the area that snake he can kill him
            case BetterSnake.PLAYER_BIT | BetterSnake.KILL_AREA_BIT:
                if(fixA.getFilterData().categoryBits == BetterSnake.PLAYER_BIT) {
                    ((Enemy) fixB.getUserData()).setPlayerToKill(null);
                } else {
                    ((Enemy) fixA.getUserData()).setPlayerToKill(null);
                }
                break;

            case BetterSnake.WORLD_BORDER_BIT | BetterSnake.PLAYER_BIT:
                HUD.setWorldBoundTouched(false);
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
