package com.LedzinyGameDevelopment.SlitherNeonio.Tools;


import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade.SawBlade;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.RopePart;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys.Enemy;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Coin;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.Player;

import java.util.Random;

public class B2WorldCreator {

    private PlayScreen screen;
    private World world;
    private TiledMap map;
    private Player player;
    private Random random;

    private Array<Enemy> enemies;
    private Array<Item> coins;
    private Array<Item> coinsFromSnake;
    private Array<SawBlade> sawBlades;
    private float updateTime;
    private int numberOfCoins;
    private float worldGeneratingTime;
    private int creationLoop;



    public B2WorldCreator(PlayScreen screen) {
        this.screen = screen;
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        this.world = world;
        this.map = map;

        random = new Random();

        BodyDef bdef;
        FixtureDef fdef;
        Body body;
        updateTime = 0;
        worldGeneratingTime = 0;
        creationLoop = 0;

        // creates world bounds
        for(MapObject object : map.getLayers().get(1).getObjects().getByType(PolygonMapObject.class)){
            PolygonShape polygonShape = new PolygonShape();
            float[] vertices = ((PolygonMapObject) object).getPolygon().getTransformedVertices();

            float[] worldVertices = new float[vertices.length];

            for(int i = 0; i < vertices.length; i++) {
                worldVertices[i] = vertices[i] / BetterSnake.PPM;
            }
            polygonShape.set(worldVertices);

            bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;

            body = world.createBody(bdef);
            fdef = new FixtureDef();
            fdef.shape = polygonShape;
            fdef.filter.categoryBits = BetterSnake.WORLD_BORDER_BIT;
            fdef.isSensor = true;
            body.createFixture(fdef).setUserData(this);
        }

        enemies = new Array<>();
        sawBlades = new Array<>();

        // creates player
        player = new Player(screen, screen.getMapPixelHeight() / 2 / BetterSnake.PPM, screen.getMapPixelHeight() / 2 / BetterSnake.PPM, this);

        for(int i = 0; i < 15; i++) {
            createEnemy();
        }

        coins = new Array<>();
        coinsFromSnake = new Array<>();
        numberOfCoins = 1720;

        // creates coins when game starts
        for(int i = 0; i < numberOfCoins * player.getScale(); i++) {
            createCoin();
        }

        for(int i = 0; i < 5; i++) {
            createSawBlade();
        }

    }

    // create coin in appropriate place
    public void createCoin() {
        float x = random.nextInt(12300) - 6150;
        float y = random.nextInt(10600) - 5300;
        coins.add(new Coin(screen, player.getX() + getScaling(x),
                player.getY() + getScaling(y), this));
    }

    // create enemy in appropriate place and checks if enemy is out of world bounds
    public void createEnemy() {
        int side = random.nextInt(4);
        float x, y;

        if (side == 0) {
            x = random.nextInt(11000) - 5500;
            x = player.getX() + getScaling(x);
            y = random.nextInt(3000) + 2000;
            y = player.getY() + getScaling(y);
        } else if (side == 1) {
            x = (random.nextInt(11000) - 5500);
            x = player.getX() + getScaling(x);
            y = -(random.nextInt(3000) + 2000);
            y = player.getY() + getScaling(y);
        } else if (side == 2) {
            y = random.nextInt(10000) - 5000;
            y = player.getY() + getScaling(y);
            x = random.nextInt(3000) + 2500;
            x = player.getX() + getScaling(x);
        } else {
            y = (random.nextInt(10000) - 5000);
            y = player.getY() + getScaling(y);
            x = -(random.nextInt(3000) + 2500);
            x = player.getX() + getScaling(x);
        }

        Enemy enemy = new Enemy(screen, x, y, this);
        if(enemy.getX() < 3640 / BetterSnake.PPM || enemy.getX() > 124360 / BetterSnake.PPM || enemy.getY() < 3640 / BetterSnake.PPM || enemy.getY() > 124360 / BetterSnake.PPM) {
            enemy.outOfBoundsBody();
            enemy.getB2body().setActive(false);
        }
        enemies.add(enemy);
    }

    public void createSawBlade() {
        int side = random.nextInt(4);
        float x, y;

        if (side == 0) {
            x = random.nextInt(11000) - 5500;
            x = player.getX() + getScaling(x);
            y = random.nextInt(3000) + 2000;
            y = player.getY() + getScaling(y);
        } else if (side == 1) {
            x = (random.nextInt(11000) - 5500);
            x = player.getX() + getScaling(x);
            y = -(random.nextInt(3000) + 2000);
            y = player.getY() + getScaling(y);
        } else if (side == 2) {
            y = random.nextInt(10000) - 5000;
            y = player.getY() + getScaling(y);
            x = random.nextInt(3000) + 2500;
            x = player.getX() + getScaling(x);
        } else {
            y = (random.nextInt(10000) - 5000);
            y = player.getY() + getScaling(y);
            x = -(random.nextInt(3000) + 2500);
            x = player.getX() + getScaling(x);
        }

        SawBlade sawBlade = new SawBlade(screen, x, y, this);
        if(sawBlade.getX() < 3640 / BetterSnake.PPM || sawBlade.getX() > 124360 / BetterSnake.PPM ||
                sawBlade.getY() < 3640 / BetterSnake.PPM || sawBlade.getY() > 124360 / BetterSnake.PPM) {
            sawBlade.setOutOfBoundsBody(true);
            sawBlade.getB2body().setActive(false);
        }
        sawBlades.add(sawBlade);
    }

