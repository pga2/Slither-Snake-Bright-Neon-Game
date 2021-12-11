package com.LedzinyGameDevelopment.SlitherNeonio.Tools;


import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class NeonColorChanger {

    private final int chooseObjectUsingThisClass; // 0 = snake, 1 = border, 2 = flag
    private Random random;

    //to make close borders almost in the same color
    private static int colorNumber = 9;
    private static boolean goesUp = true;

    private int r;
    private int g;
    private int b;
    private int timer;
    private int color; // 0 = blue, 1 = green, 2 = pink, 3 = yellow for snake and border
    private boolean[] allColorsGood;

    public NeonColorChanger(int chooseObjectUsingThisClass) {
        random = new Random();
        this.chooseObjectUsingThisClass = chooseObjectUsingThisClass;
        if(chooseObjectUsingThisClass == 0) { // for players
            int snakeFirstColor = random.nextInt(4);
            if (snakeFirstColor == 0) {
                r = 8;
                g = random.nextInt(4) + 247;
                b = random.nextInt(43) + 212;
                color = 0;
            } else if (snakeFirstColor == 1) {
                r = random.nextInt(245) + 9;
                g = random.nextInt(168) + 83;
                b = random.nextInt(187) + 24;
                color = 1;
            } else if (snakeFirstColor == 2) {
                r = random.nextInt(9) + 245;
                g = random.nextInt(128) + 83;
                b = random.nextInt(187);
                color = 2;
            } else {
                r = random.nextInt(237) + 8;
                g = random.nextInt(36) + 211;
                b = random.nextInt(254);
                color = 3;
            }
        } else if(chooseObjectUsingThisClass == 1) { // for borders
            color = 1;
            if(colorNumber < 254 && goesUp) {
                colorNumber += 20;
                r = colorNumber;
                g = 250;
                b = 211;
            } else if(goesUp){
                goesUp = false;
            }
            if(colorNumber > 9 & !goesUp) {
                colorNumber -= 20;
                r = colorNumber;
                g = 250;
                b = 211;
            } else if(!goesUp) {
                goesUp = true;
            }
        }

        allColorsGood = new boolean[3];
        allColorsGood[0] = false;
        allColorsGood[1] = false;
        allColorsGood[2] = false;
        timer = 20;
    }

    public void calculateColors() {

        // for players and world borders
        if(chooseObjectUsingThisClass == 0 || chooseObjectUsingThisClass == 1) {
            if (color == 0) {
                if (timer == 0) {
                    if (r < 9) {
                        r++;
                    } else {
                        r = 9;
                        allColorsGood[0] = true;
                    }
                    if (g < 251) {
                        g++;
                    } else {
                        g = 251;
                        allColorsGood[1] = true;
                    }
                    if (b > 211) {
                        b--;
                    } else {
                        b = 211;
                        allColorsGood[2] = true;
                    }


                    if (allColorsGood[0] && allColorsGood[1] && allColorsGood[2]) {
                        allColorsGood[0] = false;
                        allColorsGood[1] = false;
                        allColorsGood[2] = false;
                        color = 1;
                        if (chooseObjectUsingThisClass == 0)
                            timer = random.nextInt(60) + 20;
                        else if (chooseObjectUsingThisClass == 1)
                            timer = 30;
                    }
                } else {
                    timer--;
                }
            } else if (color == 1) {
                if (timer == 0) {
                    if (r < 254) {
                        r++;
                    } else {
                        r = 254;
                        allColorsGood[0] = true;
                    }
                    if (g > 83) {
                        g--;
                    } else {
                        g = 83;
                        allColorsGood[1] = true;
                    }
                    if (b > 187) {
                        b--;
                    } else {
                        b = 187;
                        allColorsGood[2] = true;
                    }


                    if (allColorsGood[0] && allColorsGood[1] && allColorsGood[2]) {
                        allColorsGood[0] = false;
                        allColorsGood[1] = false;
                        allColorsGood[2] = false;
                        color = 2;

                        if (chooseObjectUsingThisClass == 0)
                            timer = random.nextInt(60) + 20;
                        else if (chooseObjectUsingThisClass == 1)
                            timer = 30;
                    }
                } else {
                    timer--;
                }
            } else if (color == 2) {
                if (timer == 0) {
                    if (r > 245) {
                        r--;
                    } else {
                        r = 245;
                        allColorsGood[0] = true;
                    }
                    if (g < 211) {
                        g++;
                    } else {
                        g = 211;
                        allColorsGood[1] = true;
                    }
                    if (b > 0) {
                        b--;
                    } else {
                        b = 0;
                        allColorsGood[2] = true;
                    }


                    if (allColorsGood[0] && allColorsGood[1] && allColorsGood[2]) {
                        allColorsGood[0] = false;
                        allColorsGood[1] = false;
                        allColorsGood[2] = false;
                        color = 3;
                        if (chooseObjectUsingThisClass == 0)
                            timer = random.nextInt(60) + 20;
                        else if (chooseObjectUsingThisClass == 1)
                            timer = 30;
                    }
                } else {
                    timer--;
                }
            } else if (color == 3) {
                if (timer == 0) {
                    if (r > 8) {
                        r--;
                    } else {
                        r = 8;
                        allColorsGood[0] = true;
                    }
                    if (g < 247) {
                        g++;
                    } else {
                        g = 247;
                        allColorsGood[1] = true;
                    }
                    if (b < 254) {
                        b++;
                    } else {
                        b = 254;
                        allColorsGood[2] = true;
                    }


                    if (allColorsGood[0] && allColorsGood[1] && allColorsGood[2]) {
                        allColorsGood[0] = false;
                        allColorsGood[1] = false;
                        allColorsGood[2] = false;
                        color = 0;

                        if (chooseObjectUsingThisClass == 0)
                            timer = random.nextInt(60) + 20;
                        else if (chooseObjectUsingThisClass == 1)
                            timer = 30;
                    }
                } else {
                    timer--;
                }
            }
        }

    }






    public Color getColor() {
        Color color = new Color(r / 255f, g / 255f, b / 255f, 1);
        return color;
    }

}
