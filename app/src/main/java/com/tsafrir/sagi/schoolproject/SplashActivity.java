package com.tsafrir.sagi.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable delayedTask;
    private Intent intent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ImageView image = findViewById(R.id.animationRope);
        image.setImageResource(R.drawable.animation_rope);
        AnimationDrawable animation = (AnimationDrawable) image.getDrawable();

        context = this;

        intent = new Intent(context, MainActivity.class);

        // Runs the start animation.
        animation.start();
        handler = new Handler();
        delayedTask = new Runnable(){

            @Override
            public void run(){
            startActivity(intent);
            }
        };

        int delay = getIntent().getIntExtra("delayMillis", (int) (1000 * 2.5));

        handler.postDelayed(delayedTask, delay);
    }

    @Override
    protected void onDestroy() {
        // Removes the task if you exited the app.
        super.onDestroy();
        handler.removeCallbacks(delayedTask);
    }

    @Override
    protected void onPause() {
        // Removes the task if you minimized/paused the app.
        super.onPause();
        handler.removeCallbacks(delayedTask);
    }

    @Override
    protected void onResume() {
        // Resumes the task.
        super.onResume();
        handler.postDelayed(delayedTask, (long) (1000 * 2.5));
    }


}