    public void update(float dt) {
        // if enemy out of enemy creation bounds removes enemy
        for (int i = 0; i < enemies.size; i++) {
            Snake enemy = enemies.get(i);
            if ((Math.abs(player.getX() - enemy.getX())) > getScaling(6300) ||
                    (Math.abs(player.getY() - enemy.getY())) > getScaling(5500)) {
                enemy.setRemoveSnake(true);
            }

        }

        // if there is less enemys or more than should be creates or remove enemy
        if (enemies.size < Math.pow(getScaling(3.78f) * BetterSnake.PPM, 1.65)) {
            createEnemy();
        } else if (enemies.size > Math.pow(getScaling(4.1f) * BetterSnake.PPM, 1.65)) {
            for (int i = 0; i < enemies.size; i++) {
                Snake enemy = enemies.get(i);
                if ((Math.abs(player.getX() - enemy.getX())) > getScaling(4500) ||
                        (Math.abs(player.getY() - enemy.getY())) > getScaling(4000)) {
                    enemy.setRemoveSnake(true);
                    break;
                }
            }
        }

        // if sawBlade out of sawBlde creation bounds removes enemy
        for (int i = 0; i < sawBlades.size; i++) {
            SawBlade sawBlade = sawBlades.get(i);
            if ((Math.abs(player.getX() - sawBlade.getX())) > getScaling(6300) ||
                    (Math.abs(player.getY() - sawBlade.getY())) > getScaling(5500)) {
                sawBlade.updateDestroyBody();
            }

        }

        // if there is less sawBlades or more than should be creates or remove sawBlade
        if (sawBlades.size < Math.pow(getScaling(3.78f) * BetterSnake.PPM, 1.65)) {
            createSawBlade();
        } else if (sawBlades.size > Math.pow(getScaling(4.1f) * BetterSnake.PPM, 1.65)) {
            for (int i = 0; i < sawBlades.size; i++) {
                SawBlade sawBlade = sawBlades.get(i);
                if ((Math.abs(player.getX() - sawBlade.getX())) > getScaling(4500) ||
                        (Math.abs(player.getY() - sawBlade.getY())) > getScaling(4000)) {
                    sawBlade.updateDestroyBody();
                    break;
                }
            }
        }

        numberOfCoins = (int) Math.pow((30 + (30 * (player.getScale() - 0.495f) * 0.5f)), 2);

        // creates coins when number of coins is to small
        if (coins.size < numberOfCoins) {
            int newCoinPosition = random.nextInt(2);
            int side;
            float x, y;
            if (newCoinPosition == 1) {
                x = random.nextInt(9520) - 4760;
                y = random.nextInt(2000) + 2520;

                x = player.getX() + getScaling(x);
                side = random.nextInt(2);
                y = (side == 1) ? player.getY() + getScaling(y) :
                        player.getY() - getScaling(y);
            } else {
                y = random.nextInt(7840) - 3920;
                x = random.nextInt(2000) + 3360;
                y = player.getY() + getScaling(y);
                side = random.nextInt(2);
                x = (side == 1) ? player.getX() + getScaling(x) :
                        player.getX() - getScaling(x);
            }
            coins.add(new Coin(screen, x, y, this));
        }

        // destroy coins when there is to much coins
        for(int i = 0; i < coins.size; i++) {
            if (coins.size > numberOfCoins + coinsFromSnake.size) {
                Item coin = coins.get(i);
                if (player.getX() > coin.getX() + getScaling(3000) ||
                        player.getX() < coin.getX() + -getScaling(3000) ||
                        player.getY() > coin.getY() + getScaling(2150) ||
                        player.getY() < coin.getY() + -getScaling(2150)) {
                    coin.destroyBody();
                } else {
                    break;
                }
            }
        }

        //destroy coins from snakes when they are out of creating bounds
        for(int i = 0; i < coinsFromSnake.size; i++) {
            Item coin = coinsFromSnake.get(i);

            if(player.getX() > coin.getX() + getScaling(6300) ||
                    player.getX() < coin.getX() + -getScaling(6300) ||
                    player.getY() > coin.getY() + getScaling(5500) ||
                    player.getY() < coin.getY() + -getScaling(5500)) {
                coinsFromSnake.removeIndex(i);
                coin.destroyBody();
            }
        }

        //destroys and creates coins in other places when player moves
        for(int i = 0; i < coins.size; i++) {
            Item coin = coins.get(i);
            if(player.getX() > coin.getX() + ((6350 + (6350 * (player.getScale() - 0.495f) * 0.5f)) / BetterSnake.PPM) ||
                    player.getX() < coin.getX() + -((6350 + (6350 * (player.getScale() - 0.495f) * 0.5f)) / BetterSnake.PPM) ||
                    player.getY() > coin.getY() + ((5500 + (5500 * (player.getScale() - 0.495f) * 0.5f)) / BetterSnake.PPM) ||
                    player.getY() < coin.getY() + -((5500 + (5500 * (player.getScale() - 0.495f) * 0.5f)) / BetterSnake.PPM)) {

                coin.destroyBody();
                int side;
                if(screen.getPlayerAngle() > -20 && screen.getPlayerAngle() < 20 || screen.getPlayerAngle() < - 160 && screen.getPlayerAngle() > 160)
                    side = 1;
                else if(screen.getPlayerAngle() > -100 && screen.getPlayerAngle() < -80 || screen.getPlayerAngle() < 100 && screen.getPlayerAngle() > 80)
                    side = 0;
                else
                    side = random.nextInt(2);
                float x, y;
                if(side == 1) {
                    x = random.nextInt(12699) - 6349;
                    y = random.nextInt(2000) + 3499;
                    x = player.getX() + getScaling(x);
                    side = random.nextInt(2);
                    y = (side == 1) ? player.getY() + getScaling(y) :
                            player.getY() - getScaling(y);
                } else {
                    x = random.nextInt(2500) + 3499;
                    y = random.nextInt(11000) - 5500;
                    y = player.getY() + getScaling(y);
                    side = random.nextInt(2);
                    x = (side == 1) ? player.getX() + getScaling(x) :
                            player.getX() - getScaling(x);
                }
                coins.add(new Coin(screen, x, y, this));
            }
            coin.update(dt); // updates coins
        }

        // updates enemies if enemy position is close to player position
        for(Enemy enemy : enemies) {
            for(SnakeTail tail : enemy.getTailArray()) {
                if (!((Math.abs(player.getX() - tail.getX())) > getScaling(2400)) &&
                        !((Math.abs(player.getY() - tail.getY())) > getScaling(1200))) {
                    enemy.update(dt); // updates enemies
                    if(enemy.getRope() != null)
                        enemy.getRope().update(dt);
                    if(enemy.getRope() == null) {
                        BodyDef bdef = new BodyDef();
                        bdef.position.set(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getWidth() / 2);
                        bdef.type = BodyDef.BodyType.DynamicBody;
                        CircleShape shape = new CircleShape();
                        shape.setRadius(20 / BetterSnake.PPM);
                        FixtureDef fdef = new FixtureDef();
                        fdef.shape = shape;
                        fdef.isSensor = true;
                        fdef.filter.categoryBits = BetterSnake.NOTHING_BIT;
                        enemy.setRopeB2body(world.createBody(bdef));
                        enemy.getRopeB2body().createFixture(fdef).setUserData(this);

                        enemy.addRopes(new RopePart(screen, enemy.getX(), enemy.getY(), screen.getCreator(), enemy.getScale(), enemy));

                    }
                    break;
                } else {
                    enemy.getB2body().setLinearVelocity(0, 0);
                    if(enemy.getRope() != null) {
                        //enemy.getRopes().getB2body()
                        enemy.getRopeB2body().setActive(false);
                        world.destroyBody(enemy.getRopeB2body());
                        world.destroyBody(enemy.getRope().getSawConnectorB2body());
                        world.destroyBody(enemy.getRope().getB2body());
                        if(enemy.getRope().getSawBlade() != null)
                            enemy.getRope().getSawBlade().setRemoveBody(true);
                        enemy.newRopesList();
                    }

                }
            }
            enemy.removeSnakeUpdate(dt);
        }

        for(SawBlade sawBlade : sawBlades) {
            if (!((Math.abs(player.getX() - sawBlade.getX())) > getScaling(2400)) &&
                    !((Math.abs(player.getY() - sawBlade.getY())) > getScaling(1200))) {
                sawBlade.update(dt, 0, 0); // updates sawBlades
            } else {
                sawBlade.getB2body().setLinearVelocity(0, 0);
            }
        }
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Item> getCoins() {
        return coins;
    }

    public Array<Item> getCoinsFromSnake() {
        return coinsFromSnake;
    }

    public Array<SawBlade> getSawBlades() {
        return sawBlades;
    }

    public Player getPlayer() {
        return player;
    }

    //calculates world creating distance by players scale
    public float getScaling(float distance) {
        return (distance + (distance * (player.getScale() - 0.495f) * 0.5f)) / BetterSnake.PPM;
    }

}