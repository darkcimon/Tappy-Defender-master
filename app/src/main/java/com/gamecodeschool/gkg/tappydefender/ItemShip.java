package com.gamecodeschool.gkg.tappydefender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;
import java.util.StringTokenizer;

import static android.R.transition.move;

/**
 * Created on 9/22/2015.
 */
public class ItemShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    // Detect enemies leaving the screen
    private int maxX;
    private int minX;

    // Spawn enemies within screen bounds
    private int maxY;
    private int minY;

    private boolean moveItem = false;

    public ItemShip(Bitmap bitmap, int screenX, int screenY) {
        this.bitmap = bitmap;

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6) + 5;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @SuppressLint("LongLogTag")
    public void update(int playerSpeed, float distanceRemaining) {
        // every 10km item
        Log.i("distanceRemaining %10000===>", String.valueOf(distanceRemaining % 10000));
        if(distanceRemaining % 10000 == 0 && distanceRemaining!= 0){
            x = maxX;
            moveItem = true;
            Random generator = new Random();
            speed = generator.nextInt(10) + 5;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        if(moveItem &&  x >= minX-bitmap.getWidth()){
            x -= playerSpeed;
            x -= speed;
        }

        if(x < minX-bitmap.getWidth()) {
            moveItem = false;
            this.x = maxX;
        }

    }

    // Used by th eTDView update() methods to make an enemy out of bounds and force a re-spawn
    public void setX(int x){
        this.x = x;
    }
}
