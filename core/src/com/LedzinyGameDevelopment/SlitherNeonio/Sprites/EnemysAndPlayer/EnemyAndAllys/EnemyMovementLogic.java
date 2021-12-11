package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys;

import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade.SawBlade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;


import java.util.Random;

public class EnemyMovementLogic {

    private B2WorldCreator creator;
    private Snake snake;
    private Random random;
    private PlayScreen screen;
    private Vector2 vel;


    private int timer;
    private int timerMapMiddle;
    private int timerGoToPlayer;
    private double angle;
    private double newAngle;
    private double closeSnakeAngle;
    private boolean otherSnakeClose;
    private Sprite closeObject;


    public EnemyMovementLogic(Snake snake, double angle, PlayScreen screen, B2WorldCreator creator) {
        this.snake = snake;
        this.angle = angle;
        this.screen = screen;
        this.creator = creator;
        random = new Random();
        timer = random.nextInt(10);
        timerMapMiddle = 3;
        newAngle = 1;
        timerGoToPlayer = random.nextInt(80);
    }

    public void start(float velocity) {

        //logic that makes enemys stay in the map middle, dont get to close to map border, avoid other enemys, try kill player
        if(snake instanceof Enemy) {
            ((Enemy) snake).setBoostSpeed(false);
            //this part makes snake to turn back when to close to map border
            if (snake.getX() * BetterSnake.PPM > 123776) {
                if (angle < 90)
                    newAngle = -(random.nextInt(16) + 75);
                else
                    newAngle = -(random.nextInt(16) + 90);

            } else if (snake.getX() * BetterSnake.PPM < 4224) {
                if (angle < -90)
                    newAngle = random.nextInt(16) + 75;
                else
                    newAngle = random.nextInt(16) + 90;
            } else if (snake.getY() * BetterSnake.PPM > 123776) {
                if (angle < 0)
                    newAngle = -(random.nextInt(16));
                else
                    newAngle = random.nextInt(16);
            } else if (snake.getY() * BetterSnake.PPM < 4224) {
                if (angle >= 0)
                    newAngle = random.nextInt(15) + 165;
                else
                    newAngle = random.nextInt(16) - 179;
            } else {
                closeSnakeAngle = 600;
                otherSnakeClose = false;
                float distance = 10000;
                //this part checks if ther is other snake close and makes snake turn back
                for(int i = 0; i < creator.getEnemies().size; i++) {
                    Enemy enemy = creator.getEnemies().get(i);
                    for(SnakeTail tail : enemy.getTailArray()) {
                        if(!snake.equals(enemy)) {
                            if (Math.abs(tail.getX()) - Math.abs(snake.getX()) < snake.getSnakeWidth() * 2 + enemy.getSnakeWidth() + 2 &&
                                    Math.abs(tail.getX()) - Math.abs(snake.getX()) > -(snake.getSnakeWidth() * 2 + enemy.getSnakeWidth() + 2) &&
                                    Math.abs(tail.getY()) - Math.abs(snake.getY()) < snake.getSnakeWidth() * 2 + enemy.getSnakeWidth() + 2 &&
                                    Math.abs(tail.getY()) - Math.abs(snake.getY()) > -(snake.getSnakeWidth() * 2 + enemy.getSnakeWidth() + 2)) {
                                if (Math.abs(tail.getX() - snake.getX()) + Math.abs(tail.getY() - snake.getY()) < distance) {
                                    closeObject = enemy;
                                    closeSnakeAngle = enemy.getEnemyMovementLogic().getAngle();
                                    distance = Math.abs(tail.getX() - snake.getX()) + Math.abs(tail.getY() - snake.getY());
                                    newAngle = Math.toDegrees(Math.atan2(snake.getX() - tail.getX(), -(snake.getY() - tail.getY())));
                                }

                                otherSnakeClose = true;
                            }
                        }
                    }
                }
                for(int i = 0; i < creator.getSawBlades().size; i++) {
                    SawBlade sawBlade = creator.getSawBlades().get(i);
                    if (Math.abs(sawBlade.getX()) - Math.abs(snake.getX()) < snake.getSnakeWidth() * 2 + sawBlade.getRadius() + 2 &&
                            Math.abs(sawBlade.getX()) - Math.abs(snake.getX()) > -(snake.getSnakeWidth() * 2 + sawBlade.getRadius() + 2) &&
                            Math.abs(sawBlade.getY()) - Math.abs(snake.getY()) < snake.getSnakeWidth() * 2 + sawBlade.getRadius() + 2 &&
                            Math.abs(sawBlade.getY()) - Math.abs(snake.getY()) > -(snake.getSnakeWidth() * 2 + sawBlade.getRadius() + 2)) {
                        if ((Math.abs(sawBlade.getX() - snake.getX()) + Math.abs(sawBlade.getY() - snake.getY()) < distance)) {
                            if(snake.getRope() != null) {
                                if(sawBlade != snake.getRope().getSawBlade()) {
                                    closeObject = sawBlade;
                                    distance = Math.abs(sawBlade.getX() - snake.getX()) + Math.abs(sawBlade.getY() - snake.getY());
                                    newAngle = Math.toDegrees(Math.atan2(snake.getX() - sawBlade.getX(), -(snake.getY() - sawBlade.getY())));
                                }
                            } else {
                                closeObject = sawBlade;
                                distance = Math.abs(sawBlade.getX() - snake.getX()) + Math.abs(sawBlade.getY() - snake.getY());
                                newAngle = Math.toDegrees(Math.atan2(snake.getX() - sawBlade.getX(), -(snake.getY() - sawBlade.getY())));
                            }
                        }

                        otherSnakeClose = true;
                    }
                }
                for(SnakeTail tail : screen.getCreator().getPlayer().getTailArray()) {
                    if(Math.abs(tail.getX()) - Math.abs(snake.getX()) < snake.getSnakeWidth() * 2 + creator.getPlayer().getSnakeWidth() + 2 &&
                            Math.abs(tail.getX()) - Math.abs(snake.getX()) > -(snake.getSnakeWidth() * 2 + creator.getPlayer().getSnakeWidth() + 2) &&
                            Math.abs(tail.getY()) - Math.abs(snake.getY()) < snake.getSnakeWidth() * 2 + creator.getPlayer().getSnakeWidth() + 2 &&
                            Math.abs(tail.getY()) - Math.abs(snake.getY()) > -(snake.getSnakeWidth() * 2 + creator.getPlayer().getSnakeWidth() + 2)) {
                        if(Math.abs(tail.getX() - snake.getX()) + Math.abs(tail.getY() - snake.getY()) < distance) {
                            closeObject = creator.getPlayer();
                            closeSnakeAngle = screen.getPlayerAngle();
                            distance = Math.abs(tail.getX() - snake.getX()) + Math.abs(tail.getY() - snake.getY());
                            newAngle = Math.toDegrees(Math.atan2(snake.getX() - tail.getX(), -(snake.getY() - tail.getY())));
                        }

                        otherSnakeClose = true;
                    }
                }

                //this checks if close snake is equal to player to kill, when is equal then snake try to kill player
                if(closeObject == ((Enemy) snake).getPlayerToKill()) {
                    ((Enemy) snake).setBoostSpeed(true);
                    if(((Enemy) snake).getPlayerToKill() != null) {

                        if((closeSnakeAngle >= -120 || closeSnakeAngle <= 120) && Math.abs(closeSnakeAngle - angle) < 30) {
                            newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - ((Enemy) snake).getPlayerToKill().getX()),
                                    snake.getY() - ((Enemy) snake).getPlayerToKill().getY()));
                            ((Enemy) snake).setBoostSpeed(true);
                        } else if (closeSnakeAngle > 120 || closeSnakeAngle < -120){
                            if(angle > 0 && closeSnakeAngle < 0) {
                                if((180 - angle) + (180 + closeSnakeAngle) < 60) {
                                    newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - ((Enemy) snake).getPlayerToKill().getX()),
                                            snake.getY() - ((Enemy) snake).getPlayerToKill().getY()));
                                    ((Enemy) snake).setBoostSpeed(true);
                                }
                            } else if (angle < 0 && closeSnakeAngle > 0) {
                                if((180 - closeSnakeAngle) + (180 + angle) < 60) {
                                    newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - ((Enemy) snake).getPlayerToKill().getX()),
                                            snake.getY() - ((Enemy) snake).getPlayerToKill().getY()));
                                    ((Enemy) snake).setBoostSpeed(true);
                                }
                            } else if(Math.abs(closeSnakeAngle - angle) < 60) {
                                newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - ((Enemy) snake).getPlayerToKill().getX()),
                                        snake.getY() - ((Enemy) snake).getPlayerToKill().getY()));
                                ((Enemy) snake).setBoostSpeed(true);
                            }
                        }
                    }
                } else if(otherSnakeClose) { // if snake do not try to kill player then this continue avoiding other players,
                    if(closeSnakeAngle != 600) { // also checks if player or other snake not making circle (when other snake making circle and this snake is insade not increasing speed)
                        boolean snakeNotMakingCircle = true;
                        if(closeObject instanceof Snake) {
                            for (SnakeTail snakeTail : ((Snake) closeObject).getTailArray()) {
                                if (snakeTail.isSnakeTouchesHisTail()) {
                                    snakeNotMakingCircle = false;
                                }
                            }
                            if (snakeNotMakingCircle) {
                                if ((closeSnakeAngle >= -150 || closeSnakeAngle <= 150) && Math.abs(closeSnakeAngle - angle) < 30)
                                    ((Enemy) snake).setBoostSpeed(true);
                                else if (closeSnakeAngle > 150 || closeSnakeAngle < -150) {
                                    if (angle > 0 && closeSnakeAngle < 0) {
                                        if ((180 - angle) + (180 + closeSnakeAngle) < 30) {
                                            ((Enemy) snake).setBoostSpeed(true);
                                        }
                                    } else if (angle < 0 && closeSnakeAngle > 0) {
                                        if ((180 - closeSnakeAngle) + (180 + angle) < 30) {
                                            ((Enemy) snake).setBoostSpeed(true);
                                        }
                                    } else if (Math.abs(closeSnakeAngle - angle) < 30) {
                                        ((Enemy) snake).setBoostSpeed(true);
                                    }
                                }
                            }
                        }
                    }
                    timerGoToPlayer--;
                    if(timerGoToPlayer <= 0) {
                        timerGoToPlayer = random.nextInt(80);
                    }
                }
                if(!otherSnakeClose) { // makes snake looking for coins
                    if((((Enemy) snake).getSnakeType() == 4 || ((Enemy) snake).getSnakeType() == 5 ) && creator.getPlayer().getScale() > 2) {
                        if (timerGoToPlayer < 20) {
                            newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - creator.getPlayer().getX()),
                                    snake.getY() - creator.getPlayer().getY()));
                            timerGoToPlayer--;
                            if(timerGoToPlayer <= 0) {
                                timerGoToPlayer = random.nextInt(80);
                            }
                        } else if(((Enemy) snake).isCoinInCoinFinder()) {
                            if (!((Enemy) snake).getCoinFromCoinFinder().isRemoveBody()) {
                                Item coin = ((Enemy) snake).getCoinFromCoinFinder();
                                newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - coin.getX()), snake.getY() - coin.getY()));
                            } else {
                                ((Enemy) snake).setCoinInCoinFinder(false);
                                ((Enemy) snake).setCoinFromCoinFinder(null);
                            }
                            timerGoToPlayer--;
                            if(timerGoToPlayer <= 0) {
                                timerGoToPlayer = random.nextInt(80);
                            }
                        }else {
                            newAngle = (double) random.nextInt(361) - 180;
                            if (newAngle > -30 && newAngle <= 0)
                                newAngle -= 70;
                            if (newAngle < 30 && newAngle > 0)
                                newAngle += 70;
                            timerGoToPlayer--;
                            if(timerGoToPlayer <= 0) {
                                timerGoToPlayer = random.nextInt(80);
                            }
                        }
                    } else if(((Enemy) snake).isCoinInCoinFinder()) {
                        if(!((Enemy) snake).getCoinFromCoinFinder().isRemoveBody()) {
                            Item coin = ((Enemy) snake).getCoinFromCoinFinder();
                            newAngle = Math.toDegrees(Math.atan2(-(snake.getX() - coin.getX()), snake.getY() - coin.getY()));
                        } else {
                            ((Enemy) snake).setCoinInCoinFinder(false);
                            ((Enemy) snake).setCoinFromCoinFinder(null);
                        }
                    } else { // makes snake to naturally move in random direction
                        if (timer == 0) {
                            if (timerMapMiddle == 2) {
                                if (snake.getY() * BetterSnake.PPM < 9600) {
                                    if (angle >= 0)
                                        newAngle = random.nextInt(15) + 165;
                                    else
                                        newAngle = random.nextInt(16) - 179;
                                }
                                timerMapMiddle--;
                            } else if (timerMapMiddle == 1) {
                                if (snake.getY() * BetterSnake.PPM < 6400) {
                                    if (angle >= 0)
                                        newAngle = random.nextInt(15) + 165;
                                    else
                                        newAngle = random.nextInt(16) - 179;
                                }
                                timerMapMiddle--;
                            } else if (timerMapMiddle == 0) {
                                if (snake.getX() * BetterSnake.PPM > 85760) {
                                    if (angle < 90)
                                        newAngle = -(random.nextInt(16) + 75);
                                    else
                                        newAngle = -(random.nextInt(16) + 90);
                                } else if (snake.getX() * BetterSnake.PPM < 42240) {
                                    if (angle < -90)
                                        newAngle = random.nextInt(16) + 75;
                                    else
                                        newAngle = random.nextInt(16) + 90;
                                } else if (snake.getY() * BetterSnake.PPM > 85760) {
                                    if (angle < 0)
                                        newAngle = -(random.nextInt(16));
                                    else
                                        newAngle = random.nextInt(16);
                                } else if (snake.getY() * BetterSnake.PPM < 42240) {
                                    if (angle >= 0)
                                        newAngle = random.nextInt(15) + 165;
                                    else
                                        newAngle = random.nextInt(16) - 179;
                                }
                                timerMapMiddle = 3;
                            } else {
                                timerMapMiddle--;
                                timer = random.nextInt(30);
                                newAngle = (double) random.nextInt(361) - 180;
                                if (newAngle > -30 && newAngle <= 0)
                                    newAngle -= 70;
                                if (newAngle < 30 && newAngle > 0)
                                    newAngle += 70;

                            }
                        } else
                            timer--;
                    }
                }
            }
            float deltaTime = Gdx.graphics.getDeltaTime();

            // makes snake to making circle when turning back
            if(Math.abs(angle - newAngle) > deltaTime * 400 / (snake.getScale() * 2)) {
                float distanceToZero;

                if ((angle <= 0 && newAngle <= 0) || (angle >= 0 && newAngle >= 0)) {
                    if (newAngle < angle) {
                        angle -= deltaTime * 400 / (snake.getScale() * 2);
                    } else {
                        angle += deltaTime * 400 / (snake.getScale() * 2);
                    }
                } else if (angle < 0 && newAngle > 0) {
                    distanceToZero = (float) (Math.abs(angle) + Math.abs(newAngle));
                    if (distanceToZero < 180) {
                        angle += deltaTime * 400 / (snake.getScale() * 2);
                    } else {
                        angle -= deltaTime * 400 / (snake.getScale() * 2);
                    }
                } else if (angle > 0 && newAngle < 0) {
                    distanceToZero = (float) (Math.abs(angle) + Math.abs(newAngle));
                    if (distanceToZero < 180) {
                        angle -= deltaTime * 400 / (snake.getScale() * 2);
                    } else {
                        angle += deltaTime * 400 / (snake.getScale() * 2);
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
    }

    public Vector2 getVel() {
        return vel;
    }

    public double getAngle() {
        return angle;
    }

}
