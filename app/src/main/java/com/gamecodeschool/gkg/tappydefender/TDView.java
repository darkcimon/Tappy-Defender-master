package com.gamecodeschool.gkg.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import static com.gamecodeschool.gkg.tappydefender.R.drawable.enemy;

/**
 * Created on 9/21/2015.
 */

public class TDView extends SurfaceView implements Runnable
{
    volatile boolean playing;
    Thread gameThread = null;
    private Context context;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private volatile boolean gameEnded;

    private int screenX;
    private int screenY;

    // Game Objects
    private PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;
    private List<EnemyShip> enemyList = new ArrayList<EnemyShip>();
    private final int INIT_ES_X = -100;
    

    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        screenX = x;
        screenY = y;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

//        // Initialize our player ship
//        player = new PlayerShip(context, x, y);
//
//        enemy1 = new EnemyShip(context, x, y);
//        enemy2 = new EnemyShip(context, x, y);
//        enemy3 = new EnemyShip(context, x, y);
//
//        int numSpecs = 40;
//
//        for (int i = 0; i < numSpecs; i++)
//        {
//            // Where will the dust spawn?
//            SpaceDust spec = new SpaceDust(x, y);
//            dustList.add(spec);
//        }

        initGame();
    }

    @Override
    public void run(){
        while (playing){
            update();
            draw();
            control();
        }
    }

    private void update(){
        Log.i("== INITIAL UPDATE ==", "gameEnded? =="+String.valueOf(gameEnded)+", Shield length=="+String.valueOf(player.getShieldStrength()));
        // If images in excess of 100 pixels wide then increase -100 accordingly
        for(EnemyShip es : enemyList){
            if(CollisionChecker.isCollisionDetected(player.getBitmap(),player.getX(),player.getY(), es.getBitmap(),es.getX(),es.getY())){
                player.reduceShieldStrength();
                es.setX(INIT_ES_X);
            }
        }

        if(player.getShieldStrength() < 0){
            // game over do something
            gameEnded = true;
        }

        Log.i("== SECOND UPDATE ==", "gameEnded? =="+String.valueOf(gameEnded)+", Shield length=="+String.valueOf(player.getShieldStrength()));
        // Update the player
        player.update();

        // Update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        for(SpaceDust sd: dustList)
        {
            sd.update(player.getSpeed());
        }

        if(!gameEnded){
            // subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();

            // how long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        // Completed the game!
        if(distanceRemaining < 0){
            // check for new fastest time
            if(timeTaken < fastestTime){
                fastestTime = timeTaken;
            }

            // avoid ugly negative numbers in HUD
            distanceRemaining = 0;

            // end the game
            gameEnded = true;
        }
    }

    private void draw(){
        if(ourHolder.getSurface().isValid()) {
            // First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // For debugging
            // Switch to white pixels
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the hit boxes
//            canvas.drawRect(
//                player.getHitBox().left,
//                player.getHitBox().top,
//                player.getHitBox().right,
//                player.getHitBox().bottom,
//                paint
//            );
//
//            canvas.drawRect(
//                enemy1.getHitBox().left,
//                enemy1.getHitBox().top,
//                enemy1.getHitBox().right,
//                enemy1.getHitBox().bottom,
//                paint
//            );
//
//            canvas.drawRect(
//                enemy2.getHitBox().left,
//                enemy2.getHitBox().top,
//                enemy2.getHitBox().right,
//                enemy2.getHitBox().bottom,
//                paint
//            );
//
//            canvas.drawRect(
//                enemy3.getHitBox().left,
//                enemy3.getHitBox().top,
//                enemy3.getHitBox().right,
//                enemy3.getHitBox().bottom,
//                paint
//            );

            // White specs of dust
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the dust from the arrayList
            for (SpaceDust sd: dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );

            canvas.drawBitmap(
                    enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(),
                    paint
            );

            canvas.drawBitmap(
                    enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(),
                    paint
            );

            canvas.drawBitmap(
                    enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(),
                    paint
            );

            if(!gameEnded)
            {
                // Draw the hud (Heads Up Display)
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Time:" + timeTaken + "s", screenX/2, 20, paint);
                canvas.drawText("Distance:" + distanceRemaining/1000 + " KM", screenX/3, screenY - 20, paint);
                canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 + " MPS", (screenX/3) * 2, screenY - 20, paint);
            }
            else
            {
                // Show the pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + fastestTime + "s", screenX/2, 160, paint);
                canvas.drawText("Time:" + timeTaken + "s", screenX/2, 200, paint);
                canvas.drawText("Distance remaining:" + distanceRemaining/1000 + " KM", screenX/2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 350, paint);
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void initGame(){
        // Initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        enemyList.add(enemy1);
        enemyList.add(enemy2);
        enemyList.add(enemy3);

        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++){
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        initShowInfo();
    }

    private void initShowInfo(){
        // Reset time and distance
        distanceRemaining = 10000;// 10km
        timeTaken = 0;

        // Get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;
    }

    private void startGame(){
        // Initialize game objects
        player.setDefaultValues();
        for(EnemyShip es : enemyList){
            es.setX(INIT_ES_X);
        }

        initShowInfo();
    }

    private void control(){
        try {
            // pause thread for 17 ms
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {
            // do nothing
        }
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause(){
        playing = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {
            // do something
        }
    }

    // Make a new thread and start it
    // Execution moves to the R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Has the player lifted their finger up?
            case  MotionEvent.ACTION_UP:
                player.stopBoostring();
                break;
            // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                // if currently on the pause screen, start a new game
                if(gameEnded){
                    startGame();
                }
                break;
        }
        return true;
    }
}
