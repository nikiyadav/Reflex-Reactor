package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    ImageView img;
    Animation animationSlideInLeft,animationSlideOutRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        img=(ImageView)findViewById(R.id.imgLogo);
        animationSlideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animationSlideOutRight = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        animationSlideInLeft.setDuration(1500);
        animationSlideOutRight.setDuration(1500);
        animationSlideInLeft.setAnimationListener(animationSlideInLeftListener);
        animationSlideOutRight.setAnimationListener(animationSlideOutRightListener);

        img.startAnimation(animationSlideInLeft);
        img.setVisibility(View.VISIBLE);

        // This method will be executed once the timer is over
        // Start your app main activity
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Mode.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    Animation.AnimationListener animationSlideInLeftListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            img.startAnimation(animationSlideOutRight);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationSlideOutRightListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            img.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };
}
