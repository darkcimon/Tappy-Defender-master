package com.gamecodeschool.gkg.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created on 9/22/2015.
 */
public class PlayerShip {

    private final int Gravity = -12;

    // Stop ship leaving the screen
    private int maxY;
    private int minY;

    // Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;

    private boolean invincible = false;

    private final int INIT_X = 50;
    private final int INIT_Y = 50;
    private int shieldStrength = 2;

    public PlayerShip(Context context, int screenX, int screenY){
        x = INIT_X;
        y = INIT_Y;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        maxY = screenY - bitmap.getHeight();
        minY = 0;

    }

    public void update() {
        // Are we boosting?
        if(boosting){
            // Speed up
            speed += 2;
        }
        else {
            // Slow down
            speed -= 5;
        }

        // Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        // Never stop completely
        if(speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        // move the ship up or down
        y -= speed + Gravity;

        // But don't let the ship stray off screen
        if(y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }

    }

    //Getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDefaultValues() {
        this.x = INIT_X;
        this.y = INIT_Y;
        this.shieldStrength = 2;
    }

    public int getSpeed() {
        return speed;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public void addShieldStrength() {
        this.shieldStrength++;
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoostring(){
        boosting = false;
    }

    public void reduceShieldStrength() {
        if(!invincible){
            shieldStrength--;
        }
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
}
