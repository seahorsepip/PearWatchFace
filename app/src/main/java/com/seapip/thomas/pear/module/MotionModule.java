package com.seapip.thomas.pear.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;

import com.seapip.thomas.pear.R;

import java.io.InputStream;
import java.util.Random;

public class MotionModule implements Module {
    private Context mContext;
    private Rect mBounds;
    private Movie mMovie;
    private int mDuration;
    private int mPosition;
    private int mStep;
    private boolean mAmbient;
    private Bitmap mBitmap;
    private Bitmap mBitmapScaled;
    private Canvas mCanvas;
    private Random mRandom;
    private int mLastRandom;
    private int[][] mMovies;
    private boolean mBlocked;
    private int mScene;

    /* Paint */
    private Paint mFadeInPaint;

    public MotionModule(Context context, int scene) {
        mContext = context;

        mRandom = new Random();
        mLastRandom = 0;

        int[] jellyfish = new int[5];
        jellyfish[0] = R.drawable.jellyfish_1;
        jellyfish[1] = R.drawable.jellyfish_2;
        jellyfish[2] = R.drawable.jellyfish_3;
        jellyfish[3] = R.drawable.jellyfish_4;
        jellyfish[4] = R.drawable.jellyfish_5;

        int[] flower = new int[5];
        flower[0] = R.drawable.flower1;
        flower[1] = R.drawable.flower2;
        flower[2] = R.drawable.flower3;
        flower[3] = R.drawable.flower4;
        flower[4] = R.drawable.flower5;

        mMovies = new int[2][];
        mMovies[0] = jellyfish;
        mMovies[1] = flower;

        /* Paint */
        mFadeInPaint = new Paint();
        mFadeInPaint.setColor(Color.BLACK);
        mFadeInPaint.setAlpha(255);

        setScene(scene);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mAmbient && !mBlocked) {
            if (mPosition <= mDuration) {
                mMovie.setTime(mPosition);
                if (mDuration - mPosition < 500) {
                    mStep -= 5;
                    if (mStep < 30) {
                        mStep = 30;
                    }
                }
                mPosition += mStep;
                int alpha = mFadeInPaint.getAlpha() - 8;
                if (alpha < 128) {
                    alpha = 128;
                }
                mFadeInPaint.setAlpha(alpha);
                mMovie.draw(mCanvas, 0, 0);
                mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mBounds.width(),
                        mBounds.height(), true);
            } else {
                mMovie = null;
            }
            canvas.drawBitmap(mBitmapScaled, mBounds.left, mBounds.top, null);
        }
        canvas.drawRect(mBounds, mFadeInPaint);
    }

    public void setScene(int scene) {
        mScene = scene;
        start();
    }

    private void start() {
        mBlocked = true;
        mPosition = 0;
        mStep = 60;
        mFadeInPaint.setAlpha(255);
        int random;
        do {
            random = mRandom.nextInt(mMovies[mScene].length);
        } while (random == mLastRandom);
        mLastRandom = random;
        InputStream inputStream = mContext.getResources().openRawResource(+mMovies[mScene][random]);
        mMovie = Movie.decodeStream(inputStream);
        mDuration = mMovie.duration();
        mBitmap = Bitmap.createBitmap(mMovie.width(), mMovie.height(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBlocked = false;
    }

    public void tap(int x, int y) {
        if (mBounds.contains(x, y)) {
            start();
        }
    }

    @Override
    public void setColor(int color) {
    }

    @Override
    public void setAmbient(boolean ambient) {
        mAmbient = ambient;
        if (!ambient) {
            start();
        }
    }

    @Override
    public void setBurnInProtection(boolean burnInProtection) {
    }

    @Override
    public void setLowBitAmbient(boolean lowBitAmbient) {
    }
}
